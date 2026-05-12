param(
    [string]$deviceFile
)

# Script chuyên biệt để chạy tính năng Warm Up
$PSScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Gọi file lõi điều phối và truyền method
& "$PSScriptRoot\run_parallel.ps1" -method warmUp -deviceFile $deviceFile
