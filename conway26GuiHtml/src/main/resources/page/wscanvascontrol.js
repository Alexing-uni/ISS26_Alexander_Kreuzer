const canvas = document.getElementById("lifeCanvas");
const ctx = canvas.getContext("2d");
const CELL_SIZE = 20; // 20x20 celdas = 400px

let pageId = "unknown";
let opened = false;
let socketToGui;

// DIBUJA TODA LA MATRIZ DE GOLPE
function draw(grid) {
    ctx.clearRect(0, 0, canvas.width, canvas.height); // Borrar anterior
    for (let r = 0; r < grid.length; r++) {
        for (let c = 0; c < grid[r].length; c++) {
            ctx.fillStyle = grid[r][c] ? "black" : "white"; // Negro si viva
            ctx.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            ctx.strokeStyle = "#ccc"; // Borde gris claro
            ctx.strokeRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
    }
}

// CAPTURA CLICS EN EL CANVAS
canvas.addEventListener("click", (event) => {
    if (pageId !== "caller1") return; // Solo el owner puede pintar
    const rect = canvas.getBoundingClientRect();
    const col = Math.floor((event.clientX - rect.left) / CELL_SIZE);
    const row = Math.floor((event.clientY - rect.top) / CELL_SIZE);
    window.sendCmdToServer(`cell(${row},${col})`); 
});

window.sendCmdToServer = function(cmd) {
    if (opened && socketToGui) {
        var msg = `msg(eval, dispatch, ${pageId}, lifectrl, ${cmd}, 0)`;
        socketToGui.send(msg);
    }
};

function initWS() {
    var host = window.location.host || "localhost:8080";
    socketToGui = new WebSocket("ws://" + host + "/eval");

    socketToGui.onopen = function() {
        document.getElementById("status").innerText = "STATO: CONNESSO";
        opened = true;
        window.sendCmdToServer("canvas-ready"); // Avisamos que somos globales
        setInterval(() => { if(opened) socketToGui.send("PING"); }, 20000);
    };

    socketToGui.onmessage = function(event) {
        var data = event.data;
        if (data.startsWith("ID:")) {
            pageId = data.split(":")[1];
            if (pageId !== "caller1") { // Bloqueamos al observador
                document.querySelectorAll("button").forEach(b => b.disabled = true);
            }
        } 
        else if (data.includes("[[")) { // Si recibimos la matriz JSON
            try {
                const grid = JSON.parse(data);
                requestAnimationFrame(() => draw(grid)); // Dibujar ultra rápido
            } catch (e) { console.error("Error JSON", e); }
        }
    };

    socketToGui.onclose = function() {
        opened = false;
        document.getElementById("status").innerText = "STATO: DISCONNESSO";
        setTimeout(initWS, 2000);
    };
}

initWS();

// Conectar botones
document.getElementById("btnStart").onclick = () => window.sendCmdToServer("start");
document.getElementById("btnStop").onclick = () => window.sendCmdToServer("stop");
document.getElementById("btnClear").onclick = () => window.sendCmdToServer("clear");