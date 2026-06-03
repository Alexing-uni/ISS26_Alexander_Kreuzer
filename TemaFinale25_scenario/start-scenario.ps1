# =====================================================================
# start-scenario.ps1
# Avvia l'infrastruttura per il TemaFinale25 (cargoservice):
#   - VirtualRobot26 (scena 3D)     porte 8090 / 8091
#   - robotoutgui25  (GUI mappa)    porta 8085
#   - mosquitto      (broker MQTT)  porta 1883
#
# Uso:  click destro -> "Esegui con PowerShell"   oppure
#       .\start-scenario.ps1
# =====================================================================

$repo = "C:\Users\Usuario\Desktop\clas_martes\issLab2026"
$yaml = Join-Path $repo "robotsmart26\yamls\unibobasic26.yaml"

Write-Host "[1/3] rete iss-network ..."
docker network inspect iss-network 2>$null | Out-Null
if ($LASTEXITCODE -ne 0) {
    docker network create iss-network | Out-Null
    Write-Host "      creata."
} else {
    Write-Host "      gia' presente."
}

Write-Host "[2/3] avvio container (VirtualRobot + GUI + mosquitto) ..."
docker compose -f $yaml up -d

Write-Host "[3/3] stato dei container:"
docker ps --format "  {{.Names}}  |  {{.Status}}  |  {{.Ports}}"

Write-Host ""
Write-Host "==================================================================="
Write-Host " PRONTO. Apri nel browser:"
Write-Host "   VirtualRobot : http://localhost:8090"
Write-Host "   GUI mappa    : http://localhost:8085"
Write-Host "   MQTT broker  : localhost:1883"
Write-Host ""
Write-Host " Prossimi terminali (uno per servizio):"
Write-Host "   (A)  cd $repo\robotsmart26 ; .\gradlew run"
Write-Host "   (B)  cd $repo\Picow\mock   ; python mockPicowRadar.py"
Write-Host "   (C)  [progetto finale]     cargoservice26"
Write-Host "==================================================================="
