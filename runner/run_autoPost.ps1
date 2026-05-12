param(
    [string]$contentPath,
    [string]$deviceFile
)

# Script chuyên biệt để chạy tính năng Auto Post
$PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectRoot = (Get-Item "$PSScriptRoot\..").FullName

$contentFile = $contentPath
if (-not $contentFile) {
    $contentFile = "$projectRoot\content.txt"
}
$captions = @()

if (Test-Path $contentFile) {
    Write-Host "Reading content from: $contentFile" -ForegroundColor Cyan
    $captions = Get-Content -Path $contentFile -Encoding utf8
} else {
    Write-Host "Warning: Content file not found at $contentFile. Using default caption." -ForegroundColor Yellow
}

# Gọi file lõi điều phối và truyền method cùng mảng captions
& "$PSScriptRoot\run_parallel.ps1" -method autoPost -captions $captions -deviceFile $deviceFile

