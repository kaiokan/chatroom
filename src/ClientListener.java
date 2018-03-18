/*資管三A 103403519 劉昌平*/

import java.io.*;
import java.net.*;

public class ClientListener extends Thread {
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket sconnection;
	private String nickname;
	private int ID;
	public static int closeindex = 0;
	public static int comfirmconnection = 0;
	
	ClientListener(Socket socket){
		sconnection = socket;
		try{
			output = new ObjectOutputStream(sconnection.getOutputStream());
	        input = new ObjectInputStream(sconnection.getInputStream());
		}
		catch(IOException ioexception){ioexception.printStackTrace();}
	}
	
	public void run(){
		try{			       
	        while(true){
					String message = (String) input.readObject();	
					
	        		if(message.split(" ", 2)[0].equals("!Nickname=")){				//判斷Nickname可不可行的訊息
	        			nickname = message.split(" ", 2)[1];						//定義nickname
	        			if(checkrepeatnickname(nickname) == 1){ 					//有效nickname
	        				sendmessage("valid nickname");							//告知Client這個Nickname可以使用
							ServerTest.server.clientlist.add(this);					//把這個clientlistner加到clientlist中
							distributeID();											//核發ID
							ServerTest.server.onlinenum += 1;						//上線人數+1
	        				sendmessage("<Server>您已成功連線至Server:'" + sconnection.getLocalAddress() + "' Port:'" + sconnection.getLocalPort() + "'");
							for(ClientListener a: ServerTest.server.clientlist){
								a.sendmessage("!目前連線人數是 " + ServerTest.server.onlinenum);			//更新全部Client的上線人數
								a.sendmessage("<Server> '" + nickname + "'進入聊天室");		//BroadCast上線
							}
							ServerTest.server.messages.append( "<Server> Client:" + sconnection.getInetAddress() + "連線建立，ID:" + ID + "\n");	 //Server面板顯示有人上線
							setcurrentusers();															//Server增加上線中user
							ServerTest.server.onlinepeopletext.setText(Integer.toString(ServerTest.server.onlinenum));			//Server顯示上線人數+1
	        			}
	        			else  	//無效nickname
	        				sendmessage("invalid nickname");
	        		}       		
	        		else{		//其他訊息
	        			ServerTest.server.messages.append(message + "\n");
	        			for(ClientListener a: ServerTest.server.clientlist)					
	    					a.sendmessage(message);							//重設全部client的上線中人數
	        		}		  			
			}
		}	
		catch ( IOException | ClassNotFoundException e) { //Client視窗關閉
			try{
				ServerTest.server.onlinenum -= 1;		//online人數-1
				ServerTest.server.clientlist.remove(this);													//把這個ClientListener從Clientlist中刪除
				ServerTest.server.IDlist.remove(ServerTest.server.IDlist.indexOf(ID));						//把這個ID從IDlist中刪除
				for(ClientListener a: ServerTest.server.clientlist){							
					a.sendmessage("!目前連線人數是 " + ServerTest.server.onlinenum);							//重設全部client的上線中人數
					a.sendmessage("<Server> '" + nickname + "'已經離開聊天室");					//BroadCast下線
				}
				ServerTest.server.onlinepeopletext.setText(Integer.toString(ServerTest.server.onlinenum));	//Server上線人數-1
				ServerTest.server.messages.append("<Server> Client ID:'" + ID + "'已終止連線\n");				//Server自己面板顯示中斷聯繫
				setcurrentusers();																			//更新Server的上線中使用者介面
				output.close();
				input.close();
				sconnection.close();
			}
		    catch(IOException ioexception){ioexception.printStackTrace();} 
		}
	}
	
	private int checkrepeatnickname(String checkname){
		for(ClientListener i : ServerTest.server.clientlist){
			if(i.nickname.equals(checkname))
				return 0;	//Nickname不可行
		}
		return 1; 			//Nickname可行
	}
	
	private void setcurrentusers(){
		String currentusers = "";
		for(ClientListener a: ServerTest.server.clientlist)
				currentusers += a.nickname + "(ID:" + a.ID + ")\n";	
		ServerTest.server.users.setText(currentusers);			
	}
	
	private void distributeID(){
		for(int i=0; i<=ServerTest.server.IDlist.size(); i++){
			if(!ServerTest.server.IDlist.contains(i)){
				ServerTest.server.IDlist.add(i);
				ID = i;
				break;
			}
		}
	}
	
	public void sendmessage(String s){
		try{
			output.flush();	
			output.writeObject(s);	
		}
		catch(IOException ioexception){ioexception.printStackTrace();}
	}
}
