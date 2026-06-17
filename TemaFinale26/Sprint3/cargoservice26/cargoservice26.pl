%====================================================================================
% cargoservice26 description  (Sprint 3 - configurazione INTERATTIVA / demo)
%   IOPort = web-gui interattiva (webgui/ioport.html), SERVITA e APERTA
%   dall'applicazione (withobj webio = devices.WebIoPort nel cargoservice):
%     - pushbutton -> EVENT loadrequest   (bottone della pagina)
%     - sensor     -> EVENT sonaralarm:distance(D)  (slider della pagina)
%     - display    -> sottoscrive cargoservice26display
%   Niente iosensor/pushbutton hard-coded: l'utente "gioca" col sensore dal browser.
%   Attori del sistema: cargoservice, sensormonitor, marker (+ robotsmart external).
%   Per il test automatizzato TP1 vedi cargoservice26tp1.pl (aggiunge il tester).
%====================================================================================
mqttBroker("127.0.0.1", "1883", "cargoservice26in").
event( loadrequest, loadrequest(CALLER) ). %SPRINT3: il pushbutton della web-gui; risposta sul display
event( sonaralarm, distance(D) ). %dato grezzo del sensor dell'IOPort (web-gui slider o PicoW reale)
dispatch( containerInPlace, containerInPlace(WHERE) ). %notifica logica: container nell'area del sensor
dispatch( outofservice, outofservice(V) ). %V = on|off
dispatch( domark, domark(SLOT) ). %richiesta di marcatura in slot5
dispatch( markingDone, markingDone(BARCODE) ). %marcatura completata, barcode assegnato
event( loaddone, loaddone(SLOT) ). %carico completato nello slot SLOT (per display/osservatori)
request( getholdstate, getholdstate(ARG) ). %stato della stiva (pattern getrobotstate) ARG unused
reply( holdinfo, holdinfo(STATE) ).  %%for getholdstate | STATE = holdstate(slot1(pieno|libero),...)
request( moverobot, moverobot(TARGETX,TARGETY,STEPTIME) ). %move from current pos to (TARGETX,TARGETY)
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
%====================================================================================
context(ctxcargo, "localhost",  "TCP", "8030").
context(ctxrobotsmart, "127.0.0.1",  "TCP", "8020").
 qactor( robotsmart, ctxrobotsmart, "external").
  qactor( cargoservice, ctxcargo, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
  qactor( sensormonitor, ctxcargo, "it.unibo.sensormonitor.Sensormonitor").
 static(sensormonitor).
  qactor( marker, ctxcargo, "it.unibo.marker.Marker").
 static(marker).
