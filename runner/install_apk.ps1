param(
    [string]$apkPath,
    [string]$testApkPath,
    [string]$deviceFile,
    [switch]$onlyMain, # Chỉ cài đặt main APK, không cài Test APK
    [switch]$noBuild   # Bỏ qua bước build Gradle
)

# Lấy đường dẫn thư mục chứa script này
$PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectRoot = (Get-Item "$PSScriptRoot\..").FullName

# Import tiện ích
. "$PSScriptRoot\utils.ps1"
Set-Utf8Encoding

Write-Host "==================================================" -ForegroundColor Green
Write-Host "APK Installation Tool" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green

# Thực hiện build nếu không có cờ -noBuild và không cung cấp apkPath cụ thể
if (-not $noBuild -and -not $apkPath) {
    Write-Host "Building project..." -ForegroundColor Green
    cd $projectRoot
    ./gradlew assembleDebug assembleAndroidTest
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build failed. Aborting installation." -ForegroundColor Red
        exit $LASTEXITCODE
    }
}

# Xác định đường dẫn APK nếu không cung cấp
if (-not $apkPath) {
    $apkPath = "$projectRoot\app\build\outputs\apk\debug\app-debug.apk"
    Write-Host "Using default main APK: $apkPath" -ForegroundColor Gray
}

if (-not $testApkPath -and -not $onlyMain) {
    $testApkPath = "$projectRoot\app\build\outputs\apk\androidTest\debug\app-debug-androidTest.apk"
    Write-Host "Using default test APK: $testApkPath" -ForegroundColor Gray
}

# Kiểm tra file tồn tại
if (-not (Test-Path $apkPath)) {
    Write-Host "Main APK not found at: $apkPath" -ForegroundColor Red
    if (-not $apkPath -match "app-debug.apk") {
        Write-Host "Please build the project or provide a valid path." -ForegroundColor Yellow
    }
    exit 1
}

$devices = Get-AdbDevices -deviceFile $deviceFile

if ($devices.Count -eq 0) {
    Write-Host "No devices found. Please connect devices or check deviceFile." -ForegroundColor Yellow
    exit 1
}

Write-Host "Found $($devices.Count) devices. Starting parallel installation..." -ForegroundColor Green

$jobs = @()

foreach ($device in $devices) {
    Write-Host "Queuing installation for device: $device" -ForegroundColor Cyan
    
    $job = Start-Job -ScriptBlock {
        param($d, $mainApk, $testApk, $skipTest)
        
        $output = @()
        $output += "--- Installing on $d ---"
        
        # Tắt kiểm tra package để cài đặt mượt hơn
        adb -s $d shell settings put global package_verifier_user_consent -1
        
        # Cài đặt Main APK
        $output += "Installing Main APK..."
        $installMain = adb -s $d install -r $mainApk 2>&1
        $output += $installMain
        
        $success = $true
        if ($installMain -match "Failure") {
            $success = $false
        }
        
        # Cài đặt Test APK nếu cần
        if ($success -and -not $skipTest -and $testApk -and (Test-Path $testApk)) {
            $output += "Installing Test APK..."
            $installTest = adb -s $d install -r $testApk 2>&1
            $output += $installTest
            if ($installTest -match "Failure") {
                $success = $false
            }
        }
        
        if ($success) {
            return [PSCustomObject]@{ Device = $d; Status = "OK"; Output = $output }
        } else {
            return [PSCustomObject]@{ Device = $d; Status = "FAILURE"; Output = $output }
        }
    } -ArgumentList $device, $apkPath, $testApkPath, $onlyMain
    
    $jobs += $job
}

Write-Host "`nWaiting for installation to complete..." -ForegroundColor Yellow

$jobs | Wait-Job | Out-Null

Write-Host "`n================ INSTALLATION RESULTS ================" -ForegroundColor Green

$failedCount = 0
foreach ($job in $jobs) {
    $results = Receive-Job -Job $job
    $result = $results | Where-Object { $_.Status -ne $null } | Select-Object -First 1
    
    if ($result) {
        $d = $result.Device
        $status = $result.Status
        
        if ($status -eq "OK") {
            Write-Host "[$d]: SUCCESS" -ForegroundColor Green
        } else {
            Write-Host "[$d]: FAILED" -ForegroundColor Red
            $failedCount++
            # Hiển thị chi tiết lỗi nếu thất bại
            foreach ($line in $result.Output) {
                if ($line -match "Failure") {
                    Write-Host "  -> $line" -ForegroundColor Yellow
                }
            }
        }
    }
}

Write-Host "`nDone. (Total: $($devices.Count), Failed: $failedCount)" -ForegroundColor Green
