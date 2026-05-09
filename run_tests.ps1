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
        cd $workingDir
        
        $output = @()
        $output += "--- Bắt đầu test trên $d ---"
        
        # Cài đặt APK
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
        $testOutput = adb -s $d shell am instrument -w -e class com.aki.akiwarmup.AkiFrameworkTest com.aki.akiwarmup.test/androidx.test.runner.AndroidJUnitRunner 2>&1
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
                
                # Option B: Lưu log nếu fail
                $logFile = "test_log_$($d.Replace(':', '_')).txt"
                $output | Out-File $logFile
                Write-Host "  -> Chi tiết lỗi được lưu tại: $logFile" -ForegroundColor Yellow
            }
        } else {
            Write-Host "Không nhận được kết quả từ job của thiết bị." -ForegroundColor Yellow
        }
    }
} finally {
    if (-not $completed) {
        Write-Host "`n[!] Script bị dừng đột ngột. Đang dừng ứng dụng trên các thiết bị..." -ForegroundColor Yellow
        # Lấy danh sách thiết bị động như stop_tests.ps1
        $currentDevices = adb devices | Where-Object { $_ -match "\bdevice\b" } | ForEach-Object { $_.Split("`t")[0] }
        foreach ($device in $currentDevices) {
            adb -s $device shell am force-stop com.aki.akiwarmup
        }
    }
    # Dừng và xóa tất cả các job như stop_tests.ps1
    $allJobs = Get-Job
    if ($allJobs.Count -gt 0) {
        Write-Host "Đang dừng các background jobs..." -ForegroundColor Gray
        $allJobs | Stop-Job -ErrorAction SilentlyContinue
        $allJobs | Remove-Job -ErrorAction SilentlyContinue
    }
}

if ($completed) {
    Write-Host "`n==================================================" -ForegroundColor Green
    Write-Host "Tất cả các thiết bị đã hoàn thành quá trình test." -ForegroundColor Green
    Write-Host "Vui lòng kiểm tra các file log tương ứng nếu có thiết bị thất bại." -ForegroundColor Green
    Write-Host "==================================================" -ForegroundColor Green
}
