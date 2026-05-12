param(
    [string]$filePath = "content.txt"
)

# Cấu hình hiển thị tiếng Việt
[Console]::InputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

if (-not (Test-Path $filePath)) {
    Write-Host "File không tồn tại: $filePath" -ForegroundColor Red
    exit 1
}

# Đọc file và in ra từng dòng
$lines = Get-Content -Path $filePath -Encoding utf8
foreach ($line in $lines) {
    Write-Host $line
}

