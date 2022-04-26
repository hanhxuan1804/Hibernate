
import com.fasterxml.classmate.AnnotationConfiguration;
import com.fasterxml.classmate.AnnotationInclusion;
import entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChangePasswordWindow {
    private JPasswordField passwordFieldAgain;
    private JPasswordField passwordFieldNew;
    private JPasswordField passwordFieldOld;
    private JTextField textFieldUsername;
    private JButton btnHuy;
    private JButton btnLuu;
    private JTextArea textAreaThongbao;
    private JPanel panel1;
    private  JFrame frame;

    public ChangePasswordWindow(String username){
        textFieldUsername.setText(username);
        textAreaThongbao.setForeground(Color.RED);

        btnLuu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textAreaThongbao.setText("");
                if(Objects.equals(textFieldUsername.getText(), "") || passwordFieldNew.getPassword().length == 0
                || passwordFieldOld.getPassword().length == 0|| passwordFieldAgain.getPassword().length == 0){
                    textAreaThongbao.setText("Bạn chưa điền đủ thông tin!");
                    return;
                }
                if(!Arrays.equals(passwordFieldNew.getPassword(), passwordFieldAgain.getPassword())){
                    textAreaThongbao.setText("Mật khẩu mới bạn điền không giống nhau!");
                }
                else {
                    User user = checkUser(textFieldUsername.getText());
                    if(user== null){
                        textAreaThongbao.setText("Tài khoản hoặc mật khẩu cũ không đúng!");
                        return;
                    }
                    String hpass= LoginWindow.hashPassword(passwordFieldOld.getPassword());
                    if(!Objects.equals(hpass,user.getPassword())){
                        textAreaThongbao.setText("Tài khoản hoặc mật khẩu cũ không đúng!");
                        return;
                    }
                    user.setPassword(LoginWindow.hashPassword(passwordFieldNew.getPassword()));
                    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                    EntityManager entityManager = entityManagerFactory.createEntityManager();
                    EntityTransaction entityTransaction = entityManager.getTransaction();

                    try {
                        entityTransaction.begin();

                       Query query = entityManager.createQuery("update User u set u.password = ?1, u.type = 1 where u.username = ?2");
                       query.setParameter(1,user.getPassword());
                       query.setParameter(2,user.getUsername());
                        query.executeUpdate();
                        entityTransaction.commit();
                    } finally {
                        if (entityTransaction.isActive()) {
                            entityTransaction.rollback();
                        }

                        entityManager.close();
                        entityManagerFactory.close();
                    }

                    JOptionPane.showMessageDialog(frame,
                            "Done: change password successful!",
                            "Change password", JOptionPane.PLAIN_MESSAGE);
                    frame.dispose();
                }
            }
        });
        btnHuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
    }

    public User checkUser(String username){

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        User result = null;
        try {
            entityTransaction.begin();

            TypedQuery<User> userTypedQuery = entityManager.createQuery("SELECT u FROM User u where u.username=?1", User.class);
            userTypedQuery.setParameter(1, username);
            result = userTypedQuery.getSingleResult();

            entityTransaction.commit();
        }
        catch (
               NoResultException exception){
            return result;

        }
        finally {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
            }

            entityManager.close();
            entityManagerFactory.close();
        }
        return result;
    }
    public  void run(){
        frame= new JFrame("Change password");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(panel1);
        frame.setSize(600,400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }
}
