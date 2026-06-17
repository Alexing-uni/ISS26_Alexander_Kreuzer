%====================================================================================
% cargoservice26 description  (Sprint 3 - configurazione TEST AUTOMATIZZATO TP1)
%   Identica alla configurazione interattiva (cargoservice26.pl) ma AGGIUNGE il
%   tester, che esercita il sistema SENZA interazione umana:
%     - emette il pushbutton  (EVENT loadrequest)
%     - emette il sensor      (EVENT sonaralarm:distance(20) per ~3s = presenza)
%     - interroga lo stato della stiva (getholdstate) -> TP1 PASS / TP1 FAIL.
%   Si usa per la prova TP1 (carico nominale, demo finale): vedi Mainctxcargotp1.
%====================================================================================
mqttBroker("127.0.0.1", "1883", "cargoservice26in").
event( loadrequest, loadrequest(CALLER) ). %SPRINT3: il pushbutton (qui generato dal tester)
event( sonaralarm, distance(D) ). %dato grezzo del sensor (qui generato dal tester)
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
  qactor( tester, ctxcargo, "it.unibo.tester.Tester").
 static(tester).
