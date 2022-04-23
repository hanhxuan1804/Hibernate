import entity.User;


import javax.persistence.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class LoginWindow {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private static JFrame frame;


    public LoginWindow(){

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadingWindow l = new LoadingWindow();
                l.start();
                Callable<User> callable = new Callable<User>() {
                    @Override
                    public User call() throws Exception {
                        return findUser(usernameField.getText());
                    }
                };
                try {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    Future<User> future = executor.submit(callable);


                    //wait for the task to finished
                    User user = future.get();
                    if (user.getId() == -1 || user.getType() != 1 ||
                            !hashPassword(passwordField.getPassword()).equals(user.getPassword())) {

                        JOptionPane.showMessageDialog(frame,
                                "Error: Username or password incorrect!",
                                "Login fail", JOptionPane.ERROR_MESSAGE);

                    } else {
                        //TODO: kiểm tra học sinh hay giáo vụ
                        // Nếu học sinh đăng nhập lần đầu thì phải đổi mật khẩu
                        frame.dispose();
                        MainApp.run();
                    }
                    l.close();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
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

    public String hashPassword(char[] password){
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

    public User findUser(String user) {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        User result;
        try {
            entityTransaction.begin();

            TypedQuery<User> userTypedQuery = entityManager.createNamedQuery("UserByUsername", User.class);
            userTypedQuery.setParameter(1, user);
            if(userTypedQuery.getResultList().isEmpty()){
                result= new User();
                result.setId(-1);
            }
            else{result = userTypedQuery.getSingleResult();}

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

