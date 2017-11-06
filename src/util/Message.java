package util;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultListModel;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import dao.UserDao;

public class Message {
	public static final int MESSAGE=1;//1代表消息
	public static final int USERIN=2;//2代表用户进入
	public static final int USEROUT=3;//3代表用户离开
	public static final int LOGIN=4;//登录请求
	public static final int REGISTER=5;//注册请求
	public static final int TOMESSAGE=6;//私聊
	public static final int USERLIST=7;//用户列表
	public static final int MUSIC=8;//音乐名字
	//用户组建
	public static Document docs;//评论内容
	public static Map<String,JTextPane> chat=new ConcurrentHashMap<String, JTextPane>();//聊天框
	public static DefaultListModel<String> list;
	public static void init(JTextPane chatPane,DefaultListModel<String> listModel)//初始化
	{
		docs = chatPane.getDocument();
		list=listModel;
	}
	public static SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public static String parseMessage(String message)
	{
		String[] s=message.split("<>");
		//System.out.println(message);
		Integer i=Integer.parseInt(s[0]);
		switch(i)
		{
		case MESSAGE:
			if(docs==null){
				ShowDialog.showErrorMessage("组件未初始化");
			}
			else
				try {
					
					docs.insertString(docs.getLength(),s[1]+"：",StyleUtil.getNameStyle());
					System.out.println(message);
					docs.insertString(docs.getLength(),s[2].replaceAll("<hr>", "\n")+"\n",null);
				} catch (BadLocationException e) {
					ShowDialog.showErrorMessage("评论界面出现问题");
				}
			//显示消息
		//System.out.println(s[1]);
		break;
		case USERIN:
			if(docs!=null){
			//用户进入后更新
			try {
				docs.insertString(docs.getLength(),"欢迎"+s[1]+"\n", StyleUtil.getIOStyle());
				list.addElement(s[1]);
			} catch (BadLocationException e) {
				// TODO 自动生成的 catch 块
				ShowDialog.showErrorMessage("用户列表页面出现问题");
			}
			}
		break;
		case USEROUT:
			try {
				docs.insertString(docs.getLength(),s[1]+"下线\n", StyleUtil.getIOStyle());
				list.removeElement(s[1]);
			} catch (BadLocationException e) {
				// TODO 自动生成的 catch 块
				ShowDialog.showErrorMessage("用户列表页面出现问题");
			}
			break;
		case LOGIN:
			if(UserDao.login(s[1], s[2]))
				return s[1];
		case REGISTER:
			System.out.println(s[1]+":"+s[2]);
			break;
		case TOMESSAGE:
			JTextPane jtp = null;
			for(String n:chat.keySet())
			{
				if(n.equals(s[1]))
					jtp=chat.get(n);
			}
			if(jtp==null)
			{
				jtp=new JTextPane();
				chat.put(s[1],jtp);
				//初始化控件
				System.out.println("初始化了空间"+s[1]);
			}
			Document temp=jtp.getDocument();
			try {
				temp.insertString(temp.getLength(),s[1]+"：\n",StyleUtil.getNameStyle());
				temp.insertString(temp.getLength(),s[2].replaceAll("<hr>", "\n")+"\n",null);
				System.out.println("有人向你发送信息");
			} catch (BadLocationException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			break;
		case USERLIST:
			for(int j=1;j<s.length;j++)
			{
				list.addElement(s[j]);
				System.out.println("添加了"+s[j]);
			}
			break;
		case MUSIC:
			break;
		}
		return null;
	}
	//返回发送消息的格式
	public static String sendMessage(String user,String content)
	{
		return Message.MESSAGE+"<>"+ user+ "<>" + content;
	}
	public static String inUser(String user,String content)
	{
		return Message.USERIN+"<>"+ user;
	}
	public static String outUser(String user,String content)
	{
		return Message.USEROUT+"<>"+ user;
	}
	public static String login(String username,String password)
	{
		return Message.LOGIN+"<>"+username+"<>"+password;
	}
	public static String register(String username,String password)
	{
		return Message.REGISTER+"<>"+username+"<>"+password;
	}
	public static String sendToMessage(String fromUser,String content)
	{
		return Message.TOMESSAGE+"<>"+fromUser+"<>"+content;
	}
	public static JTextPane getJTextPane(String name)//获取聊天框
	{
		JTextPane jtp = null;
		for(String n:chat.keySet())
		{
			if(n.equals(name))
				jtp=chat.get(n);
		}
		if(jtp==null)
		{
			jtp=new JTextPane();
			System.out.println("添加了聊天框"+name);
		chat.put(name,jtp);
		}
		return jtp;
	}
}
