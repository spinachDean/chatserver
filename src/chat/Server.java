package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import dao.UserDao;
import audio.AudioServer;
import util.Message;

public class Server extends Thread {

	private static int port = 8898;
	public static boolean prit = false;
	public static Vector<String> userList = new Vector<String>();
	public static LinkedList<String> messageList = new LinkedList<>();
	public static Map<String, ServerThread> threadsList = new ConcurrentHashMap<String, ServerThread>();
	private boolean running = true;
	private ServerSocket serverSocket;
	private AudioServer audioServer;

	public Server() {
		audioServer = new AudioServer();
		start();
	}

	public void close() {
		running = false;
		audioServer.closeServer();
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		System.out.println("服务器关闭");
	}

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		try {
			serverSocket = new ServerSocket(port);
			new PrintClient();
			while (running) {
				Socket socket = serverSocket.accept();
				new ServerThread(socket);
			}
		} catch (Exception e) {
			System.out.println("已关闭连接");
		}
	}

	/**
	 * @author Administrator 客户端打印类 传输消息
	 */
	class PrintClient extends Thread {
		public PrintClient() {
			start();
		}

		public void run() {
			while (running) {
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (prit == true) {
					String msg = messageList.getFirst();
					// System.out.println("prepare to sent to Clent");
					for (String name : threadsList.keySet()) {
						ServerThread sThread = threadsList.get(name);
						sThread.sendMessage(msg);
					}
					synchronized (messageList) {
						messageList.removeFirst();
					}
					prit = messageList.size() > 0 ? true : false;
				}
			}
		}
	}

	class ServerThread extends Thread {
		private Socket client;
		private PrintWriter pw;
		private BufferedReader br;
		private String user;

		public ServerThread(Socket socket) {
			try {
				client = socket;
				pw = new PrintWriter(client.getOutputStream(), true);
				br = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				// pw.println(Message.sendMessage("server",
				// "please input your name"));
				start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//给所有人发消息
		public void pushMessage(String msg) {
			synchronized (messageList) {
				messageList.add(msg);
			}
			prit = true;
		}

		// 给当前用户发消息
		public void sendMessage(String msg) {
			pw.println(msg);
		}

		// 给指定用户发消息
		public boolean sendMessage(String msg, String toUserName) {
			try {
				threadsList.get(toUserName).sendMessage(
						Message.sendToMessage(user, msg));
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		public void run() {
			try {
				int first = 1;
				String msg = br.readLine();
				while (running) {
					if (first == 1) {

						user = Message.parseMessage(msg);
						if (user == null) {
							pw.println(Message
									.sendMessage("server", "用户名或密码错误"));
							msg = br.readLine();
							continue;
						}
						
						threadsList.put(user, this);
						pw.println("success");
						// 发送用户列表
						StringBuilder sb = new StringBuilder();
						sb.append(Message.USERLIST);
						for(String u:userList)
						{
							sb.append("<>"+u);
						}
						this.sendMessage(sb.toString());
						//将用户填入列表
						userList.add(user);
						this.pushMessage(Message.USERIN + "<>" + user);
						first--;
					} else {
						String[] temp = msg.split("<>");
						if (temp.length > 1) {
							if (!sendMessage(temp[1], temp[0]))
								sendMessage(Message.sendMessage("服务器",
										"没有该用户"));
						} else
							this.pushMessage(Message.sendMessage(user, msg));
						// System.out.println("the prit is " + prit + " " +
						// messageList.size());
					}
					msg = br.readLine();
					// System.out.println(msg);
				}
				pw.println("bye Client");
			} catch (Exception e) {
				// 用户强制离开后会出现异常
				System.out.println(user + "强制退出");
			} finally {
				try {
					br.close();
					pw.close();
					client.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				threadsList.remove(this);
				userList.remove(user);
				pushMessage(Message.USEROUT + "<>" + user);
			}
		}
	}

	public static void main(String[] srgs) throws Exception {

		new Server();

	}
}
