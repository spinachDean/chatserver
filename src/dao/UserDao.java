package dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class UserDao {
	
	/**
	 * ��¼����
	 * @param username
	 * @param password
	 * @return
	 */
	private static Properties userProperties=new Properties();
	public static boolean login(String username,String password)
	{
		try {
			userProperties.load(new FileInputStream("user.properties"));
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		String p=userProperties.getProperty(username);
		if(p!=null&&p.equals(password))return true;
		else return false;
	}
	/**
	 * ע�᷽��
	 * @param username
	 * @param password
	 * @return
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static boolean register(String username,String password) throws FileNotFoundException, IOException
	{
		try {
			userProperties.load(new FileInputStream("user.properties"));
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		String p=userProperties.getProperty(username);
		if(p==null){
		userProperties.setProperty(username, password);
		userProperties.store(new FileOutputStream("user.properties"),new Date().toString());
		return true;
		}
		else
		return false;
		
	}
}
