# =====================================================================
# stop-scenario.ps1
# Ferma l'infrastruttura del TemaFinale25 (VirtualRobot + GUI + mosquitto).
# =====================================================================

$repo = "C:\Users\Usuario\Desktop\clas_martes\issLab2026"
$yaml = Join-Path $repo "robotsmart26\yamls\unibobasic26.yaml"

Write-Host "Arresto dei container ..."
docker compose -f $yaml down

Write-Host "Infrastruttura fermata."
docker ps --format "  {{.Names}}  {{.Status}}"
