import entity.User;


import javax.persistence.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;


public class LoginWindow {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton changePasswordButton;
    private static JFrame frame;

    private List<User> userList;

    public LoginWindow(){
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadingWindow l = new LoadingWindow();
                l.start();
                userList = loadUser();
                boolean trueLogin = false;
                for (User user:userList
                     ) {
                    if (Objects.equals(user.getUsername(), usernameField.getText())
                        && Objects.equals(user.getPassword(), hashPassword(passwordField.getPassword()))){
                        trueLogin = true;
                        if(user.getType() == -1){
                            MainApp mainApp = new MainApp(userList.size());
                            mainApp.run();
                            frame.dispose();
                        }
                        else if(user.getType() == 1){
                            StudentMainWindow stu = new StudentMainWindow(user.getUsername());
                            stu.run();
                            frame.dispose();
                        }
                        else{
                            StudentMainWindow stu = new StudentMainWindow(user.getUsername());
                            stu.run();
                            ChangePasswordWindow changePasswordWindow = new ChangePasswordWindow(user.getUsername());
                            changePasswordWindow.run();
                            frame.dispose();
                        }
                    }
                }
                if(!trueLogin){
                    JOptionPane.showMessageDialog(frame,
                            "Error: Username or password incorrect!",
                            "Login fail", JOptionPane.ERROR_MESSAGE);
                }
                l.close();

            }
        });
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangePasswordWindow changePasswordWindow = new ChangePasswordWindow(usernameField.getText());
                changePasswordWindow.run();
            }
        });
    }

    public static void main(String[] args) {
        frame = new JFrame("Login");
        frame.setContentPane((new LoginWindow()).mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static String hashPassword(char[] password){
        String pass = String.copyValueOf(password);
        MessageDigest msg = null;
        try {
            msg = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        byte[] hash = msg.digest(pass.getBytes(StandardCharsets.UTF_8));
        // convert bytes to hexadecimal
        StringBuilder s = new StringBuilder();
        for (byte b : hash) {
            s.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return s.toString();
    }

    public List<User> loadUser() {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        List<User> result;
        try {
            entityTransaction.begin();

            TypedQuery<User> userTypedQuery = entityManager.createNamedQuery("ListUser", User.class);
            //userTypedQuery.setParameter(1, user);
           result = userTypedQuery.getResultList();

            entityTransaction.commit();
        } finally {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
            }

            entityManager.close();
            entityManagerFactory.close();
        }
        return result;
    }
}

