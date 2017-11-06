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
	public static final int MESSAGE=1;//1������Ϣ
	public static final int USERIN=2;//2�����û�����
	public static final int USEROUT=3;//3�����û��뿪
	public static final int LOGIN=4;//��¼����
	public static final int REGISTER=5;//ע������
	public static final int TOMESSAGE=6;//˽��
	public static final int USERLIST=7;//�û��б�
	public static final int MUSIC=8;//��������
	//�û��齨
	public static Document docs;//��������
	public static Map<String,JTextPane> chat=new ConcurrentHashMap<String, JTextPane>();//�����
	public static DefaultListModel<String> list;
	public static void init(JTextPane chatPane,DefaultListModel<String> listModel)//��ʼ��
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
				ShowDialog.showErrorMessage("���δ��ʼ��");
			}
			else
				try {
					
					docs.insertString(docs.getLength(),s[1]+"��",StyleUtil.getNameStyle());
					System.out.println(message);
					docs.insertString(docs.getLength(),s[2].replaceAll("<hr>", "\n")+"\n",null);
				} catch (BadLocationException e) {
					ShowDialog.showErrorMessage("���۽����������");
				}
			//��ʾ��Ϣ
		//System.out.println(s[1]);
		break;
		case USERIN:
			if(docs!=null){
			//�û���������
			try {
				docs.insertString(docs.getLength(),"��ӭ"+s[1]+"\n", StyleUtil.getIOStyle());
				list.addElement(s[1]);
			} catch (BadLocationException e) {
				// TODO �Զ����ɵ� catch ��
				ShowDialog.showErrorMessage("�û��б�ҳ���������");
			}
			}
		break;
		case USEROUT:
			try {
				docs.insertString(docs.getLength(),s[1]+"����\n", StyleUtil.getIOStyle());
				list.removeElement(s[1]);
			} catch (BadLocationException e) {
				// TODO �Զ����ɵ� catch ��
				ShowDialog.showErrorMessage("�û��б�ҳ���������");
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
				//��ʼ���ؼ�
				System.out.println("��ʼ���˿ռ�"+s[1]);
			}
			Document temp=jtp.getDocument();
			try {
				temp.insertString(temp.getLength(),s[1]+"��\n",StyleUtil.getNameStyle());
				temp.insertString(temp.getLength(),s[2].replaceAll("<hr>", "\n")+"\n",null);
				System.out.println("�������㷢����Ϣ");
			} catch (BadLocationException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			break;
		case USERLIST:
			for(int j=1;j<s.length;j++)
			{
				list.addElement(s[j]);
				System.out.println("�����"+s[j]);
			}
			break;
		case MUSIC:
			break;
		}
		return null;
	}
	//���ط�����Ϣ�ĸ�ʽ
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
	public static JTextPane getJTextPane(String name)//��ȡ�����
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
			System.out.println("����������"+name);
		chat.put(name,jtp);
		}
		return jtp;
	}
}
