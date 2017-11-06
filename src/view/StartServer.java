package view;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import audio.MP3Player;
import bean.Music;
import util.StyleUtil;
import chat.Server;
import dao.MusicDao;

import javax.swing.JScrollPane;

import java.util.Vector;

public class StartServer {

	private JFrame frame;
	private JDialog dialog;
	private JTable table;
	private Server server;	
	JLabel status = new JLabel("服务器已停止");
	JButton close = new JButton("\u5173\u95ED\u670D\u52A1\u5668");
	JButton start = new JButton("\u542F\u52A8\u670D\u52A1\u5668");
	private final JButton button = new JButton("\u7BA1\u7406\u5728\u7EBF\u7528\u6237");
	private final JScrollPane scrollPane = new JScrollPane();
	private static JList<Music> jList;
	private static JLabel nowMusic=new JLabel("当前播放音乐：无");
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartServer window = new StartServer();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StartServer() {
		initialize();
		StyleUtil.changeStyle(frame);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if(server!=null)
				server.close();
			}
		});
		frame.setTitle("\u670D\u52A1\u5668\u7BA1\u7406");
		frame.setBounds(100, 100, 371, 348);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblEwLabel = new JLabel("\u670D\u52A1\u5668\u7BA1\u7406");
		lblEwLabel.setFont(new Font("方正舒体", Font.BOLD, 25));
		lblEwLabel.setBounds(15, 10, 143, 31);
		frame.getContentPane().add(lblEwLabel);
		
		
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				server=new Server();
		        close.setEnabled(true);
		        start.setEnabled(false);
		        status.setText("服务器已启动");
			}
		});
		start.setBounds(15, 51, 93, 29);
		frame.getContentPane().add(start);
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(server!=null)
				server.close();
		        close.setEnabled(false);
		        start.setEnabled(true);
		        status.setText("服务器已停止");
			}
		});
		
		
		close.setEnabled(false);
		close.setBounds(118, 51, 93, 29);
		frame.getContentPane().add(close);
		
		table = new JTable();
		table.setBounds(15, 123, 1, 1);
		frame.getContentPane().add(table);
		
		
		status.setBounds(162, 22, 72, 15);
		frame.getContentPane().add(status);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showManageDialog();
			}
		});
		button.setBounds(221, 51, 134, 29);
		
		frame.getContentPane().add(button);
		
		JLabel label = new JLabel("\u6B4C\u66F2\u5217\u8868");
		label.setFont(new Font("宋体", Font.PLAIN, 16));
		label.setBounds(38, 90, 79, 31);
		frame.getContentPane().add(label);
	
		//歌曲列表
		jList=new JList<>(MusicDao.musicList);
		scrollPane.setBounds(15, 123, 120, 143);
		frame.getContentPane().add(scrollPane);
		scrollPane.setViewportView(jList);
		nowMusic.setLocation(15, 276);
		nowMusic.setSize(120, 15);
		frame.getContentPane().add(nowMusic);
		//初始化管理用户对话框
		dialog=new JDialog(frame,true);
		dialog.setTitle("管理用户");
		dialog.setSize(200,200);
		dialog.getContentPane().setLayout(null);
		dialog.setResizable(false);
		StyleUtil.changeStyle(dialog);
		dialog.setLocationRelativeTo(frame);
		
		
		
	}
	public void showManageDialog()
	{
	       dialog.setVisible(true);    
	}
	//更新音乐标签
	public static void changeMusicStatus(int index,Music m){
		nowMusic.setText("当前播放的音乐:"+m.getName());
		jList.setSelectedIndex(index);
	}
}
