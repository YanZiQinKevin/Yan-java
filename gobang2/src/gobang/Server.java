package gobang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 服务器
 * 
 * @author soft01
 * 
 */
public class Server {
	private static Map<Socket, String> users = new HashMap<Socket, String>();

	public Server() throws Exception {
		ServerSocket ss = new ServerSocket(8889);
		boolean stop = true;
		while (stop) {
			Socket s = ss.accept();
			new ServerThread(s).start();

		}
	}

	class ServerThread extends Thread {
		private Socket s;

		public ServerThread(Socket s) {
			this.s = s;
		}

		@Override
		public void run() {
			// 服务器读取客户端发送数据
			// 昵称X，y
			// 服务器发送数据到客户端
			// 昵称可用，昵称重复
			// 你是先手
			System.out.println("以连接");
			BufferedReader br = null;
			PrintWriter pw = null;
			try {
				br = new BufferedReader(new InputStreamReader(s
						.getInputStream()));
				pw = new PrintWriter(s.getOutputStream());

				String input = "";
				// System.out.println(br.readLine());

				// System.out.println((input = br.readLine()));
				while ((input = br.readLine()) != null) {

					System.out.println(1);
					if (input.startsWith(Protocol.NICK)) {
						input = getRealName(input);

						boolean rs = users.containsValue(input);
						if (rs == true) {
							pw.println(Protocol.NICKREP);
							pw.flush();
						} else {
							users.put(s, input);
							pw.println(Protocol.NICKOK);
							System.out.println(Protocol.NICKOK);
							pw.flush();
							// 2判断有几个玩家，如果有一个玩家
							// 这个玩家就是先手
							if (users.size() % 2 == 1) {
								System.out.println("第一个玩家");
								PrintWriter out = new PrintWriter(s
										.getOutputStream());
								out.println(Protocol.FIRST);
								out.flush();
							}

						}
					} else if (input.startsWith(Protocol.XY)) {
						Set<Socket> sss = users.keySet();
						for (Socket s2 : sss) {
							if (s != s2) {
								System.out.println(s);
								PrintWriter out = new PrintWriter(s2
										.getOutputStream());
								out.println(input);
								out.flush();
							}
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private String getRealName(String name) {
			return name.substring(2, name.length() - 2);
		}
	}

	public static void main(String[] arge) throws Exception {
		new Server();
	}

}
