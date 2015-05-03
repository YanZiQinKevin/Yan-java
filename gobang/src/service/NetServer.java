package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import comment.Protocol;

public class NetServer {
 
private static Map<Socket,String> users = new HashMap<Socket,String>();	
//private String currentNickname;


public void init(){
	try {
	ServerSocket ss = new ServerSocket(8889);
	boolean stop = true;
	
		while(stop){
			Socket s = ss.accept();
		    new ServerThread(s).start();
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

class ServerThread extends Thread{
	private Socket socket;
	public ServerThread(Socket socket){
		this.socket = socket;
	}
	public void run(){
		
		try {
			BufferedReader br = 
				new BufferedReader(
						new InputStreamReader(this.socket.getInputStream()));
			
			PrintWriter pw = 
				new PrintWriter(this.socket.getOutputStream());
			String line = "";
			
			
			String input = "";
			while((input = br.readLine()) != null){
			if(input.startsWith(Protocol.NICKNAME)){
				//$!kaka$!
				    if(NetServer.users.size()>2){break;}
				    
					String name = getRealName(input);
				   if(NetServer.users.containsKey(name) == true){
					  //昵称不能用重新选一个 
					  pw.println(Protocol.NICKNAMEREP);
					  pw.flush();
					 
				   }else{
					  NetServer.users.put( socket,name);
					  pw.println(Protocol.NICKNAMEOK);
					  pw.flush();
					  if(NetServer.users.keySet().size()==1){
						  pw.println(Protocol.FIRST);
						  pw.flush();	  
					  }
				   }
			}else if(input.startsWith(Protocol.XY)){
				
				for(Socket s : NetServer.users.keySet()){
					if(s != socket){//发送给另一个玩家
					 PrintWriter out = new PrintWriter(s.getOutputStream());
					 out.println(input);
					 out.flush();
					}
				}
				
			}
			}
		} catch (IOException e) {
			//e.printStackTrace();
			NetServer.users.remove(socket);
		}
		
	}
	
	private String getRealName(String name){
		return name.substring(2,name.length()-2);
	}
	

	}


public static void main(String[] args) {
  	new NetServer().init();
}
}
