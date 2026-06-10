%====================================================================================
% cargoservice26 description
%====================================================================================
mqttBroker("127.0.0.1", "1883", "cargoservice26in").
request( loadrequest, loadrequest(CALLER) ). %CALLER don't care
reply( answer, answer(RESP) ).  %%for loadrequest | RESP = reserved(SLOT)|retrylater|reject
request( moverobot, moverobot(TARGETX,TARGETY,STEPTIME) ). %move from current pos to (TARGETX,TARGETY)
reply( moverobotdone, moverobotok(ARG) ).  %%for moverobot
reply( moverobotfailed, moverobotfailed(PLANDONE,PLANTODO) ).  %%for moverobot
%====================================================================================
context(ctxcargo, "localhost",  "TCP", "8030").
context(ctxrobotsmart, "127.0.0.1",  "TCP", "8020").
 qactor( robotsmart, ctxrobotsmart, "external").
  qactor( cargoservice, ctxcargo, "it.unibo.cargoservice.Cargoservice").
 static(cargoservice).
