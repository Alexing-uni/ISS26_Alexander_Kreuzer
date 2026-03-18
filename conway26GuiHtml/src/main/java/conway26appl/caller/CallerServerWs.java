package conway26appl.caller; // CORRECCIÓN: Sin el 'main.java.'

import unibo.basicomm23.interfaces.IApplMessage;
import unibo.basicomm23.msg.ApplMessage;
import unibo.basicomm23.utils.CommUtils;
import java.net.URI;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
 
public class CallerServerWs  {  
	private IApplMessage reqmsg    = CommUtils.buildRequest("clientjava", "eval", "CELL", "server"  );
	private IApplMessage setctrl   = CommUtils.buildRequest("clientjava", "eval", "setcontroller", "server"  );
	protected CountDownLatch latch = new CountDownLatch(1);
    protected HttpClient client    = HttpClient.newHttpClient();  
    protected String name;
    
    public CallerServerWs( ) throws Exception {
    	sendCellChange( );
    }

    protected void sendRawMessage( ) throws InterruptedException {
        WebSocket webSocket = client.newWebSocketBuilder()
            .buildAsync(URI.create("ws://localhost:8080/chat"), new WebSocketListener(latch))
            .join();
        webSocket.sendText(setctrl.toString(), true);
        latch.await();
        CommUtils.outred("CallerServerWs | setup1 finito");
   }     

    protected void sendCellChange( ) throws InterruptedException {
        WebSocket webSocket = client.newWebSocketBuilder()
            .buildAsync(URI.create("ws://localhost:8080/eval"), new WebSocketListener(latch))
            .join();
        String c56 = reqmsg.toString().replace("CELL", "cell(5,6,1)");
        CommUtils.outmagenta("CallerServerWs | send " + c56);
        webSocket.sendText(c56, true);
        latch.await();
    }     
    
    private static class WebSocketListener implements WebSocket.Listener {
        private final CountDownLatch latch;

        public WebSocketListener(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("CallerServerWs | --- Connessione aperta ---");
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            CommUtils.outmagenta("CallerServerWs | Messaggio ricevuto dal server: " + data);
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            System.out.println("CallerServerWs | --- Connessione chiusa: " + reason + " ---");
            latch.countDown();
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.err.println("Errore: " + error.getMessage());
            latch.countDown();
        }
    }

    public static void main(String[] args) throws Exception {
    	System.out.println("Java.version="+ System.getProperty("java.version"));
    	new CallerServerWs();
    }
}