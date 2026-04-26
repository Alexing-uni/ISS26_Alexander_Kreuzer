%====================================================================================
% firefly3system description   
%====================================================================================
event( flash, flash(ID) ).
%====================================================================================
context(ctxfirefly3, "localhost",  "TCP", "8021").
 qactor( firefly1, ctxfirefly3, "it.unibo.firefly1.Firefly1").
 static(firefly1).
  qactor( firefly2, ctxfirefly3, "it.unibo.firefly2.Firefly2").
 static(firefly2).
  qactor( firefly3, ctxfirefly3, "it.unibo.firefly3.Firefly3").
 static(firefly3).
