param(
    [string]$method,
    [string[]]$captions = @(),
    [string]$deviceFile
)

# Láº¥y Ä‘Æ°á»ng dáº«n thÆ° má»¥c chá»©a script nÃ y
$PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectRoot = (Get-Item "$PSScriptRoot\..").FullName

# Import tiá»‡n Ã­ch
. "$PSScriptRoot\utils.ps1"
Set-Utf8Encoding

Write-Host "==================================================" -ForegroundColor Green
Write-Host "Äang build APK (Debug vÃ  AndroidTest)..." -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green

# Chuyá»ƒn vá» gá»‘c dá»± Ã¡n Ä‘á»ƒ build
cd $projectRoot
./gradlew assembleDebug assembleAndroidTest

if ($LASTEXITCODE -ne 0) {
    Write-Host "Build tháº¥t báº¡i. Vui lÃ²ng kiá»ƒm tra láº¡i lá»—i build." -ForegroundColor Red
    exit $LASTEXITCODE
}

$devices = Get-AdbDevices -deviceFile $deviceFile

if ($devices.Count -eq 0) {
    Write-Host "KhÃ´ng tÃ¬m tháº¥y thiáº¿t bá»‹ ADB nÃ o Ä‘ang káº¿t ná»‘i!" -ForegroundColor Yellow
    exit 1
}

Write-Host "TÃ¬m tháº¥y $($devices.Count) thiáº¿t bá»‹. Báº¯t Ä‘áº§u kÃ­ch hoáº¡t test song song..." -ForegroundColor Green

$jobs = @()
$i = 0

foreach ($device in $devices) {
    $caption = "Xin chÃ o"
    if ($captions.Count -gt 0) {
        $caption = $captions[$i % $captions.Count]
    }
    $i++
    
    Write-Host "Äang khá»Ÿi táº¡o job cho thiáº¿t bá»‹: $device" -ForegroundColor Cyan
    
    $job = Start-Job -ScriptBlock {
        param($d, $workingDir, $method, $caption)
        cd $workingDir
        
        $output = @()
        $output += "--- Báº¯t Ä‘áº§u test trÃªn $d ---"
        
        # CÃ i Ä‘áº·t APK
        adb -s $d shell settings put global package_verifier_user_consent -1
        $output += "Äang cÃ i Ä‘áº·t APK..."
        $installDebug = adb -s $d install -r app/build/outputs/apk/debug/app-debug.apk 2>&1
        $output += $installDebug
        $installTest = adb -s $d install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk 2>&1
        $output += $installTest
        
        $installFailed = ($installDebug -match "Failure") -or ($installTest -match "Failure")
        
        if ($installFailed) {
            $reason = "CÃ i Ä‘áº·t APK tháº¥t báº¡i"
            return [PSCustomObject]@{ Device = $d; Status = "FAILURE"; Reason = $reason; Output = $output }
        }
        
        # Cháº¡y test
        $output += "Äang cháº¡y test..."
        
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
            $reason = "Test tháº¥t báº¡i"
            foreach ($line in $testOutput) {
                # Kiá»ƒm tra káº¿t quáº£ tá»« finish() trong app
                if ($line -match "INSTRUMENTATION_RESULT: reason=(.*)") {
                    $reason = $Matches[1].Trim()
                    break
                }
                # Kiá»ƒm tra reason tá»« sendStatus (náº¿u cÃ³ dÃ¹ng)
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

Write-Host "`nÄÃ£ kÃ­ch hoáº¡t test trÃªn táº¥t cáº£ cÃ¡c thiáº¿t bá»‹." -ForegroundColor Green
Write-Host "Äang chá» cÃ¡c thiáº¿t bá»‹ hoÃ n thÃ nh..." -ForegroundColor Yellow

$completed = $false
try {
    $jobs | Wait-Job | Out-Null
    $completed = $true
    
    Write-Host "`n================ Káº¾T QUáº¢ TEST ================" -ForegroundColor Green
    
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
                Write-Host "  -> Chi tiáº¿t lá»—i Ä‘Æ°á»£c lÆ°u táº¡i: $logFile" -ForegroundColor Yellow
            }
        }
    }
} finally {
    if (-not $completed) {
        Write-Host "`n[!] Script bá»‹ dá»«ng Ä‘á»™t ngá»™t. Äang dá»n dáº¹p..." -ForegroundColor Yellow
        # CÃ³ thá»ƒ gá»i stop_tests.ps1 á»Ÿ Ä‘Ã¢y
        . "$PSScriptRoot\stop_tests.ps1"
    }
}
