# Script dừng tất cả các test đang chạy trên các thiết bị ADB
$PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Import tiện ích
. "$PSScriptRoot\utils.ps1"
Set-Utf8Encoding

Write-Host "==================================================" -ForegroundColor Yellow
Write-Host "Đang dừng tất cả các test đang chạy..." -ForegroundColor Yellow
Write-Host "==================================================" -ForegroundColor Yellow

# 1. Dừng các Job trong PowerShell
$jobs = Get-Job
if ($jobs.Count -gt 0) {
    Write-Host "Đang dừng $($jobs.Count) background jobs trong PowerShell..." -ForegroundColor Gray
    $jobs | Stop-Job -ErrorAction SilentlyContinue
    $jobs | Remove-Job -ErrorAction SilentlyContinue
}

# 2. Dừng app trên các thiết bị ADB
$devices = Get-AdbDevices

if ($devices.Count -gt 0) {
    Write-Host "Đang dừng app trên $($devices.Count) thiết bị..." -ForegroundColor Gray
    foreach ($device in $devices) {
        # Force stop app và test runner
        adb -s $device shell am force-stop com.aki.akiwarmup
        adb -s $device shell am force-stop com.ss.android.ugc.trill
    }
}

Write-Host "`n==================================================" -ForegroundColor Green
Write-Host "Đã dừng tất cả các test thành công." -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green

