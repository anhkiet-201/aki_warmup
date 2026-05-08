# Script chạy AkiFrameworkTest trên tất cả thiết bị ADB kết nối
# Chế độ: Chạy song song (Parallel) dùng PowerShell Jobs
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "==================================================" -ForegroundColor Green
Write-Host "Đang build APK (Debug và AndroidTest)..." -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green

./gradlew assembleDebug assembleAndroidTest

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build thất bại. Vui lòng kiểm tra lại lỗi build." -ForegroundColor Red
    exit $LASTEXITCODE
}

# Lấy danh sách thiết bị đang kết nối
$devices = adb devices | Where-Object { $_ -match "\bdevice\b" } | ForEach-Object { $_.Split("`t")[0] }

if ($devices.Count -eq 0) {
    Write-Host "Không tìm thấy thiết bị ADB nào đang kết nối!" -ForegroundColor Yellow
    exit 1
}

Write-Host "Tìm thấy $($devices.Count) thiết bị. Bắt đầu kích hoạt test song song..." -ForegroundColor Green

$pwd = Get-Location
$jobs = @()

foreach ($device in $devices) {
    Write-Host "Đang khởi tạo job cho thiết bị: $device" -ForegroundColor Cyan
    
    $job = Start-Job -ScriptBlock {
        param($d, $workingDir)
        # Chuyển về thư mục dự án
        cd $workingDir
        
        $logFile = "test_log_$($d.Replace(':', '_')).txt"
        "--- Bắt đầu test trên $d ---" | Out-File $logFile
        
        # Cài đặt APK
        "Đang cài đặt APK..." | Out-File $logFile -Append
        adb -s $d install -r app/build/outputs/apk/debug/app-debug.apk >> $logFile 2>&1
        adb -s $d install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk >> $logFile 2>&1
        
        # Chạy test
        "Đang chạy test..." | Out-File $logFile -Append
        adb -s $d shell am instrument -w -e class com.aki.akiwarmup.AkiFrameworkTest com.aki.akiwarmup.test/androidx.test.runner.AndroidJUnitRunner >> $logFile 2>&1
        
        "--- Hoàn thành test trên $d ---" | Out-File $logFile -Append
    } -ArgumentList $device, $pwd
    
    $jobs += $job
}

Write-Host "`nĐã kích hoạt test trên tất cả các thiết bị." -ForegroundColor Green
Write-Host "Đang chờ các thiết bị hoàn thành (Bạn có thể xem file log 'test_log_*.txt' để theo dõi tiến độ)..." -ForegroundColor Yellow

$completed = $false
try {
    # Chờ tất cả các job hoàn thành
    $jobs | Wait-Job | Out-Null
    $completed = $true
} finally {
    if (-not $completed) {
        Write-Host "`n[!] Script bị dừng đột ngột. Đang dừng ứng dụng trên các thiết bị..." -ForegroundColor Yellow
        foreach ($device in $devices) {
            adb -s $device shell am force-stop com.aki.akiwarmup
        }
    }
    # Dừng và xóa các job
    $jobs | Stop-Job -ErrorAction SilentlyContinue
    $jobs | Remove-Job -ErrorAction SilentlyContinue
}

if ($completed) {
    Write-Host "`n==================================================" -ForegroundColor Green
    Write-Host "Tất cả các thiết bị đã hoàn thành quá trình test." -ForegroundColor Green
    Write-Host "Vui lòng kiểm tra các file log 'test_log_<IP>_5555.txt' để xem kết quả chi tiết." -ForegroundColor Green
    Write-Host "==================================================" -ForegroundColor Green
}
