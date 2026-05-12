param(
    [string]$method,
    [string[]]$captions = @()
)

# Lấy đường dẫn thư mục chứa script này
$PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectRoot = (Get-Item "$PSScriptRoot\..").FullName

# Import tiện ích
. "$PSScriptRoot\utils.ps1"
Set-Utf8Encoding

Write-Host "==================================================" -ForegroundColor Green
Write-Host "Đang build APK (Debug và AndroidTest)..." -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green

# Chuyển về gốc dự án để build
cd $projectRoot
./gradlew assembleDebug assembleAndroidTest

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build thất bại. Vui lòng kiểm tra lại lỗi build." -ForegroundColor Red
    exit $LASTEXITCODE
}

$devices = Get-AdbDevices

if ($devices.Count -eq 0) {
    Write-Host "Không tìm thấy thiết bị ADB nào đang kết nối!" -ForegroundColor Yellow
    exit 1
}

Write-Host "Tìm thấy $($devices.Count) thiết bị. Bắt đầu kích hoạt test song song..." -ForegroundColor Green

$jobs = @()
$i = 0

foreach ($device in $devices) {
    $caption = "Xin chào"
    if ($captions.Count -gt 0) {
        $caption = $captions[$i % $captions.Count]
    }
    $i++
    
    Write-Host "Đang khởi tạo job cho thiết bị: $device" -ForegroundColor Cyan
    
    $job = Start-Job -ScriptBlock {
        param($d, $workingDir, $method, $caption)
        cd $workingDir
        
        $output = @()
        $output += "--- Bắt đầu test trên $d ---"
        
        # Cài đặt APK
        adb -s $d shell settings put global package_verifier_user_consent -1
        $output += "Đang cài đặt APK..."
        $installDebug = adb -s $d install -r app/build/outputs/apk/debug/app-debug.apk 2>&1
        $output += $installDebug
        $installTest = adb -s $d install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk 2>&1
        $output += $installTest
        
        $installFailed = ($installDebug -match "Failure") -or ($installTest -match "Failure")
        
        if ($installFailed) {
            $reason = "Cài đặt APK thất bại"
            return [PSCustomObject]@{ Device = $d; Status = "FAILURE"; Reason = $reason; Output = $output }
        }
        
        # Chạy test
        $output += "Đang chạy test..."
        
        $targetClass = "com.aki.akiwarmup.AkiFrameworkTest"
        if ($method) {
            $targetClass = "$targetClass#$method"
        }
        
        $testOutput = adb -s $d shell am instrument -w -e class $targetClass -e caption "'$caption'" com.aki.akiwarmup.test/androidx.test.runner.AndroidJUnitRunner 2>&1
        $output += $testOutput
        
        $testSuccess = $testOutput -match "OK \("
        
        if ($testSuccess) {
            return [PSCustomObject]@{ Device = $d; Status = "OK"; Reason = ""; Output = $output }
        } else {
            $reason = "Test thất bại"
            foreach ($line in $testOutput) {
                if ($line -match "Failure in") {
                    $reason = $line.Trim()
                    break
                }
                if ($line -match "INSTRUMENTATION_RESULT: shortMsg=(.*)") {
                    $reason = $Matches[1].Trim()
                    break
                }
            }
            return [PSCustomObject]@{ Device = $d; Status = "FAILURE"; Reason = $reason; Output = $output }
        }
    } -ArgumentList $device, $projectRoot, $method, $caption
    
    $jobs += $job
}

Write-Host "`nĐã kích hoạt test trên tất cả các thiết bị." -ForegroundColor Green
Write-Host "Đang chờ các thiết bị hoàn thành..." -ForegroundColor Yellow

$completed = $false
try {
    $jobs | Wait-Job | Out-Null
    $completed = $true
    
    Write-Host "`n================ KẾT QUẢ TEST ================" -ForegroundColor Green
    
    foreach ($job in $jobs) {
        $results = Receive-Job -Job $job
        $result = $results | Where-Object { $_.Status -ne $null } | Select-Object -First 1
        
        if ($result) {
            $d = $result.Device
            $status = $result.Status
            $reason = $result.Reason
            $output = $result.Output
            
            if ($status -eq "OK") {
                Write-Host "[$d]: OK" -ForegroundColor Green
            } else {
                Write-Host "[$d]: FAILURE - $reason" -ForegroundColor Red
                
                $logFile = "$PSScriptRoot\test_log_$($d.Replace(':', '_')).txt"
                $output | Out-File $logFile
                Write-Host "  -> Chi tiết lỗi được lưu tại: $logFile" -ForegroundColor Yellow
            }
        }
    }
} finally {
    if (-not $completed) {
        Write-Host "`n[!] Script bị dừng đột ngột. Đang dọn dẹp..." -ForegroundColor Yellow
        # Có thể gọi stop_tests.ps1 ở đây
        . "$PSScriptRoot\stop_tests.ps1"
    }
}

