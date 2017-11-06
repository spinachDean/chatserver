package dao;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import bean.Music;

public class MusicDao {
	private static Properties musicProperties=new Properties();
	private static List<Music> musicList;
	private static Music nowMusic;
	static{
		setMusicList();
		nowMusic=musicList.get(0);
	}
	public static Music getNextMusic(int pattern)
	{
		
		int nowIndex=musicList.indexOf(nowMusic);
		switch (pattern) {
		case 0:
			//列表循环播放
			nowMusic=musicList.get((++nowIndex)%musicList.size());
			return nowMusic;
		case 1:
			//单曲循环播放
			nowMusic=musicList.get(nowIndex);
			return nowMusic;
		default:
			nowMusic=musicList.get((++nowIndex)%musicList.size());
			return nowMusic;
		
		}
		
	}
	public static List<Music> setMusicList()
	{
		try {
			musicProperties.load(new FileInputStream("music.properties"));
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		musicList=new ArrayList<Music>();
		Properties p=musicProperties;
		for(Object key:p.keySet())
		{
			
			String k=(String) key;
			Music music=new Music(k.split("-")[0],k.split("-")[1],p.getProperty(k));
			musicList.add(music);
			System.out.println(music.getName());
		}
		return musicList;
	}
	public static Music getMusic(String name,String songer)
	{
		try {
			musicProperties.load(new FileInputStream("music.properties"));
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		String key=name+"-"+songer;
		String path=musicProperties.getProperty(key);
		if(path!=null)
		return new Music(name,songer,path);
		else return null;
	}
	public static void main(String[] args) {
		System.out.println(getNextMusic(0).getName());
		System.out.println(getNextMusic(0).getName());
	}
}
