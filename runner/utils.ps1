# Tiện ích dùng chung cho các script PowerShell
function Set-Utf8Encoding {
    [Console]::InputEncoding = [System.Text.Encoding]::UTF8
    [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
    $OutputEncoding = [System.Text.Encoding]::UTF8
}

function Get-AdbDevices {
    param(
        [string]$deviceFile
    )
    if ($deviceFile -and (Test-Path $deviceFile)) {
        # Đọc danh sách thiết bị từ file, bỏ qua dòng trống
        $devices = Get-Content -Path $deviceFile -Encoding utf8 | Where-Object { $_.Trim() -ne "" }
        return $devices
    }
    # Nếu không có file hoặc file không tồn tại, tự động quét
    $devices = adb devices | Where-Object { $_ -match "\bdevice\b" } | ForEach-Object { $_.Split("`t")[0] }
    return $devices
}



