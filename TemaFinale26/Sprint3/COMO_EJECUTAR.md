# cargoservice26 — ejecución MANUAL desde 0 (sin demo.ps1)

Requisito: **Docker Desktop abierto**.

> **Por qué "localhost:8090 está ocupado":** la escena WebGL admite **UN solo cliente
> conectado**. Si queda una ventana del navegador (de un run anterior, o de un perfil
> dedicado que abrió la app/script) todavía enganchada a 8090, tu pestaña nueva la ve
> ocupada. Solución: cerrar esas ventanas + recrear `wenv` fresco + abrir 8090 en UNA
> sola pestaña. El Paso 0 lo hace.

---

## Paso 0 — limpieza (cierra lo que quedó enganchado)  [Ventana 1]

```powershell
# matar cargoservice/robotsmart previos (liberan 8020/8030/8095)
Get-CimInstance Win32_Process -Filter "Name='java.exe'" -EA SilentlyContinue |
  Where-Object { $_.CommandLine -match "cargoservice26|robotsmart26|ctxcargo|ctxrobotsmart" } |
  ForEach-Object { Stop-Process -Id $_.ProcessId -Force -EA SilentlyContinue }

# cerrar las ventanas de navegador de perfiles dedicados (las que OCUPAN la escena)
Get-CimInstance Win32_Process -Filter "Name='msedge.exe'" -EA SilentlyContinue |
  Where-Object { $_.CommandLine -match "edge_scene|edge_ioport" } |
  ForEach-Object { Stop-Process -Id $_.ProcessId -Force -EA SilentlyContinue }
```
> Y cierra también cualquier pestaña normal que tengas abierta en `localhost:8090`.

## Paso 1 — Docker + broker + escena FRESCA  [misma Ventana 1]

```powershell
# infraestructura: VirtualRobot (wenv) + GUI + mosquitto
docker compose -f C:\Users\Usuario\Desktop\clas_martes\issLab2026\robotsmart26\yamls\unibobasic26.yaml up -d

# mosquitto: listener 1883 (mqtt) + 9001 (websocket) — config LIMPIA (idempotente)
docker exec mosquitto sh -c "printf 'listener 1883\nprotocol mqtt\n\nlistener 9001\nprotocol websockets\n\nallow_anonymous true\n' > /mosquitto/config/mosquitto.conf"
docker restart mosquitto

# escena WebGL PRISTINA (resetea el servidor -> 8090 queda libre)
docker compose -f C:\Users\Usuario\Desktop\clas_martes\issLab2026\robotsmart26\yamls\unibobasic26.yaml up -d --force-recreate wenv
```

## Paso 2 — robotsmart (servicio del profesor, 8020)  [Ventana 2]

```powershell
cd C:\Users\Usuario\Desktop\clas_martes\issLab2026\robotsmart26
.\gradlew.bat run
```
> Espera a ver: **`robotmnemo | at home. GUI on ...`**  → déjala abierta.

## Paso 3 — cargoservice (este Sprint)  [Ventana 3]

```powershell
cd C:\Users\Usuario\Desktop\clas_martes\issLab2026\TemaFinale26\Sprint3\cargoservice26
.\gradlew.bat run -PioportOpen=false
```
> `-PioportOpen=false` = la app **NO** abre la web-gui (la abres tú en el Paso 4).
> Espera a ver: **`[WEBIOPORT] auto-open OFF: apri tu ... http://localhost:8095`** → déjala abierta.
> (Para que la abra la app sola — modo "requisito del Sprint", para la defensa — usa
> simplemente `.\gradlew.bat run` sin la opción.)

## Paso 4 — TÚ abres las dos páginas (UNA pestaña cada una)

```powershell
# web-gui del IOPort (8095): pushbutton + sensor + display
Start-Process msedge "--user-data-dir=$env:TEMP\edge_ioport --new-window http://localhost:8095"

# escena WebGL (8090): ejecuta los movimientos del robot — anti-throttle, grande y visible
Start-Process msedge "--user-data-dir=$env:TEMP\edge_scene --new-window --disable-background-timer-throttling --disable-renderer-backgrounding --disable-backgrounding-occluded-windows --disable-features=CalculateNativeWinOcclusion http://localhost:8090"
```
> **UNA sola pestaña de cada una** (dos de 8090 → "ocupado"; dos de 8095 → dos sensores en conflicto).
> Abre la escena **antes de pulsar PREMI** (ejecuta los movimientos) y déjala grande y visible.

---

## Uso (web-gui 8095 + escena 8090)

1. En la web-gui: **"Container presente (D=20)"** → **PREMI** → `reserved slotX`.
2. **Haz clic en la ESCENA (8090)** y déjala enfocada mientras el robot se mueve
   (el sensor sigue emitiendo gracias al Web Worker): HOME → slot5 (`bcN`) → slotX → HOME
   → `slotX=PIENO(bcN)`.
3. Repite PREMI. Al llenar los 4 slots → `reject` (bodega llena, correcto).
4. **Out of service**: "Guasto sonar (D=95)" unos segundos; **Timeout**: PREMI sin contenedor (D=45) → 30s → `disengaged`.

## Si ves `moverobotfailed` / `robotfailure`
La escena WebGL se degradó. Recrea la escena y reábrela (Paso 1 último comando + Paso 4):
```powershell
docker compose -f C:\Users\Usuario\Desktop\clas_martes\issLab2026\robotsmart26\yamls\unibobasic26.yaml up -d --force-recreate wenv
```
Para la defensa basta con 2–3 cargas + `reject` + `Out of service`.

## Cerrar
`Ctrl+C` (responde `S`) en las Ventanas 2 y 3.
