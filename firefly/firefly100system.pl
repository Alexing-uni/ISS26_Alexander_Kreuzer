%====================================================================================
% firefly100system description   
%====================================================================================
event( flash, flash(ID) ).
%====================================================================================
context(ctxfirefly100, "localhost",  "TCP", "8022").
 qactor( fireflymanager, ctxfirefly100, "it.unibo.fireflymanager.Fireflymanager").
 static(fireflymanager).
  qactor( firefly, ctxfirefly100, "it.unibo.firefly.Firefly").
dynamic(firefly). %%Oct2023 
