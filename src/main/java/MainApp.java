import entity.Monhoc;
import entity.Thoikhoabieu;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainApp implements Observer {
    private JPanel panel1;
    private JButton btnTaoMonHoc;
    private JButton btnQuanLy;
    private JButton btnXemDiemDanh;
    private JPanel panelCardLayout;
    private JRadioButton radioButton1;
    private JPanel panelCard1;
    private JPanel panelcard2;
    private JTextField textTenMonHoc;
    private JTextField textMaMonHoc;
    private JList<Thoikhoabieu> listThoiKhoaBieu;
    private JButton btnThêmTKB;
    private JButton btnThemMH;

    private Thoikhoabieu thoikhoabieu;
    private final DefaultListModel<Thoikhoabieu> listModel = new DefaultListModel<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private static MainApp instance;
    private static JFrame frame;

    @Override
    public void update(Observable o, Object data) {

        thoikhoabieu = ((Thoikhoabieu)data);
        listModel.addElement(thoikhoabieu);
        listThoiKhoaBieu.setModel(listModel);
    }

    public MainApp(){
        instance=this;
        btnTaoMonHoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout) panelCardLayout.getLayout()).show(panelCardLayout,"Card2");
            }
        });

        initCardLayoutAddSubject();
    }


    public static void run(){
        frame = new JFrame("Attendance Application");
        frame.setContentPane(new MainApp().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] agrs ){
        frame = new JFrame("Attendance Application");
        frame.setContentPane(new MainApp().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private void updateJListTKB(){
        for (int i =0; i< listModel.getSize();i++){
            listModel.elementAt(i).setMonHoc(textMaMonHoc.getText());
        }
        listThoiKhoaBieu.setModel(listModel);
        listThoiKhoaBieu.updateUI();
    }


    public static MainApp getInstance(){
        if(instance==null){
            instance =new MainApp();
        }
        return instance;
    }

    private void initCardLayoutAddSubject(){



        btnThêmTKB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddTimeTable addTimeTable= new AddTimeTable(textMaMonHoc.getText(), getInstance());
            }
        });

        btnThemMH.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executorService.submit(() ->{
                    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                    EntityManager entityManager = entityManagerFactory.createEntityManager();
                    EntityTransaction entityTransaction = entityManager.getTransaction();
                   lock.writeLock().lock();
                   try {
                       Monhoc monhoc = new Monhoc();
                       monhoc.setMaMonHoc(textMaMonHoc.getText());
                       monhoc.setTenMonHoc(textTenMonHoc.getText());

                       entityTransaction.begin();
                       entityManager.persist(monhoc);
                       for (int i= 0; i<listModel.getSize();i++){
                           entityManager.persist(listModel.elementAt(i));
                       }
                       entityTransaction.commit();
                   }
                   catch (Exception ex)
                   {
                       System.out.println(ex.getMessage());
                   }
                   finally
                   {
                       if (entityTransaction.isActive()) {
                           entityTransaction.rollback();
                       }
                       entityManager.close();
                       entityManagerFactory.close();
                       lock.writeLock().unlock();
                   }
                });

            }
        });
        textMaMonHoc.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateJListTKB();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateJListTKB();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateJListTKB();
            }
        });
    }
}
