
#!/usr/bin/env pwsh

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host ""

git add .
git commit -m "auto"
git push origin main
