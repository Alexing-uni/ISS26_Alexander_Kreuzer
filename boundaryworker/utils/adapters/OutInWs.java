package adapters;

import java.util.Observable;
import unibo.basicomm23.interfaces.IObserver;
import unibo.basicomm23.interfaces.Interaction;
import unibo.basicomm23.msg.ProtocolType;
import unibo.basicomm23.utils.CommUtils;
import unibo.basicomm23.utils.ConnectionFactory;
import unibo.basicomm23.ws.WsConnection;
import java.awt.Desktop;
import java.net.URI;

public class OutInWs implements IObserver{
	private static OutInWs instance = null;
    private Interaction conn;
    
    public static OutInWs getResource(String port) {
    	//CommUtils.outyellow("OutInWs getResource instance=" + instance);
    	if( instance != null ) return instance;
    	else return new OutInWs(port);
    }
    
    private OutInWs(String port) { 
      	
        try {     	
//        	Runtime.getRuntime().exec("docker-compose -f actorgridgui.yaml up");   	
//        	CommUtils.delay(5000);
//        	CommUtils.outcyan("OutInWs actorgridgui OPEN http://localhost:"+port);

        	/*
        	 * Desktop.isDesktopSupported() FALSE in docker
        	 */
        	/*
         	if (Desktop.isDesktopSupported()) {
        		CommUtils.outblue("java.awt.Desktop supported ");
        		Desktop desktop = Desktop.getDesktop();
        		URI uri = new URI("http://localhost:8085/"); 
        	    desktop.browse(uri); 
        	}
			*/
        	connectToRobotMind(port); //DEVE ESSERE PARTITA LA FACADE ...
        } catch (Exception e) {
        	CommUtils.outred("OutInWs | ERROR:" +e.getMessage());
        }    	   	
    }
    
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 2000;

    public void connectToRobotMind(String port) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                if (CommUtils.getEnvvarValue("VIRTUAL_ENV") != null) { // In docker
                    CommUtils.delay(4000);
                    conn = WsConnection.create("robotoutgui25:" + port, "wsupdates");
                } else {
                    conn = WsConnection.create("localhost:" + port, "wsupdates");
                }
                CommUtils.outcyan("               OutInWs connected to " + port);
                ((WsConnection) conn).addObserver(this);
                return; // success
            } catch (Exception e) {
                CommUtils.outred("               connectToRobotMind | attempt " + attempt + "/" + MAX_RETRIES
                        + " ERROR:" + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    CommUtils.delay(RETRY_DELAY_MS);
                }
            }
        }
        CommUtils.outred("               connectToRobotMind | GIVE UP after " + MAX_RETRIES
                + " attempts – GUI disabled (robotoutgui25 not available)");
        conn = null; // GUI not available; send() will be a no-op
    }
    
    public void send(String msg) {
        if (conn == null) return; // GUI not available – silently skip
        try {
        	//CommUtils.outyellow("		OutInWs send " + msg );
        	conn.forward(msg);
        } catch (Exception e) {
        	CommUtils.outred("               OutInWs | ERROR:" +e.getMessage());
        }
    }

     
//    public void workWithGui( ) {
//        try {
//        	CommUtils.outblue("workWithGui cell");
//        	//conn.forward("clear");       	
//        	//CommUtils.delay(2000);     	
////        	conn.forward("stop");
//        	
//        	conn.forward("cell(1,2,1)");
//        	conn.forward("cell(3,1,1)");
//          
////            CommUtils.delay(1000);
////            CommUtils.outblue("workWithGui clear");
////            conn.forward("clear");    
//            
//        	CommUtils.delay(10000); //To chcek broadcasted messages
//         	CommUtils.outmagenta("OutInWs | BYE" );
//            System.exit(0);
//        
//        } catch (Exception e) {
//        	CommUtils.outred("OutInWs | ERROR:" +e.getMessage());
//        }    	
//    }
    
	@Override 
	public void update(Observable o, Object arg) {
		//CommUtils.outyellow("OutInWs | riceve da observale: " + o + " la info:" + arg);		
		update(arg.toString() );
	}


	@Override
	public void update(String message) {
		//CommUtils.outcyan("OutInWs | update elabora: " + message);
	}
 

    public static void main(String[] args) {
    	OutInWs caller = new OutInWs("8085");
    	//caller.workWithGui(); 
    }
    
    
    /*
     * Just to test ...
     */
    public static int ncellend   = 0;
    public static int ncellstart = 0;
    public static int ncellemit  = 0;
    public static void incend() {
    	ncellend++;
    	CommUtils.outblue("ncellend="+ncellend);
    }
    public static void incemit() {
    	ncellemit++;
    	//CommUtils.outblue("ncellemit="+ncellemit);
    }

    public static void incstart() {
    	ncellstart++;   	 
    }
 
} 
