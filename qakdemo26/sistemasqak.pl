%====================================================================================
% sistemasqak description   
%====================================================================================
request( eval, eval(X) ). %richiesta di valutazione
reply( evalDone, evalDone(V) ).  %%for eval
%====================================================================================
context(ctxsistemas, "localhost",  "TCP", "8030").
 qactor( sistemas, ctxsistemas, "it.unibo.sistemas.Sistemas").
 static(sistemas).
  qactor( clients, ctxsistemas, "it.unibo.clients.Clients").
 static(clients).
