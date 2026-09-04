#!/usr/bin/env bash
# ==============================================================================
# Script cài đặt APK (Main APK & Test APK) song song cho macOS / Linux
# Tự động gỡ app cũ nếu sai Signature/Version và kiểm soát số luồng song song
# ==============================================================================

set -eo pipefail

# Mã màu ANSI
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;90m'
NC='\033[0m' # No Color

# Xác định đường dẫn thư mục
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Giá trị mặc định cho tham số
APK_PATH=""
TEST_APK_PATH=""
DEVICE_FILE=""
ONLY_MAIN=false
NO_BUILD=false
MAX_PARALLEL=15

# Hàm hiển thị hướng dẫn sử dụng
show_help() {
    echo -e "${CYAN}Sử dụng:${NC} $0 [options]"
    echo ""
    echo -e "${YELLOW}Tùy chọn:${NC}"
    echo "  -a, --apk <path>          Đường dẫn đến file Main APK (app-debug.apk)"
    echo "  -t, --test-apk <path>     Đường dẫn đến file Test APK (app-debug-androidTest.apk)"
    echo "  -d, --device-file <path>  Đường dẫn đến file chứa danh sách thiết bị (mỗi dòng 1 serial/IP)"
    echo "  -p, --max-parallel <num>  Số thiết bị tối đa cài đặt đồng thời trong 1 đợt (mặc định: 15)"
    echo "      --only-main           Chỉ cài đặt Main APK, bỏ qua Test APK"
    echo "      --no-build            Bỏ qua bước Gradle build trước khi cài đặt"
    echo "  -h, --help                Hiển thị hướng dẫn sử dụng này"
    echo ""
    echo -e "${YELLOW}Ví dụ:${NC}"
    echo "  $0"
    echo "  $0 --no-build -p 20"
    echo "  $0 --only-main -d devices.txt"
    echo "  $0 -a /path/to/app-debug.apk -t /path/to/app-androidTest.apk"
    exit 0
}

# Phân tích tham số dòng lệnh
while [[ $# -gt 0 ]]; do
    case "$1" in
        -a|--apk)
            APK_PATH="$2"
            shift 2
            ;;
        -t|--test-apk)
            TEST_APK_PATH="$2"
            shift 2
            ;;
        -d|--device-file)
            DEVICE_FILE="$2"
            shift 2
            ;;
        -p|--max-parallel)
            MAX_PARALLEL="$2"
            shift 2
            ;;
        --only-main)
            ONLY_MAIN=true
            shift
            ;;
        --no-build)
            NO_BUILD=true
            shift
            ;;
        -h|--help)
            show_help
            ;;
        *)
            echo -e "${RED}Tham số không hợp lệ: $1${NC}"
            echo "Dùng '$0 --help' để xem hướng dẫn."
            exit 1
            ;;
    esac
done

echo -e "${GREEN}==================================================${NC}"
echo -e "${GREEN}APK Installation Tool (macOS / Linux)${NC}"
echo -e "${GREEN}==================================================${NC}"

# Thực hiện build nếu không có cờ --no-build và không cung cấp apkPath cụ thể
if [ "$NO_BUILD" = false ] && [ -z "$APK_PATH" ]; then
    echo -e "${GREEN}Building project...${NC}"
    (cd "$PROJECT_ROOT" && ./gradlew assembleDebug assembleAndroidTest)
    if [ $? -ne 0 ]; then
        echo -e "${RED}Build failed. Aborting installation.${NC}"
        exit 1
    fi
fi

# Xác định đường dẫn APK nếu không cung cấp
if [ -z "$APK_PATH" ]; then
    APK_PATH="$PROJECT_ROOT/app/build/outputs/apk/debug/app-debug.apk"
    echo -e "${GRAY}Using default main APK: $APK_PATH${NC}"
fi

if [ -z "$TEST_APK_PATH" ] && [ "$ONLY_MAIN" = false ]; then
    TEST_APK_PATH="$PROJECT_ROOT/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"
    echo -e "${GRAY}Using default test APK: $TEST_APK_PATH${NC}"
fi

# Kiểm tra file APK tồn tại
if [ ! -f "$APK_PATH" ]; then
    echo -e "${RED}Main APK not found at: $APK_PATH${NC}"
    echo -e "${YELLOW}Please build the project or provide a valid path.${NC}"
    exit 1
fi

