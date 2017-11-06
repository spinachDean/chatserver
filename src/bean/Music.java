package bean;

import java.io.File;
import java.io.Serializable;

public class Music implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1750907264554932630L;
	private String name;
	private String songer;
	private String path;
	private File file;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSonger() {
		return songer;
	}
	public void setSonger(String songer) {
		this.songer = songer;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public Music(String name,String songer,String path)
	{
		this.name=name;
		this.songer=songer;
		this.path=path;
		this.file=new File(path);
	}
	@Override
	public String toString() {
		// TODO 自动生成的方法存根
		return name+"-"+songer;
	}
}
