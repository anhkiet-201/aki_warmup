# Script chuyên biệt để chạy tính năng Auto Post
$PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectRoot = (Get-Item "$PSScriptRoot\..").FullName

$contentFile = "$projectRoot\content.txt"
$captions = @()

if (Test-Path $contentFile) {
    Write-Host "Đang đọc dữ liệu từ: $contentFile" -ForegroundColor Cyan
    $captions = Get-Content -Path $contentFile -Encoding utf8
} else {
    Write-Host "Cảnh báo: Không tìm thấy file content.txt tại $contentFile. Sẽ dùng caption mặc định." -ForegroundColor Yellow
}

# Gọi file lõi điều phối và truyền method cùng mảng captions
& "$PSScriptRoot\run_parallel.ps1" -method autoPost -captions $captions

