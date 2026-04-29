%====================================================================================
% fireflymodel0system description   
%====================================================================================
event( flash, flash(ID) ).
%====================================================================================
context(ctxfirefly0, "localhost",  "TCP", "8019").
 qactor( firefly, ctxfirefly0, "it.unibo.firefly.Firefly").
 static(firefly).
