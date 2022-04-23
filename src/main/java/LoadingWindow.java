import javax.swing.*;
import java.awt.*;

public class LoadingWindow {
    private JFrame frame;
    private  ImageIcon imageIcon;

    public LoadingWindow (){

    }

    public  void start(){
        frame = new JFrame();
        imageIcon = new ImageIcon(this.getClass().getResource("/img/loading.gif"));
        JLabel mylabel = new JLabel(imageIcon);
        mylabel.setSize(64,64);
        frame.setLayout(new BorderLayout());
        frame.add(mylabel,BorderLayout.CENTER);
        frame.setSize(50,100);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    public void close(){
        frame.dispose();
    }

}
