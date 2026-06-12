# =====================================================================
#  demo.ps1 - avvia la DEMO dello Sprint 2 del TemaFinale26 (cargoservice)
#  Uso:  powershell -ExecutionPolicy Bypass -File demo.ps1
#  Prerequisito: Docker Desktop avviato.
#
#  IMPORTANTE: la scena WebGL (localhost:8090) ESEGUE i movimenti del robot.
#  Deve esserci UNA SOLA pagina della scena aperta (piu' pagine = robot
#  desincronizzati e moverobotfailed). Questo script garantisce una sola
#  scena: chiude quelle vecchie e riavvia il container wenv.
# =====================================================================
$ErrorActionPreference = "Continue"
$root = Split-Path -Parent $PSScriptRoot   # .../TemaFinale26
$repo = Split-Path -Parent $root           # .../issLab2026
$prof = "$env:TEMP\edge_scene_profile"     # profilo Edge DEDICATO alla scena

Write-Host "=== 1) infrastruttura Docker (VirtualRobot + GUI + mosquitto) ===" -ForegroundColor Cyan
docker compose -f "$repo\robotsmart26\yamls\unibobasic26.yaml" up -d
Start-Sleep 4

# il display web usa MQTT su websocket (porta 9001): l'immagine mosquitto di
# default NON lo abilita -> lo aggiungo alla config del container (idempotente)
$wsOn = docker exec mosquitto sh -c "grep -c 'listener 9001' /mosquitto/config/mosquitto.conf" 2>$null
if ("$wsOn".Trim() -eq "0") {
    Write-Host "    abilito il listener websockets (9001) di mosquitto ..." -ForegroundColor Yellow
    docker exec mosquitto sh -c "printf '\nlistener 1883\nprotocol mqtt\n\nlistener 9001\nprotocol websockets\n\nallow_anonymous true\n' >> /mosquitto/config/mosquitto.conf"
    docker restart mosquitto | Out-Null
    Start-Sleep 4
}

Write-Host "=== 2) garantisco UNA SOLA scena WebGL ===" -ForegroundColor Cyan
# chiude eventuali finestre della scena aperte in precedenza (solo il profilo dedicato)
Get-CimInstance Win32_Process -Filter "Name='msedge.exe'" -ErrorAction SilentlyContinue |
  Where-Object { $_.CommandLine -like "*edge_scene_profile*" } |
  ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }
Start-Sleep 2
docker restart wenv | Out-Null   # azzera le connessioni-scena rimaste appese
Start-Sleep 6
Start-Process "msedge" @("--user-data-dir=$prof","--no-first-run","--new-window",
                         "--disable-background-timer-throttling","--disable-renderer-backgrounding",
                         "--disable-backgrounding-occluded-windows","--disable-features=CalculateNativeWinOcclusion","http://localhost:8090")
Write-Host "    scena aperta: TENERLA IN PRIMO PIANO durante la demo" -ForegroundColor Yellow
Write-Host "    (NON aprire localhost:8090 in altre schede!)" -ForegroundColor Yellow
Start-Sleep 8

Write-Host "=== 3) display web-gui dell'IOPort (browser normale) ===" -ForegroundColor Cyan
Start-Process "$PSScriptRoot\cargoservice26\webgui\display.html"

Write-Host "=== 4) robotsmart26 (servizio del docente, porta 8020) ===" -ForegroundColor Cyan
$rs = Start-Process -FilePath "cmd" -ArgumentList "/c","cd /d $repo\robotsmart26 && gradlew.bat run" -PassThru -WindowStyle Minimized
do { Start-Sleep 3 } until (Get-NetTCPConnection -LocalPort 8020 -State Listen -ErrorAction SilentlyContinue)
Write-Host "    robotsmart26 in ascolto su 8020 (attendo 'at home') ..." -ForegroundColor Green
Start-Sleep 10

Write-Host "=== 5) cargoservice26 (scenario demo ~3 min) ===" -ForegroundColor Cyan
Write-Host "    NB: a fine scenario il servizio resta in ascolto (e' un server):" -ForegroundColor Yellow
Write-Host "    'EXECUTING xx%' di Gradle e' NORMALE. Per chiudere: CTRL+C qui," -ForegroundColor Yellow
Write-Host "    e chiudere la finestra di robotsmart26." -ForegroundColor Yellow
Set-Location "$PSScriptRoot\cargoservice26"
.\gradlew.bat run
