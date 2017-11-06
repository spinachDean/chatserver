package util;

import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class StyleUtil {
	static {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public static void changeStyle(JFrame frame) {
		try {

			SwingUtilities.updateComponentTreeUI(frame.getContentPane());
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		setCenter(frame);
	}

	public static void setCenter(JFrame frame) {
		frame.setLocationRelativeTo(null);
	}

	public static void changeStyle(JDialog dialog) {
		try {

			SwingUtilities.updateComponentTreeUI(dialog.getContentPane());
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

	}
	public static AttributeSet getNameStyle()
	{
		SimpleAttributeSet attrset = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrset,12);
		StyleConstants.setBold(attrset, true);
		StyleConstants.setForeground(attrset,new Color(0,0,200));
		return attrset;
	}
	public static AttributeSet getIOStyle()
	{
		SimpleAttributeSet attrset = new SimpleAttributeSet();
		StyleConstants.setFontSize(attrset,12);
		StyleConstants.setBold(attrset, true);
		StyleConstants.setForeground(attrset,Color.RED);
		return attrset;
	}
}
