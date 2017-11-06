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
	private DataOutputStream out;// 输出流
	private CaptureThread captureThread;
	private int pattern=0;//歌曲播放模式
	public  boolean stauts = true;// 服务器状态
	public  Map<String, Socket> list=new ConcurrentHashMap<String,Socket>();//线程安全的用户表
	public  Music music = MusicDao.getNextMusic(0);
	public AudioServer() {
		start();
	}
	@Override
	public void run() {
		// 初始化ServerSocket,开启录音线程并传送给socket
		try {
			server = new ServerSocket(8899);

			captureThread=new CaptureThread();
			captureThread.start();
			while (stauts) {//只有status为真的时候音频服务器才算启动
				socket = server.accept();
				// 等待socket接入
				System.out.println("wait.........");
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//读取数据
				String name=br.readLine();
				System.out.println("音频服务器："+name);
				list.put(name,socket);
			}
		} catch (IOException e) {
			System.out.println("音频服务器已关闭");
		}
	}
	//改变音乐
	public void changeMusic(String name,String songer)
	{
		this.captureThread.changeMusic(MusicDao.getMusic(name, songer));
	}
	//暂停音频服务器 无法连入
	public void stopServer()
	{
		stauts=false;
	}
	//继续服务器 可以连入
	public void continueServer()
	{
		stauts=true;
	}
	//关闭服务器
	public void closeServer()
	{
		try {
			stopServer();
			server.close();
			captureThread.stop();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	//音频服务线程
	class CaptureThread extends Thread {
		// 创建临时缓冲数组
		byte sendByte[];
		int length;
		FileInputStream fin;
		DataOutputStream out;
		boolean stauts=true;//是否播放
		//发送音乐
		public void send(Socket socket,String name){
			try{
			out = new DataOutputStream(socket.getOutputStream());
			out.write(sendByte,0, length);
			out.flush();
			}
			catch(Exception e)
			{
				System.out.println(name+"连接中断");
				list.remove(name);
			}
		}
		//更换音乐
		public void changeMusic(Music music)
		{
			try {
				fin = new FileInputStream(music.getFile());
				int time = MP3Player.getAudioPlayTime(music.getFile().getAbsolutePath());
				length = (int) (music.getFile().length() / time) * 108;
				sendByte = new byte[length];
			} catch (Exception e) {
				// TODO 自动生成的 catch 块
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
				// 循环录音
				while ((length = fin.read(sendByte, 0, sendByte.length)) > 0) {
					for(String name:list.keySet())
					{
						send(list.get(name),name);
					}
					Thread.sleep(100);
				}
				System.out.println(music.getName()+"传输完成切换下一首");
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
					System.out.println("关闭异常");
				}
			}

		}

	}
	
	
	//用户线程 用来传送音乐
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
                //参考http://www.cnblogs.com/feiyun126/p/3921466.html
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
	                	//保存到配置文件
	                	
	                	//
					} catch (Exception e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
                    

                }
        }
    }

}