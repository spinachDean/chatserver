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
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		System.out.println("�������ر�");
	}

	@Override
	public void run() {
		// TODO �Զ����ɵķ������
		try {
			serverSocket = new ServerSocket(port);
			new PrintClient();
			while (running) {
				Socket socket = serverSocket.accept();
				new ServerThread(socket);
			}
		} catch (Exception e) {
			System.out.println("�ѹر�����");
		}
	}

	/**
	 * @author Administrator �ͻ��˴�ӡ�� ������Ϣ
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
		//�������˷���Ϣ
		public void pushMessage(String msg) {
			synchronized (messageList) {
				messageList.add(msg);
			}
			prit = true;
		}

		// ����ǰ�û�����Ϣ
		public void sendMessage(String msg) {
			pw.println(msg);
		}

		// ��ָ���û�����Ϣ
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
									.sendMessage("server", "�û������������"));
							msg = br.readLine();
							continue;
						}
						
						threadsList.put(user, this);
						pw.println("success");
						// �����û��б�
						StringBuilder sb = new StringBuilder();
						sb.append(Message.USERLIST);
						for(String u:userList)
						{
							sb.append("<>"+u);
						}
						this.sendMessage(sb.toString());
						//���û������б�
						userList.add(user);
						this.pushMessage(Message.USERIN + "<>" + user);
						first--;
					} else {
						String[] temp = msg.split("<>");
						if (temp.length > 1) {
							if (!sendMessage(temp[1], temp[0]))
								sendMessage(Message.sendMessage("������",
										"û�и��û�"));
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
				// �û�ǿ���뿪�������쳣
				System.out.println(user + "ǿ���˳�");
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
