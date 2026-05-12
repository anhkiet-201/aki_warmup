param(
    [string]$deviceFile
)

# Script dừng tất cả các test đang chạy trên các thiết bị ADB
$PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Import tiện ích
. "$PSScriptRoot\utils.ps1"
Set-Utf8Encoding

Write-Host "==================================================" -ForegroundColor Yellow
Write-Host "Stopping all running tests..." -ForegroundColor Yellow
Write-Host "==================================================" -ForegroundColor Yellow

# 1. Dừng các Job trong PowerShell
$jobs = Get-Job
if ($jobs.Count -gt 0) {
    Write-Host "Stopping $($jobs.Count) background jobs in PowerShell..." -ForegroundColor Gray
    $jobs | Stop-Job -ErrorAction SilentlyContinue
    $jobs | Remove-Job -ErrorAction SilentlyContinue
}

# 2. Dừng app trên các thiết bị ADB
$devices = Get-AdbDevices -deviceFile $deviceFile

if ($devices.Count -gt 0) {
    Write-Host "Stopping app on $($devices.Count) devices..." -ForegroundColor Gray
    foreach ($device in $devices) {
        # Force stop app và test runner
        adb -s $device shell am force-stop com.aki.akiwarmup
        adb -s $device shell am force-stop com.ss.android.ugc.trill
    }
}

Write-Host "`n==================================================" -ForegroundColor Green
Write-Host "All tests stopped successfully." -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green
