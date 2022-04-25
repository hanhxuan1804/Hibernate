import entity.Sinhvien;
import entity.Sinhvienmonhoc;

import javax.persistence.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewStudentOfSubject extends JFrame{
    private JPanel panel1;
    private JList listSinhVien;
    private JButton exitButton;
    private JLabel labelMH;
    List<Sinhvien> listSV;

    public ViewStudentOfSubject(String maMonHoc){
        labelMH.setText("Môn học: "+ maMonHoc);
        labelMH.setForeground(Color.BLUE);

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();

        try {
            entityTransaction.begin();
            TypedQuery<Sinhvien> typedQuery = entityManager.createNamedQuery("GetSVofMH", Sinhvien.class);
            typedQuery.setParameter(1,maMonHoc);
            listSV = typedQuery.getResultList();
            System.out.println(listSV);
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
        }
        DefaultListModel<Sinhvien> model = new DefaultListModel<>();

        for (Sinhvien sv:listSV
             ) {
            model.addElement(sv);
        }
        listSinhVien.setModel(model);
        listSinhVien.updateUI();


        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    public  void run(){
        setTitle("Xem danh sách sinh viên:");
        setContentPane(panel1);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        pack();
        setVisible(true);
    }
}
