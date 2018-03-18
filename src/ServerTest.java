/*資管三A 103403519 劉昌平*/

import javax.swing.*;

public class ServerTest{
	public static Server server;
	public static void main( String[] args ){ 
		server = new Server();
	    server.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    server.setSize( 600, 400 ); 
	    server.setVisible( true ); 
	 } 
}
