var pageId = "unknown";
var opened = false;
var socketToGui;

window.sendCmdToServer = function(cmd) {
    if (opened && socketToGui) {
        var msg = "msg( eval, dispatch, " + pageId + ", lifectrl, " + cmd + ", 0 )";
        console.log(">>> ENVIANDO AL SERVIDOR: " + msg);
        socketToGui.send(msg);
    }
};

function initWS() {
    var host = window.location.host || "localhost:8080";
    socketToGui = new WebSocket("ws://" + host + "/eval");

    socketToGui.addEventListener("open", function() {
        console.log(">>> WebSocket Conectado");
        opened = true;
        window.sendCmdToServer("ready");
    });

    socketToGui.addEventListener("message", function(event) {
        var data = event.data;

        // 1. LÓGICA DE OWNER (Bloquear botones si eres el 2º)
        if (data.startsWith("ID:")) {
            pageId = data.split(":")[1];
            console.log(">>> SOY EL JUGADOR: " + pageId);
            
            if (pageId !== "caller1") {
                console.log(">>> MODO OBSERVADOR. Bloqueando controles.");
                document.querySelectorAll("button").forEach(btn => {
                    if (!btn.textContent.includes("Remove")) {
                        btn.disabled = true;
                        btn.style.opacity = "0.5";
                    }
                });
            }
        } 
		// 2. LÓGICA DE PINTAR
		        else if (data.includes("cell(")) {
		            var match = data.match(/cell\s*\(\s*(\d+)\s*,\s*(\d+)\s*\)/);
		            if (match) {
		                var cellId = "cell(" + match[1] + "," + match[2] + ")";
		                var cellEl = document.getElementById(cellId);
		                if (cellEl) {
		                    cellEl.style.backgroundColor = (cellEl.style.backgroundColor === "black") ? "" : "black";
		                }
		            }
		        }
		        // 3. NUEVO: LÓGICA DE LIMPIAR TABLERO
		        else if (data.includes("clear")) {
		            console.log(">>> LIMPIANDO TABLERO PARA TODOS");
		            document.querySelectorAll(".grid div").forEach(cell => {
		                cell.style.backgroundColor = ""; // Borramos el color
		            });
		        }
    });

    socketToGui.addEventListener("close", function() {
        opened = false;
        setTimeout(initWS, 2000);
    });
}
initWS();