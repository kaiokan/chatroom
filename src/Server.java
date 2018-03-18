/*資管三A 103403519 劉昌平*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Server extends JFrame{
	public static JTextArea messages, users;
	private Screen screen;
	private Bar bar;
	private ServerSocket server = null;
	private Socket sconnection;
	private JLabel onlinepeople, broadcast;
	public static JTextField onlinepeopletext;
	private JTextField broadcasttext;
	private Waitforconnection waitforconnection;
	private final int port = 1500;
	public static ArrayList <ClientListener> clientlist = new ArrayList<ClientListener>();
	public static ArrayList <Integer> IDlist = new ArrayList<Integer>();
	public static int onlinenum = 0;
	
	Server(){
		super("Server");
		bar = new Bar();
		add(bar, BorderLayout.NORTH);
		screen = new Screen();
		add(screen, BorderLayout.CENTER);
		waitforconnection = new Waitforconnection();
		waitforconnection.start();
		broadcasttext.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				for(ClientListener a: clientlist)
						a.sendmessage("***Server Broadcast***" + e.getActionCommand());
				broadcasttext.setText("");
				messages.append("***Server Broadcast***" + e.getActionCommand() + "\n");
			}
		});
	}	
	
	private class Bar extends JPanel{
		Bar(){
			onlinepeople = new JLabel("線上人數：");
			add(onlinepeople);
			onlinepeopletext = new JTextField(Integer.toString(onlinenum) , 10);
			onlinepeopletext.setEditable(false);
			add(onlinepeopletext);
			broadcast = new JLabel("廣播：");
			add(broadcast);
			broadcasttext = new JTextField(null, 10);
			add(broadcasttext);
		}
	}
	
	private class Screen extends JPanel{
		Screen(){
			messages = new JTextArea(22, 29);
			messages.setEditable(false);
			add(messages);
			users = new JTextArea(22, 20);
			users.setEditable(false);
			add(users);
		}
	}
	
	private class Waitforconnection extends Thread{
		public void run(){
				try{
					server = new ServerSocket(port);
					messages.append("<System>聊天室已上線，等待來自port:1500的連線...\n");
				}
				catch(IOException ioexception){
					JOptionPane.showMessageDialog(null, "Dont open two same server.", "Error", 2);
					System.exit(0);
				}	
				
				while(true){	
					try{
						sconnection = server.accept(); //獲得連線
					}	
					catch(IOException ioexception){ioexception.printStackTrace();}			
					ClientListener client = new ClientListener(sconnection);
					client.start(); //開始跑clientlistener
				}		
		}
	}
}