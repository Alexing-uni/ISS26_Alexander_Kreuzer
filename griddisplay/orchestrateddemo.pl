%====================================================================================
% orchestrateddemo description   
%====================================================================================
request( paint, paint(X,Y,COLOR) ). %richiesta di disegno
reply( painted, painted(WHAT) ).  %%for paint
dispatch( cellstate, cellstate(X,Y,COLOR) ). %aggiornamento cella
%====================================================================================
context(ctxorch, "localhost",  "TCP", "8060").
 qactor( director, ctxorch, "it.unibo.director.Director").
 static(director).
  qactor( painter, ctxorch, "it.unibo.painter.Painter").
 static(painter).
  qactor( displaymock, ctxorch, "it.unibo.displaymock.Displaymock").
 static(displaymock).
