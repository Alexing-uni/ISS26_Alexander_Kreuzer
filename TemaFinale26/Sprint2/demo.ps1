# =====================================================================
#  demo.ps1 - avvia la DEMO dello Sprint 2 del TemaFinale26 (cargoservice)
#  Uso:  powershell -ExecutionPolicy Bypass -File demo.ps1
#  Prerequisito: Docker Desktop avviato.
# =====================================================================
$ErrorActionPreference = "Continue"
$root = Split-Path -Parent $PSScriptRoot   # .../TemaFinale26
$repo = Split-Path -Parent $root           # .../issLab2026

Write-Host "=== 1) infrastruttura Docker (VirtualRobot + GUI + mosquitto) ===" -ForegroundColor Cyan
docker compose -f "$repo\robotsmart26\yamls\unibobasic26.yaml" up -d
Start-Sleep 5

Write-Host "=== 2) scena WebGL del VirtualRobot (TENERLA IN PRIMO PIANO!) ===" -ForegroundColor Cyan
# flags: senza, in background il browser rallenta i frame e gli step possono fallire
Start-Process "msedge" @("--new-window","--disable-background-timer-throttling",
                         "--disable-renderer-backgrounding","--disable-backgrounding-occluded-windows",
                         "http://localhost:8090")
Start-Sleep 5

Write-Host "=== 3) display web-gui dell'IOPort ===" -ForegroundColor Cyan
Start-Process "msedge" "$PSScriptRoot\cargoservice26\webgui\display.html"

Write-Host "=== 4) robotsmart26 (servizio del docente, porta 8020) ===" -ForegroundColor Cyan
$rs = Start-Process -FilePath "cmd" -ArgumentList "/c","cd /d $repo\robotsmart26 && gradlew.bat run" -PassThru -WindowStyle Minimized
do { Start-Sleep 3 } until (Get-NetTCPConnection -LocalPort 8020 -State Listen -ErrorAction SilentlyContinue)
Write-Host "    robotsmart26 in ascolto su 8020" -ForegroundColor Green
Start-Sleep 8   # attesa 'at home'

Write-Host "=== 5) cargoservice26 (scenario demo ~3 min) ===" -ForegroundColor Cyan
Set-Location "$PSScriptRoot\cargoservice26"
.\gradlew.bat run

# Al termine: CTRL+C ; per fermare robotsmart chiudere la sua finestra.
