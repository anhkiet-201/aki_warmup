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

# 1. Dừng các Job trong PowerShell bằng cách giết tiến trình con
$currentPid = $PID
$childProcesses = Get-CimInstance Win32_Process | Where-Object { $_.ParentProcessId -eq $currentPid -and $_.Name -match "powershell" }

if ($childProcesses) {
    Write-Host "Force killing $($childProcesses.Count) child PowerShell processes..." -ForegroundColor Gray
    foreach ($p in $childProcesses) {
        Stop-Process -Id $p.ProcessId -Force -ErrorAction SilentlyContinue
    }
}

# Vẫn gọi Remove-Job để dọn dẹp danh sách job
Get-Job | Remove-Job -Force -ErrorAction SilentlyContinue

# 2. Dừng app trên các thiết bị ADB
$devices = Get-AdbDevices -deviceFile $deviceFile

if ($devices.Count -gt 0) {
    Write-Host "Stopping app on $($devices.Count) devices..." -ForegroundColor Gray
    foreach ($device in $devices) {
        # Force stop app và test runner
        adb -s $device shell am force-stop com.aki.akiwarmup
        adb -s $device shell am force-stop com.aki.akiwarmup.test
        adb -s $device shell am force-stop com.ss.android.ugc.trill
        adb -s $device shell am force-stop com.genfarmer.uiautomator.test
        adb -s $device shell am force-stop com.genfarmer.uiautomator
    }
}

Write-Host "`n==================================================" -ForegroundColor Green
Write-Host "All tests stopped successfully." -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Green
