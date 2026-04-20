%====================================================================================
% conway26qak0 description   
%====================================================================================
mqttBroker("localhost", "1883", "lifegameIn").
event( start, start(X) ).
event( stop, stop(X) ).
event( clear, clear(X) ).
event( cellstate, cellstate(X,Y) ).
%====================================================================================
context(ctxlifegame, "localhost",  "TCP", "8945").
 qactor( lifegame, ctxlifegame, "it.unibo.lifegame.Lifegame").
 static(lifegame).
