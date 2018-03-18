/*資管三A 103403519 劉昌平*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client extends JFrame{
	private JLabel nicknamelabel, onlinenum, sendmessage;
	private JTextArea screen;
	private JTextField nicknamespace, onlinenumspace, messagebox;
	private Topscreen topscreen;
	private Bottomscreen bottomscreen;
	private Socket cConnection;
	private String nickname;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerListener serverlistener;
	
	Client(){
		super("Client");
		topscreen = new Topscreen();
		add(topscreen, BorderLayout.NORTH);
		screen = new JTextArea();
		screen.setEditable(false);
		add(new JScrollPane(screen), BorderLayout.CENTER);
		bottomscreen = new Bottomscreen();
		add(bottomscreen, BorderLayout.SOUTH);	
		try{
			cConnection = new Socket(InetAddress.getByName("127.0.0.1"), 1500);	
	        output = new ObjectOutputStream( cConnection.getOutputStream() );      
	        output.flush();
	        input = new ObjectInputStream( cConnection.getInputStream() );   	
	        //確定要開始連線了
			screen.append("<System>連線至server'" + cConnection.getInetAddress() + ":" + cConnection.getPort() + "'\n");
	 
			serverlistener = new ServerListener(); //開啟ServerListener
			serverlistener.start();
			
			messagebox.addActionListener(new ActionListener(){	//讀訊息
				public void actionPerformed(ActionEvent e){
					try{
						output.writeObject(nickname + "：" + e.getActionCommand());
						messagebox.setText("");
					} 
					catch (IOException e1) {e1.printStackTrace();}
				}
			});
		}
		catch(IOException ioexception){			 //連線失敗
			JOptionPane.showMessageDialog(null, "Server關閉中", "Server Closed", 2);
			System.exit(0);
		}
	}
	
	public class Topscreen extends JPanel{
		Topscreen(){
			nicknamelabel = new JLabel("我的暱稱：");
			add(nicknamelabel);
			nicknamespace = new JTextField(null, 10);
			nicknamespace.setEditable(false);
			add(nicknamespace);
			onlinenum = new JLabel("線上人數");
			add(onlinenum);
			onlinenumspace = new JTextField(null, 10);
			onlinenumspace.setEditable(false);
			add(onlinenumspace);
		}
	}
	
	public class Bottomscreen extends JPanel{
		Bottomscreen(){
			sendmessage = new JLabel("傳送訊息：");
			add(sendmessage);
			messagebox = new JTextField(null, 10);
			add(messagebox);
		}
	}
	
	public class ServerListener extends Thread{
		public void run(){
			try{		
				nickname = JOptionPane.showInputDialog(null, "請輸入您的暱稱：", "暱稱輸入", 1);
				output.writeObject("!Nickname= " + nickname); 
				while(true){	//偵測該暱稱是否已被使用
					String checker = (String) input.readObject();
					if(checker.equals("valid nickname"))
						break;
					else if(checker.equals("invalid nickname")){
						JOptionPane.showMessageDialog(null, "該匿稱已有人使用，請重新輸入", "錯誤", 2);
						nickname = JOptionPane.showInputDialog(null, "請輸入您的暱稱：", "暱稱輸入", 1);
						output.writeObject("!Nickname= " + nickname); 
					}	
				}	
				setVisible(true);
				nicknamespace.setText(nickname);
			}
			catch (IOException | ClassNotFoundException e){e.printStackTrace();}	
			
			while(true){
	        	try{
	        		String message = (String) input.readObject();        	//不斷讀訊息		
	        		if(message.split(" ", 2)[0].equals("!目前連線人數是")) 	//更新人數類型的訊息
	        			onlinenumspace.setText(message.split(" ", 2)[1]);
	        		else
	        			screen.append(message + "\n");						//普通的訊息
	        	}
	        	catch (IOException | ClassNotFoundException e){
	        		JOptionPane.showMessageDialog(null, "Can not connect to server", "Server Closed", 2);
	        		System.exit(0);
	        	}
			}
		}
	}
}
