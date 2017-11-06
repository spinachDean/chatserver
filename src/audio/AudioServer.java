package audio;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bean.Music;
import dao.MusicDao;

public class AudioServer extends Thread {
	private ServerSocket server;
	private Socket socket;
	private DataOutputStream out;// �����
	private CaptureThread captureThread;
	private int pattern=0;//��������ģʽ
	public  boolean stauts = true;// ������״̬
	public  Map<String, Socket> list=new ConcurrentHashMap<String,Socket>();//�̰߳�ȫ���û���
	public  Music music = MusicDao.getNextMusic(0);
	public AudioServer() {
		start();
	}
	@Override
	public void run() {
		// ��ʼ��ServerSocket,����¼���̲߳����͸�socket
		try {
			server = new ServerSocket(8899);

			captureThread=new CaptureThread();
			captureThread.start();
			while (stauts) {//ֻ��statusΪ���ʱ����Ƶ��������������
				socket = server.accept();
				// �ȴ�socket����
				System.out.println("wait.........");
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//��ȡ����
				String name=br.readLine();
				System.out.println("��Ƶ��������"+name);
				list.put(name,socket);
			}
		} catch (IOException e) {
			System.out.println("��Ƶ�������ѹر�");
		}
	}
	//�ı�����
	public void changeMusic(String name,String songer)
	{
		this.captureThread.changeMusic(MusicDao.getMusic(name, songer));
	}
	//��ͣ��Ƶ������ �޷�����
	public void stopServer()
	{
		stauts=false;
	}
	//���������� ��������
	public void continueServer()
	{
		stauts=true;
	}
	//�رշ�����
	public void closeServer()
	{
		try {
			stopServer();
			server.close();
			captureThread.stop();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	//��Ƶ�����߳�
	class CaptureThread extends Thread {
		// ������ʱ��������
		byte sendByte[];
		int length;
		FileInputStream fin;
		DataOutputStream out;
		boolean stauts=true;//�Ƿ񲥷�
		//��������
		public void send(Socket socket,String name){
			try{
			out = new DataOutputStream(socket.getOutputStream());
			out.write(sendByte,0, length);
			out.flush();
			}
			catch(Exception e)
			{
				System.out.println(name+"�����ж�");
				list.remove(name);
			}
		}
		//��������
		public void changeMusic(Music music)
		{
			try {
				fin = new FileInputStream(music.getFile());
				int time = MP3Player.getAudioPlayTime(music.getFile().getAbsolutePath());
				length = (int) (music.getFile().length() / time) * 108;
				sendByte = new byte[length];
			} catch (Exception e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
		public void run() {
			try {
				fin = new FileInputStream(music.getFile());
				int time = MP3Player.getAudioPlayTime(music.getFile().getAbsolutePath());
				length = (int) (music.getFile().length() / time) * 108;
				System.out.println(length);
				sendByte = new byte[length];
				while(stauts)
				{
				// ѭ��¼��
				while ((length = fin.read(sendByte, 0, sendByte.length)) > 0) {
					for(String name:list.keySet())
					{
						send(list.get(name),name);
					}
					Thread.sleep(100);
				}
				System.out.println(music.getName()+"��������л���һ��");
				music=MusicDao.getNextMusic(pattern);
				fin = new FileInputStream(music.getFile());
				}
			} catch (Exception e) {
					e.printStackTrace();
			} finally {
				try {
					if (out != null)
						out.close();
					if (fin != null)
						fin.close();
					if (socket != null)
						socket.close();
					
				} catch (Exception e) {
					System.out.println("�ر��쳣");
				}
			}

		}

	}
	
	
	//�û��߳� ������������
	class ServerThread extends Thread{
        private Socket client;
        private DataInputStream din;
        private  FileOutputStream fout = null;
        private byte[] inputByte = new byte[1024];
        private int length;
        private String name;
        public ServerThread(Socket socket,String name){
            try {
                client = socket;         
                //�ο�http://www.cnblogs.com/feiyun126/p/3921466.html
                din = new DataInputStream(client.getInputStream());
                this.name=name;
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void run(){
                while(stauts){
                	try {
                	String filename;
                	while((filename=din.readUTF())!=null);
                	File file=new File("music/"+name+":"+filename);
                    while(!file.exists())
                    	file=new File("music/"+name+":"+filename);
                    
						fout = new FileOutputStream(file);
	                	while((length = din.read(inputByte, 0, inputByte.length))!=-1) {
	                		fout.write(inputByte, 0, length);
	                		fout.flush();
	          
	                    }
	                	//���浽�����ļ�
	                	
	                	//
					} catch (Exception e) {
						// TODO �Զ����ɵ� catch ��
						e.printStackTrace();
					}
                    

                }
        }
    }

}