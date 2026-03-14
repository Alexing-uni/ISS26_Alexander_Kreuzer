package conway26appl;
import main.java.conway.io.IoJavalin;
import unibo.basicomm23.utils.CommUtils;

public class MainConwayGui  {
   	private IoJavalin server = new IoJavalin();
  	
    public static void main(String[] args) {
	    System.out.println("MainConway | STARTS " );  
	    
		var resource = MainConwayGui.class.getResource("/page");
		CommUtils.outgreen("DEBUG: La cartella /page si trova in: " + resource);

	    MainConwayGui app = new MainConwayGui();
	    System.out.println("MainConway | ENDS " );  
    }

}