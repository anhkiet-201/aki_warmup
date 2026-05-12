param(
    [string]$method,
    [string[]]$captions = @(),
    [string]$deviceFile
)

# Lấy đường dẫn thư mục chứa script này
$PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectRoot = (Get-Item "$PSScriptRoot\..").FullName

# Import tiện ích
. "$PSScriptRoot\utils.ps1"
Set-Utf8Encoding

Write-Host "==================================================" -ForegroundColor Green
Write-Host "Building APK..." -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green

# Chuyển về gốc dự án để build
cd $projectRoot
./gradlew assembleDebug assembleAndroidTest

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build failed." -ForegroundColor Red
    exit $LASTEXITCODE
}

$devices = Get-AdbDevices -deviceFile $deviceFile

if ($devices.Count -eq 0) {
    Write-Host "No devices found." -ForegroundColor Yellow
    exit 1
}

Write-Host "Found $($devices.Count) devices. Starting parallel tests..." -ForegroundColor Green

$jobs = @()
$i = 0

foreach ($device in $devices) {
    $caption = "#ttnhr Tuyển dụng việc làm Bình Dương"
    if ($captions.Count -gt 0) {
        $caption = $captions[$i % $captions.Count]
    }
    $i++
    
    Write-Host "Starting test on device: $device" -ForegroundColor Cyan
    
    $job = Start-Job -ScriptBlock {
        param($d, $workingDir, $method, $caption)
        cd $workingDir
        
        $output = @()
        $output += "--- on $d ---"
        
        # Cài đặt APK
        adb -s $d shell settings put global package_verifier_user_consent -1
        $output += "Installing APK..."
        $installDebug = adb -s $d install -r app/build/outputs/apk/debug/app-debug.apk 2>&1
        $output += $installDebug
        $installTest = adb -s $d install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk 2>&1
        $output += $installTest
        
        $installFailed = ($installDebug -match "Failure") -or ($installTest -match "Failure")
        
        if ($installFailed) {
            $reason = "Install failed"
            return [PSCustomObject]@{ Device = $d; Status = "FAILURE"; Reason = $reason; Output = $output }
        }
        
        # Chạy test
        $output += "Running..."
        
        $targetClass = "com.aki.akiwarmup.AkiFrameworkTest"
        if ($method) {
            $targetClass = "$targetClass#$method"
        }
        # Dừng các package uiautomator khác để tránh xung đột
        adb -s $d shell am force-stop com.genfarmer.uiautomator.test
        adb -s $d shell am force-stop com.genfarmer.uiautomator
        
        $testOutput = adb -s $d shell am instrument -w -r -e class $targetClass -e caption "'$caption'" com.aki.akiwarmup.test/androidx.test.runner.AndroidJUnitRunner 2>&1
        $output += $testOutput
        
        $testSuccess = $testOutput -match "OK \("
        
        if ($testSuccess) {
            return [PSCustomObject]@{ Device = $d; Status = "OK"; Reason = ""; Output = $output }
        } else {
            $reason = "Test failed"
            foreach ($line in $testOutput) {
                # Kiểm tra kết quả từ finish() trong app
                if ($line -match "INSTRUMENTATION_RESULT: reason=(.*)") {
                    $reason = $Matches[1].Trim()
                    break
                }
                # Kiểm tra reason từ sendStatus (nếu có dùng)
                if ($line -match "INSTRUMENTATION_STATUS: reason=(.*)") {
                    $reason = $Matches[1].Trim()
                    break
                }
                if ($line -match "Failure in") {
                    $reason = $line.Trim()
                    break
                }
                if ($line -match "INSTRUMENTATION_RESULT: shortMsg=(.*)") {
                    $reason = $Matches[1].Trim()
                    break
                }
            }
            $reasonNoSpaces = $reason -replace ' ', '_'
            adb -s $d shell "am start -n com.aki.akiwarmup/.MainActivity --es message '$reasonNoSpaces' -f 0x10000000"
            
            return [PSCustomObject]@{ Device = $d; Status = "FAILURE"; Reason = $reason; Output = $output }
        }
    } -ArgumentList $device, $projectRoot, $method, $caption
    
    $jobs += $job
}

Write-Host "`nTriggered tests on all devices." -ForegroundColor Green
Write-Host "Waiting for devices to complete..." -ForegroundColor Yellow

$completed = $false
try {
    $jobs | Wait-Job | Out-Null
    $completed = $true
    
    Write-Host "`n================ RESULTS ================" -ForegroundColor Green
    
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
                Write-Host "  -> Details: $logFile" -ForegroundColor Yellow
            }
        }
    }
} finally {
    if (-not $completed) {
        Write-Host "`n Stopped." -ForegroundColor Yellow
        # Gọi stop_tests.ps1 và truyền deviceFile nếu có
        . "$PSScriptRoot\stop_tests.ps1" -deviceFile $deviceFile
    }
}
