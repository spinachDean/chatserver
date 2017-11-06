package audio;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import util.ShowDialog;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
/***
 * ���ֲ�������
 * @author lt
 *  time 2016-7-5
 */
 //�̳����߳���Thread
public class MP3Player extends Thread{
    Player player;
    InputStream inputStream;
    //���췽��
    public MP3Player(File file) {
        try {
			inputStream=new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
    }
    public MP3Player(InputStream in) {
       inputStream=in;
    }
    //��дrun����
    @Override
    public void run() {
        super.run();
        try {
            play();     
        } catch (FileNotFoundException | JavaLayerException e) {
            // TODO Auto-generated catch block
        	System.out.println("��Ƶ����ʧ�ܣ����������");
        	ShowDialog.showErrorMessage("��Ƶ����ʧ�ܣ����������");
            this.stop();
        }
    }
    //���ŷ���
    public void play() throws FileNotFoundException, JavaLayerException {

            BufferedInputStream buffer =
                    new BufferedInputStream(inputStream);
            player = new Player(buffer);
            player.play();
    }
    public static int getAudioPlayTime(String mp3) throws IOException, BitstreamException
    {
        File file = new File(mp3);
        FileInputStream fis=new FileInputStream(file);
        int b=fis.available();
        Bitstream bt=new Bitstream(fis);
        Header h = bt.readFrame();
        int time = (int) h.total_ms(b);
        System.out.println(time);
        return time;
    }
public static void main(String[] args) throws FileNotFoundException {
	MP3Player mp3Player=new MP3Player(new FileInputStream(new File("E://�ջ����.mp3")));
	mp3Player.start();
	try {
		System.out.println(123);
	} catch (Exception e) {
		// TODO �Զ����ɵ� catch ��
		e.printStackTrace();
	}
	
}
}
