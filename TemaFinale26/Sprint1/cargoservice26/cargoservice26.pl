%====================================================================================
% cargoservice26 description  (Sprint 1: core + sensor IOPort + timeout 30s)
%====================================================================================
mqttBroker("127.0.0.1", "1883", "cargoservice26in").
request( loadrequest, loadrequest(CALLER) ). %CALLER don't care
reply( answer, answer(RESP) ).  %%for loadrequest | RESP = reserved(SLOT)|retrylater|reject
event( containerInPlace, containerInPlace(WHERE) ). %notifica logica del sensor dell'IOPort
request( moverobot, moverobot(TARGETX,TARGETY,STEPTIME) ). %move from current pos to (TARGETX,TARGETY)
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
%====================================================================================
context(ctxcargo, "localhost",  "TCP", "8030").
context(ctxrobotsmart, "127.0.0.1",  "TCP", "8020").
 qactor( robotsmart, ctxrobotsmart, "external").
  qactor( cargoservice, ctxcargo, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
  qactor( iosensor, ctxcargo, "it.unibo.iosensor.Iosensor").
 static(iosensor).
  qactor( pushbutton, ctxcargo, "it.unibo.pushbutton.Pushbutton").
 static(pushbutton).
