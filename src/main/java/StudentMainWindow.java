import entity.Diemdanh;
import entity.Monhoc;
import entity.Sinhvienmonhoc;
import entity.Thoikhoabieu;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StudentMainWindow extends JFrame{
    private JPanel panel1;
    private JButton btnCNDiemDanh;
    private JButton btnCNXemKetQua;
    private JComboBox comboMonHoc;
    private JTextArea textAreaThongbao;
    private JPanel cardLayouMain;
    private JLabel lbMaxMonHoc;
    private JLabel lbTenMonHoc;
    private JLabel lbThoiGian;
    private JLabel lbThu;
    private JLabel lbPhongHoc;
    private JButton btnDiemDanh;
    private JTable tableDiemDanh;

    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private List<Sinhvienmonhoc> list ;
    private int tuan;
    private int maTKB;

    public StudentMainWindow(String maSinhVien){
        textAreaThongbao.setForeground(Color.RED);
        inintCardLayoutDiemDanh(maSinhVien);
        innitCardLayoutXemDiemDanh(maSinhVien);
        updateCardLayoutDiemdanh();
        if(hasAttendance(maSinhVien)){btnDiemDanh.setEnabled(false);}
        btnCNDiemDanh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout) cardLayouMain.getLayout()).show(cardLayouMain,"Card1");
            }
        });
        btnCNXemKetQua.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout) cardLayouMain.getLayout()).show(cardLayouMain,"Card2");
            }
        });
        comboMonHoc.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                textAreaThongbao.setText("");
                updateCardLayoutDiemdanh();
                if(hasAttendance(maSinhVien)){
                    textAreaThongbao.setText("Bạn đã điểm danh buổi học này rồi!");
                    btnDiemDanh.setEnabled(false);}
                innitCardLayoutXemDiemDanh(maSinhVien);
            }
        });
        btnDiemDanh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executorService.submit(()->{
                    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                    EntityManager entityManager = entityManagerFactory.createEntityManager();
                    EntityTransaction entityTransaction = entityManager.getTransaction();
                    Thoikhoabieu thoikhoabieu;
                    try {
                        entityTransaction.begin();

                        Diemdanh diemdanh= new Diemdanh();
                        diemdanh.setMaSinhVien(maSinhVien);
                        diemdanh.setTuan(tuan);
                        diemdanh.setMaThoiKhoaBieu(maTKB);
                        entityManager.persist(diemdanh);

                        entityTransaction.commit();
                    } finally {
                        if (entityTransaction.isActive()) {
                            entityTransaction.rollback();
                        }

                        entityManager.close();
                        entityManagerFactory.close();
                    }

                });
                btnDiemDanh.setEnabled(false);
            }
        });
    }

    private void innitCardLayoutXemDiemDanh(String maSinhVien) {
        executorService.submit(() ->{
            DefaultTableModel model = new DefaultTableModel(new Object[]{"Tuần 1","Tuần 2","Tuần 3","Tuần 4","Tuần 5",
                    "Tuần 6","Tuần 7","Tuần 8","Tuần 9","Tuần 10",
                    "Tuần 11","Tuần 12","Tuần 13","Tuần 14","Tuần 15"},0){
            };
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            lock.readLock().lock();

            try {
                entityTransaction.begin();
                TypedQuery<Integer> getTuan = entityManager.createNamedQuery("ListTuanOfSVMH",Integer.class);
                getTuan.setParameter(1,(String) comboMonHoc.getSelectedItem());
                getTuan.setParameter(2,maSinhVien);
                List<Integer> lt = getTuan.getResultList();

                Object[] data = new Object[15];

                for (int i = 0; i < 15; i++) {
                    if(i<tuan){data[i]= "Vắng";}
                    else data[i]="Chưa có";
                }
                for (Integer integer : lt) {
                    data[integer] = "Có";
                }
                model.addRow(data);
                SwingUtilities.invokeAndWait(()->{
                    tableDiemDanh.setModel(model);
                    tableDiemDanh.setRowHeight(20);
                    tableDiemDanh.setEnabled(false);
                    tableDiemDanh.updateUI();
                });
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
                lock.readLock().unlock();
            }
        });

    }

    private void updateCardLayoutDiemdanh() {
        Callable<Monhoc> callableMH = new Callable<Monhoc>() {
            @Override
            public Monhoc call() throws Exception {
                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                EntityTransaction entityTransaction = entityManager.getTransaction();
                Monhoc monhoc;
                try {
                    entityTransaction.begin();

                    TypedQuery<Monhoc> monhocTypedQuery = entityManager.createNamedQuery("GetMonHocByMaMonHoc",Monhoc.class);
                    monhocTypedQuery.setParameter(1,(String) comboMonHoc.getSelectedItem());
                    monhoc = monhocTypedQuery.getSingleResult();

                    entityTransaction.commit();
                } finally {
                    if (entityTransaction.isActive()) {
                        entityTransaction.rollback();
                    }

                    entityManager.close();
                    entityManagerFactory.close();
                }
                return monhoc;
            }
        };
        Callable<Thoikhoabieu> callableTKB = new Callable<Thoikhoabieu>() {
            @Override
            public Thoikhoabieu call() throws Exception {
                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                EntityTransaction entityTransaction = entityManager.getTransaction();
                Thoikhoabieu thoikhoabieu;
                try {
                    entityTransaction.begin();

                    TypedQuery<Thoikhoabieu> monhocTypedQuery = entityManager.createNamedQuery("GetTKBByMaMonHoc",Thoikhoabieu.class);
                    monhocTypedQuery.setParameter(1,(String) comboMonHoc.getSelectedItem());
                    thoikhoabieu = monhocTypedQuery.getSingleResult();

                    entityTransaction.commit();
                } finally {
                    if (entityTransaction.isActive()) {
                        entityTransaction.rollback();
                    }

                    entityManager.close();
                    entityManagerFactory.close();
                }
                return thoikhoabieu;
            }
        };

        Future<Monhoc> future1 = executorService.submit(callableMH);
        Future<Thoikhoabieu> future2 = executorService.submit(callableTKB);
        try {
            Monhoc monhoc = future1.get();
            Thoikhoabieu thoikhoabieu = future2.get();
            lbMaxMonHoc.setText(monhoc.getMaMonHoc());
            lbTenMonHoc.setText(monhoc.getTenMonHoc());
            lbThoiGian.setText(thoikhoabieu.getGioBatDau() +" - " +thoikhoabieu.getGioKetThuc());
            lbThu.setText(thoikhoabieu.getThuTrongTuan().toString());
            lbPhongHoc.setText(thoikhoabieu.getTenPhongHoc());
            btnDiemDanh.setEnabled(isDateTimeSuitable(thoikhoabieu.getNgayBatDau(), thoikhoabieu.getNgayKetThuc(), thoikhoabieu.getGioBatDau(), thoikhoabieu.getGioKetThuc()));
            if(!btnDiemDanh.isEnabled()){
                textAreaThongbao.setText("Vui lòng điểm danh vào đúng buổi học!");
            }
            maTKB = thoikhoabieu.getMaThoiKhoaBieu();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }


    private void inintCardLayoutDiemDanh(String maSinhVien){
        Callable<List<String>> callableMH = new Callable<List<String> >() {
            @Override
            public List<String>  call() throws Exception {
                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                EntityTransaction entityTransaction = entityManager.getTransaction();

                List<String> strings = new ArrayList<>();;
                try {
                    entityTransaction.begin();

                    TypedQuery<Sinhvienmonhoc> monhocTypedQuery = entityManager.createNamedQuery("MonHocOfMaSinhVien",Sinhvienmonhoc.class);
                    monhocTypedQuery.setParameter(1, maSinhVien);
                    list = monhocTypedQuery.getResultList();
                    for (Sinhvienmonhoc m:list
                    ) {
                        strings.add(m.getMaMonHoc());
                    }

                    entityTransaction.commit();
                } finally {
                    if (entityTransaction.isActive()) {
                        entityTransaction.rollback();
                    }

                    entityManager.close();
                    entityManagerFactory.close();
                }

                return strings;
            }
        };
        Future<List<String>> future1 = executorService.submit(callableMH);
        try {
            List<String> stringList = future1.get();
            DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>();
            for (String s:stringList
            ) {
                cbModel.addElement(s);
            }
            comboMonHoc.setModel(cbModel);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    private boolean isDateTimeSuitable(Date startDate,Date endDate, Time startTime, Time endTime){
        java.util.Date now = new java.util.Date();
        Date currentDate = new java.sql.Date(now.getTime());
        if(currentDate.before(startDate)|| currentDate.after(endDate)) return false;
        Time currentTime = new java.sql.Time(now.getTime());
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");
        Date ts = null;
        Date cr = null;
        Date te = null;
        try {
            ts = new Date(parser.parse(startTime.toString()).getTime());
            cr = new Date(parser.parse(currentTime.toString()).getTime());
            te = new Date(parser.parse(endTime.toString()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert cr != null;
        if(cr.before(ts) || cr.after(te)) return false;

        java.util.GregorianCalendar curentCal = new java.util.GregorianCalendar();
        curentCal.setTime(currentDate);
        java.util.GregorianCalendar startCal = new java.util.GregorianCalendar();
        startCal.setTime(startDate);

        if(curentCal.get(Calendar.DAY_OF_WEEK)!= startCal.get(Calendar.DAY_OF_WEEK)){
            return false;
        }
        Duration diff = Duration.between(startDate.toLocalDate().atStartOfDay(), currentDate.toLocalDate().atStartOfDay());
        long diffDays = diff.toDays();
        tuan = (int) (diffDays/7+1);
        System.out.println(tuan);
        return true;
    }

    private boolean hasAttendance(String masinhvien){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        Long check = 1L;
        try {
            entityTransaction.begin();

            TypedQuery<Long> monhocTypedQuery = entityManager.createNamedQuery("CheckExistDiemDanh",Long.class);
            monhocTypedQuery.setParameter(1,maTKB);
            monhocTypedQuery.setParameter(2,masinhvien);
            monhocTypedQuery.setParameter(3,tuan);
            check = monhocTypedQuery.getSingleResult();

            entityTransaction.commit();
        } finally {
            if (entityTransaction.isActive()) {
                entityTransaction.rollback();
            }

            entityManager.close();
            entityManagerFactory.close();

        }

        return check != 0;
    }


    public void run(){
        setTitle("Attendance Application");
        setContentPane(panel1);
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
