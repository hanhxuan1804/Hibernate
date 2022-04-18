import javax.swing.*;

public class MainApp {
    private JButton button1;
    private JPanel panel1;


    public static void run(){
        JFrame frame = new JFrame("Attendance Application");
        frame.setContentPane(new MainApp().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
