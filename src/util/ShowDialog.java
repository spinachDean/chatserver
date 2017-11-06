package util;

import javax.swing.JOptionPane;

public class ShowDialog {
	public static void showErrorMessage(String message)
	{
		JOptionPane.showMessageDialog(null,message,"error", JOptionPane.ERROR_MESSAGE);
	}
	public static void showInfoMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message,"ÌבÊ¾",JOptionPane.INFORMATION_MESSAGE);
	}
	public static void main(String[] args) {
		showErrorMessage("123");
	}
}