# Lấy danh sách thiết bị
DEVICES=()
if [ -n "$DEVICE_FILE" ] && [ -f "$DEVICE_FILE" ]; then
    while IFS= read -r line || [ -n "$line" ]; do
        line="$(echo "$line" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')"
        if [ -n "$line" ] && [[ ! "$line" =~ ^# ]]; then
            DEVICES+=("$line")
        fi
    done < "$DEVICE_FILE"
else
    while IFS= read -r line; do
        if [ -n "$line" ]; then
            DEVICES+=("$line")
        fi
    done < <(adb devices | awk '$2=="device" {print $1}')
fi

if [ ${#DEVICES[@]} -eq 0 ]; then
    echo -e "${YELLOW}No devices found. Please connect devices or check deviceFile.${NC}"
    exit 1
fi

echo -e "${GREEN}Found ${#DEVICES[@]} devices (Batch size: $MAX_PARALLEL). Starting installation...${NC}"

# Tạo thư mục tạm để chứa log của các subshell
TEMP_DIR=$(mktemp -d /tmp/aki_install_XXXXXX)
trap 'rm -rf "$TEMP_DIR"' EXIT

# Hàm cài đặt chi tiết trên 1 thiết bị
install_on_device() {
    local dev="$1"
    local LOG_FILE="$TEMP_DIR/$dev.log"
    local STATUS_FILE="$TEMP_DIR/$dev.status"
    
    echo "--- Installing on $dev ---" > "$LOG_FILE"
    
    # Tắt kiểm tra package để cài đặt mượt hơn
    adb -s "$dev" shell settings put global package_verifier_user_consent -1 >> "$LOG_FILE" 2>&1 || true
    
    install_apk_file() {
        local apk_file="$1"
        local pkg_name="$2"
        local is_main="$3"
        
        echo "Installing $(basename "$apk_file")..." >> "$LOG_FILE"
        local out
        out=$(adb -s "$dev" install -r "$apk_file" 2>&1)
        echo "$out" >> "$LOG_FILE"
        
        # Nếu lỗi signature / incompatible / downgrade -> gỡ app cũ rồi cài lại
        if echo "$out" | grep -qiE "INSTALL_FAILED_UPDATE_INCOMPATIBLE|INSTALL_FAILED_VERSION_DOWNGRADE|signatures do not match"; then
            echo "Detected signature/version mismatch on $dev. Auto-uninstalling $pkg_name..." >> "$LOG_FILE"
            adb -s "$dev" uninstall "$pkg_name" >> "$LOG_FILE" 2>&1 || true
            if [ "$is_main" = true ]; then
                adb -s "$dev" uninstall "${pkg_name}.test" >> "$LOG_FILE" 2>&1 || true
            fi
            echo "Retrying installation of $(basename "$apk_file")..." >> "$LOG_FILE"
            out=$(adb -s "$dev" install -r "$apk_file" 2>&1)
            echo "$out" >> "$LOG_FILE"
        fi
        
        if echo "$out" | grep -qiE "Failure|error:"; then
            return 1
        fi
        return 0
    }
    
    local success=true
    
    # Cài đặt Main APK
    if ! install_apk_file "$APK_PATH" "com.aki.akiwarmup" true; then
        success=false
    fi
    
    # Cài đặt Test APK nếu cần
    if [ "$success" = true ] && [ "$ONLY_MAIN" = false ] && [ -n "$TEST_APK_PATH" ] && [ -f "$TEST_APK_PATH" ]; then
        if ! install_apk_file "$TEST_APK_PATH" "com.aki.akiwarmup.test" false; then
            success=false
        fi
    fi
    
    if [ "$success" = true ]; then
        echo "OK" > "$STATUS_FILE"
    else
        echo "FAILURE" > "$STATUS_FILE"
    fi
}

# Chạy cài đặt theo từng Batch MAX_PARALLEL thiết bị
TOTAL_DEVICES=${#DEVICES[@]}
TOTAL_BATCHES=$(( (TOTAL_DEVICES + MAX_PARALLEL - 1) / MAX_PARALLEL ))

for ((i = 0; i < TOTAL_DEVICES; i += MAX_PARALLEL)); do
    BATCH=("${DEVICES[@]:i:MAX_PARALLEL}")
    BATCH_NUM=$(( i / MAX_PARALLEL + 1 ))
    
    echo -e "\n${YELLOW}▶ Processing batch $BATCH_NUM/$TOTAL_BATCHES (${#BATCH[@]} devices)...${NC}"
    
    PIDS=()
    for dev in "${BATCH[@]}"; do
        echo -e "${CYAN}  -> Queuing $dev${NC}"
        install_on_device "$dev" &
        PIDS+=($!)
    done
    
    for pid in "${PIDS[@]}"; do
        wait "$pid" 2>/dev/null || true
    done
done

echo -e "\n${GREEN}================ INSTALLATION RESULTS ================${NC}"

FAILED_COUNT=0
SUCCESS_COUNT=0

for dev in "${DEVICES[@]}"; do
    STATUS="FAILURE"
    if [ -f "$TEMP_DIR/$dev.status" ]; then
        STATUS=$(cat "$TEMP_DIR/$dev.status")
    fi
    
    if [ "$STATUS" = "OK" ]; then
        echo -e "${GREEN}[$dev]: SUCCESS${NC}"
        ((SUCCESS_COUNT++))
    else
        echo -e "${RED}[$dev]: FAILED${NC}"
        ((FAILED_COUNT++))
        if [ -f "$TEMP_DIR/$dev.log" ]; then
            grep -iE "Failure|error:" "$TEMP_DIR/$dev.log" | while IFS= read -r line; do
                echo -e "  ${YELLOW}-> $line${NC}"
            done
        fi
    fi
done

echo -e "\n${GREEN}Done. (Total: $TOTAL_DEVICES, Success: $SUCCESS_COUNT, Failed: $FAILED_COUNT)${NC}"

if [ "$FAILED_COUNT" -gt 0 ]; then
    exit 1
fi
