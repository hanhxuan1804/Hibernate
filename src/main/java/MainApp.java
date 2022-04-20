import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApp {
    private JPanel panel1;
    private JButton btnTaoMonHoc;
    private JButton btnQuanLy;
    private JButton btnXemDiemDanh;
    private JPanel panelCardLayout;
    private JRadioButton radioButton1;
    private JPanel panelCard1;
    private JPanel panelcard2;
    private JTextField textTenMonHoc;
    private JTextField textField1;
    private JList listThoiKhoaBieu;
    private JButton btnThêmTKB;
    private JButton tạoButton;


    public MainApp(){
        btnTaoMonHoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout) panelCardLayout.getLayout()).show(panelCardLayout,"Card2");
            }
        });
    }


    public static void run(){
        JFrame frame = new JFrame("Attendance Application");
        frame.setContentPane(new MainApp().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
