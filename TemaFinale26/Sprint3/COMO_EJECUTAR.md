# cargoservice26 — ejecución MANUAL (sin demo.ps1)

Requisito: **Docker Desktop abierto**. Abre **3 ventanas de PowerShell**.

---

## Ventana 1 — Infraestructura + escena

```powershell
# (1) matar instancias previas (libera 8020/8030/8095)
Get-CimInstance Win32_Process -Filter "Name='java.exe'" -EA SilentlyContinue |
  Where-Object { $_.CommandLine -match "cargoservice26|robotsmart26|ctxcargo|ctxrobotsmart" } |
  ForEach-Object { Stop-Process -Id $_.ProcessId -Force -EA SilentlyContinue }

# (2) Docker: VirtualRobot (wenv) + GUI + mosquitto
docker compose -f C:\Users\Usuario\Desktop\clas_martes\issLab2026\robotsmart26\yamls\unibobasic26.yaml up -d

# (3) mosquitto: listener 1883 (mqtt) + 9001 (websocket para la web-gui) — config LIMPIA
docker exec mosquitto sh -c "printf 'listener 1883\nprotocol mqtt\n\nlistener 9001\nprotocol websockets\n\nallow_anonymous true\n' > /mosquitto/config/mosquitto.conf"
docker restart mosquitto

# (4) escena WebGL FRESCA (pristina: el robot vuelve fiable)
docker compose -f C:\Users\Usuario\Desktop\clas_martes\issLab2026\robotsmart26\yamls\unibobasic26.yaml up -d --force-recreate wenv

# (5) abrir la escena — UNA sola pestaña, anti-throttle. DEJALA GRANDE Y VISIBLE
Start-Process msedge "--user-data-dir=$env:TEMP\edge_scene --new-window --disable-background-timer-throttling --disable-renderer-backgrounding --disable-backgrounding-occluded-windows --disable-features=CalculateNativeWinOcclusion http://localhost:8090"
```

## Ventana 2 — robotsmart (servicio del profesor, puerto 8020)

```powershell
cd C:\Users\Usuario\Desktop\clas_martes\issLab2026\robotsmart26
.\gradlew.bat run
# ESPERA a ver:  robotmnemo | at home. GUI on ...
# (deja esta ventana abierta; es un servidor)
```

## Ventana 3 — cargoservice (abre sola la web-gui en 8095)

```powershell
cd C:\Users\Usuario\Desktop\clas_martes\issLab2026\TemaFinale26\Sprint3\cargoservice26
.\gradlew.bat run
# ESPERA a ver:  [WEBIOPORT] IOPort web-gui servita su http://localhost:8095
# (deja esta ventana abierta; es un servidor)
```

---

## Uso (web-gui 8095 + escena 8090)

Flujo más fiable:
1. En la **web-gui** pulsa **"Container presente (D=20)"** (deja el contenedor "presente").
2. Pulsa **PREMI** → `reserved slotX`.
3. **Haz clic en la ventana de la ESCENA (8090)** y déjala enfocada/grande mientras el robot se mueve.
   El sensor sigue emitiendo (Web Worker), así que puedes mirar la escena sin que se corte.
   Recorrido: HOME → **slot5** (marcatura `bcN`) → **slotX** → HOME → display `slotX=PIENO(bcN)`.
4. Repite **PREMI** para slot2, slot3, slot4. Al llenar los 4 → `reject` (bodega llena, es lo correcto).

Casos a enseñar en la defensa:
- **Out of service**: "Guasto sonar (D=95)" unos segundos → display `Out of service`; PREMI → `retrylater`. Vuelve a "Container presente".
- **Timeout/disengage**: PREMI y NO pongas contenedor (deja D=45) → a los 30s `disengaged`.

---

## Si ves `moverobotfailed` / `robotfailure`

Es la **escena WebGL degradada** (pierde frames). Soluciones:
- Mantén la **escena (8090) grande, visible y enfocada** durante el movimiento del robot.
- Si insiste, **recrea la escena** y recárgala:
  ```powershell
  docker compose -f C:\Users\Usuario\Desktop\clas_martes\issLab2026\robotsmart26\yamls\unibobasic26.yaml up -d --force-recreate wenv
  ```
  Luego cierra/reabre la pestaña de la escena (paso 5 de la Ventana 1).
- Para una defensa fluida basta enseñar **2–3 cargas** (no hace falta llenar siempre los 4).

## Cerrar todo

`Ctrl+C` en las Ventanas 2 y 3 (responde `S`). Las containers Docker pueden quedarse arriba.
