# Tiện ích dùng chung cho các script PowerShell
function Set-Utf8Encoding {
    [Console]::InputEncoding = [System.Text.Encoding]::UTF8
    [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
    $OutputEncoding = [System.Text.Encoding]::UTF8
}

function Get-AdbDevices {
    $devices = adb devices | Where-Object { $_ -match "\bdevice\b" } | ForEach-Object { $_.Split("`t")[0] }
    return $devices
}



