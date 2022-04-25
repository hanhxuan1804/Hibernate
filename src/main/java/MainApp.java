import entity.*;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainApp implements Observer {
    public static final String filecsv = "data.csv";

    private JPanel panel1;
    private JButton btnTaoMonHoc;
    private JButton btnQuanLy;
    private JButton btnXemDiemDanh;
    private JPanel panelCardLayout;
    private JPanel panelCard1;
    private JPanel panelcard2;
    private JTextField textTenMonHoc;
    private JTextField textMaMonHoc;
    private JList<Thoikhoabieu> listThoiKhoaBieu;
    private JButton btnThêmTKB;
    private JButton btnThemMH;
    private JComboBox comboBoxPhuongThuc;
    private JPanel cardCheck;
    private JList listSinhVien;
    private JPanel panelQLHSCard;
    private JPanel cardAdd;
    private JPanel cardImport;
    private JComboBox comboBoxMonHoc;
    private JTextField textFieldMSSV;
    private JTextField textFieldHoTen;
    private JTextField textFieldEmail;
    private JFormattedTextField textFieldNgaySinh;
    private JButton btnThemSVMH;
    private JButton xemDanhSáchButton;
    private JTextArea labelThongBao;
    private JButton exportTemplateButton;
    private JButton importFromTemplateButton;
    private JList listSVFromCSV;

    private Thoikhoabieu thoikhoabieu;
    private final DefaultListModel<Thoikhoabieu> listModel = new DefaultListModel<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int userIDCreate;
    private DefaultListModel<CheckListItem> lsvModel;
    private List<Sinhvienmonhoc> sinhvienmonhocList;
    private final DefaultListModel<Sinhvien> csvModel = new DefaultListModel<>();

    private static MainApp instance;
    private static JFrame frame;

    @Override
    public void update(Observable o, Object data) {

        thoikhoabieu = ((Thoikhoabieu)data);
        listModel.addElement(thoikhoabieu);
        listThoiKhoaBieu.setModel(listModel);
    }

    public MainApp(int maxUser){

        instance=this;
        userIDCreate =maxUser;
        loadSinhVienMonHoc();
        btnTaoMonHoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((CardLayout) panelCardLayout.getLayout()).show(panelCardLayout,"Card1");
            }
        });
        btnQuanLy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    initComboBoxMonHoc();
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                }
                ((CardLayout) panelCardLayout.getLayout()).show(panelCardLayout,"Card2");
            }
        });
        initCardLayoutAddSubject();
        initCardlayoutQLHS();
        btnThemSVMH.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboBoxPhuongThuc.getSelectedItem() == "Nhập sinh viên mới"){
                    addNewStudent();
                    updateJListCheckSV();
                }
                else if(comboBoxPhuongThuc.getSelectedItem() ==  "Check chọn"){
                    addByList();
                }
                else{
                    addByCSV();
                    updateJListCheckSV();
                }
            }
        });
        comboBoxMonHoc.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateJListCheckSV();
            }
        });
        exportTemplateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = new File(filecsv);
                try {
                    if(!file.exists()){
                        file.createNewFile();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try{
                    PrintWriter out = new PrintWriter(file);
                    out.println("Mã Sinh Viên,Họ tên,Email,Ngày Sinh(dd/MM/yyyy)");
                    Desktop desktop = Desktop.getDesktop();
                    desktop.edit(file);
                    out.close();
                }catch(IOException ex){
                    System.out.println("Error: " + ex.getMessage());
                }
            }
        });
        importFromTemplateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                csvModel.removeAllElements();
                File file = new File(filecsv);
                try {
                    if(!file.exists()){
                        file.createNewFile();
                        return;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try{
                    BufferedReader in = new BufferedReader(new FileReader(file));
                    String line = in .readLine();
                    line = in .readLine();
                    while(line != null && line != ""){
                        String[] tokens ={"", "", "", "1/1/1990"};
                        String[] split = line.split(",");
                        for (int i = 0; i < split.length; i++) {
                            tokens[i] = split[i];
                        }
                        Sinhvien sv = new Sinhvien();
                        sv.setMaSinhVien(tokens[0]);
                        sv.setHoTen(tokens[1]);
                        sv.setEmail(tokens[2]);
                        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            java.util.Date sd = simpleDateFormat.parse(tokens[3]);
                            java.sql.Date date = new java.sql.Date(sd.getTime());
                            sv.setNgaySinh(date);
                        } catch (ParseException ex) {
                            ex.printStackTrace();}
                        line = in.readLine();
                        csvModel.addElement(sv);
                    }
                    in.close();
                }catch(IOException ex){
                    System.out.println("Error: " + ex.getMessage());
                }
                listSVFromCSV.setModel(csvModel);
                listSVFromCSV.updateUI();
            }
        });
        xemDanhSáchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoadingWindow l= new LoadingWindow();
                l.start();
                ViewStudentOfSubject ss = new ViewStudentOfSubject((String) comboBoxMonHoc.getSelectedItem());
                ss.run();
                l.close();
            }
        });
    }

    private void addByCSV() {
        LoadingWindow l = new LoadingWindow();
        l.start();
        executorService.submit(() ->{
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            lock.writeLock().lock();

            try {
                entityTransaction.begin();
                for (int i = 0; i < csvModel.getSize(); i++) {
                    TypedQuery<Long> checkSVInDatabase = entityManager.createNamedQuery("SVByMaSinhVien",Long.class);
                    checkSVInDatabase.setParameter(1, csvModel.getElementAt(i).getMaSinhVien());
                    System.out.println(checkSVInDatabase.getSingleResult());
                    if(checkSVInDatabase.getSingleResult()==0){
                        lsvModel.addElement(new CheckListItem(csvModel.getElementAt(i).getMaSinhVien() + "   " + csvModel.getElementAt(i).getHoTen()));
                        entityManager.persist(csvModel.getElementAt(i));
                    }
                }
                TypedQuery<String> typedQuery = entityManager.createNamedQuery("GetMSVofMH",String.class);
                typedQuery.setParameter(1,(String) comboBoxMonHoc.getSelectedItem());
                List<String> lsv = typedQuery.getResultList();
                for (int i = 0; i < csvModel.getSize(); i++) {
                    if(!lsv.contains(csvModel.getElementAt(i).getMaSinhVien())){
                        Sinhvienmonhoc svmh = new Sinhvienmonhoc();
                        svmh.setMaMonHoc((String) comboBoxMonHoc.getSelectedItem());
                        svmh.setMaSinhVien(csvModel.getElementAt(i).getMaSinhVien());
                        sinhvienmonhocList.add(svmh);
                        entityManager.persist(svmh);
                    }
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
                l.close();
                updateJListCheckSV();
                JOptionPane.showMessageDialog(panelCard1,
                        "Done: Add student to subject successful!",
                        "Add student...",JOptionPane.INFORMATION_MESSAGE);

            }

        });

    }


    private void addByList() {
        LoadingWindow l = new LoadingWindow();
        l.start();
        executorService.submit(() ->{
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            lock.writeLock().lock();

            try {
                entityTransaction.begin();
                DefaultListModel<CheckListItem> model = (DefaultListModel<CheckListItem>) listSinhVien.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    if(model.elementAt(i).isSelected()){
                        String label =model.elementAt(i).toString();
                        String mssv = label.substring(0,label.indexOf(" "));
                        Sinhvienmonhoc svmh= new Sinhvienmonhoc();
                        svmh.setMaSinhVien(mssv);
                        svmh.setMaMonHoc(comboBoxMonHoc.getSelectedItem().toString());

                        sinhvienmonhocList.add(svmh);
                        entityManager.persist(svmh);
                        model.elementAt(i).setSelected(false);
                    }
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
                l.close();
                updateJListCheckSV();
                JOptionPane.showMessageDialog(panelCard1,
                        "Done: Add student to subject successful!",
                        "Add student...",JOptionPane.INFORMATION_MESSAGE);

            }
        });

    }

    private void loadSinhVienMonHoc() {
        Callable<List<Sinhvienmonhoc>> callable = new Callable<List<Sinhvienmonhoc>>() {
            @Override
            public List<Sinhvienmonhoc> call() throws Exception {
                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                EntityTransaction entityTransaction = entityManager.getTransaction();

                List<Sinhvienmonhoc> result;
                try {
                    entityTransaction.begin();

                    TypedQuery<Sinhvienmonhoc> svmHbyMaMonHoc = entityManager.createNamedQuery("ListSVMH", Sinhvienmonhoc.class);
                    result = svmHbyMaMonHoc.getResultList();

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
        };
        Future<List<Sinhvienmonhoc>> future = executorService.submit(callable);
        try {
            sinhvienmonhocList = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }



    private void updateJListCheckSV() {
        DefaultListModel<CheckListItem> svModel = new DefaultListModel<>();
        List<Sinhvienmonhoc> list = new ArrayList<>();
        for (Sinhvienmonhoc s: sinhvienmonhocList
        ) {
            if(s.getMaMonHoc().equals(comboBoxMonHoc.getSelectedItem())){
                list.add(s);
            }
        }
            for (int i=0;i<lsvModel.getSize();i++ ) {
                boolean check = false;
                for (Sinhvienmonhoc s: list
                ) {
                    if(lsvModel.getElementAt(i).toString().startsWith(s.getMaSinhVien())){
                        check = true;
                    }
                }
                if(!check){
                    svModel.addElement(lsvModel.getElementAt(i));
                }
            }

        listSinhVien.setModel(svModel);
        listSinhVien.updateUI();
    }


    public void run(){
        frame = new JFrame("Attendance Application");
        frame.setContentPane(panel1);
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

    private void addNewStudent(){
        LoadingWindow l = new LoadingWindow();
        l.start();
        executorService.submit(() ->{
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            lock.writeLock().lock();

            try {
                entityTransaction.begin();
                Sinhvien sinhvien = new Sinhvien();
                sinhvien.setHoTen(textFieldHoTen.getText());
                sinhvien.setMaSinhVien(textFieldMSSV.getText());
                sinhvien.setEmail(textFieldEmail.getText());
                SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd/MM/yyyy");
                try {
                    java.util.Date sd = simpleDateFormat.parse(textFieldNgaySinh.getText());
                    java.sql.Date date = new java.sql.Date(sd.getTime());
                    sinhvien.setNgaySinh(date);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                User user = new User();
                user.setId(userIDCreate);
                user.setUsername(sinhvien.getMaSinhVien());
                user.setPassword(LoginWindow.hashPassword(sinhvien.getMaSinhVien().toCharArray()));
                user.setEmail(sinhvien.getEmail());
                user.setType(0);
                user.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));

                Sinhvienmonhoc svmh = new Sinhvienmonhoc();
                svmh.setMaSinhVien(sinhvien.getMaSinhVien());
                svmh.setMaMonHoc((String) comboBoxMonHoc.getSelectedItem());
                sinhvienmonhocList.add(svmh);
                lsvModel.addElement(new CheckListItem(sinhvien.getMaSinhVien() + "   " + sinhvien.getHoTen()));
                updateJListCheckSV();

                entityManager.persist(sinhvien);
                entityManager.persist(user);
                entityManager.persist(svmh);

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
                l.close();
                JOptionPane.showMessageDialog(panelCard1,
                        "Done: Add student to subject successful!",
                        "Add student...",JOptionPane.INFORMATION_MESSAGE);

            }
        });
    }

    public static MainApp getInstance(){
        if(instance==null){
            instance =new MainApp(-1);
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
                LoadingWindow l = new LoadingWindow();
                l.start();
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
                       l.close();
                       JOptionPane.showMessageDialog(panelCard1,
                               "Done: Add subject successful!",
                               "Add subject",JOptionPane.INFORMATION_MESSAGE);

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

    private void createUIComponents() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        textFieldNgaySinh = new JFormattedTextField(dateFormat);
    }

    private void initCardlayoutQLHS(){
        Callable<List<CheckListItem>> callable = new Callable<List<CheckListItem> >() {
            @Override
            public List<CheckListItem>  call() throws Exception {
                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                EntityTransaction entityTransaction = entityManager.getTransaction();

                List<CheckListItem> checkListItems = new ArrayList<>();;
                List<Sinhvien> result;
                try {
                    entityTransaction.begin();

                    TypedQuery<Sinhvien> sinhvienTypedQuery = entityManager.createNamedQuery("ListSinhVien",Sinhvien.class);

                    result = sinhvienTypedQuery.getResultList();

                    entityTransaction.commit();
                } finally {
                    if (entityTransaction.isActive()) {
                        entityTransaction.rollback();
                    }

                    entityManager.close();
                    entityManagerFactory.close();
                }

                for (Sinhvien sinhvien: result
                ) {
                    checkListItems.add(new CheckListItem(sinhvien.getMaSinhVien() + "   " + sinhvien.getHoTen()));
                }
                return checkListItems;
            }
        };
        Future<List<CheckListItem>> future = executorService.submit(callable);

        List<CheckListItem> checkListItems ;
        try {
            checkListItems = future.get();
            lsvModel= new DefaultListModel<>();
            for (CheckListItem c:checkListItems
                 ) {
                lsvModel.addElement(c);
            }
            listSinhVien.setModel(lsvModel);
            listSinhVien.setCellRenderer(new CheckListRenderer());
            listSinhVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listSinhVien.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    JList list = (JList) event.getSource();
                    int index = list.locationToIndex(event.getPoint());// Get index of item
                    // clicked
                    CheckListItem item = (CheckListItem) list.getModel().getElementAt(index);
                    item.setSelected(!item.isSelected()); // Toggle selected state
                    list.repaint(list.getCellBounds(index, index));// Repaint cell
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }

    private  void initComboBoxMonHoc() throws ExecutionException, InterruptedException {
        Callable<List<String>> callableMH = new Callable<List<String> >() {
            @Override
            public List<String>  call() throws Exception {
                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("default");
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                EntityTransaction entityTransaction = entityManager.getTransaction();

                List<String> strings = new ArrayList<>();;
                try {
                    entityTransaction.begin();

                    TypedQuery<Monhoc> monhocTypedQuery = entityManager.createNamedQuery("ListMonHoc",Monhoc.class);

                    List<Monhoc> monhocs = monhocTypedQuery.getResultList();
                    for (Monhoc m:monhocs
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

        List<String> stringList = future1.get();
        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<>();
        for (String s:stringList
        ) {
            cbModel.addElement(s);
        }
        comboBoxMonHoc.setModel(cbModel);

        String[] selections = { "Check chọn", "Nhập sinh viên mới", "Import từ CSV"};
        DefaultComboBoxModel<String> ptModel = new DefaultComboBoxModel<>(selections);
        comboBoxPhuongThuc.setModel(ptModel);
        comboBoxPhuongThuc.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getSource() == comboBoxPhuongThuc) {
                    int i = comboBoxPhuongThuc.getSelectedIndex() +1 ;
                    ((CardLayout) panelQLHSCard.getLayout()).show(panelQLHSCard,"Card"+i);

                }
            }
        });
    }
}

class CheckListItem {

    private String label;
    private boolean isSelected = false;

    public CheckListItem(String label) {
        this.label = label;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return label;
    }
}

class CheckListRenderer extends JCheckBox implements ListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean hasFocus) {
        setEnabled(list.isEnabled());
        setSelected(((CheckListItem) value).isSelected());
        setFont(list.getFont());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        setText(value.toString());
        return this;
    }
}
