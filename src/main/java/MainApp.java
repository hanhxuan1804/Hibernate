import entity.Monhoc;
import entity.Sinhvien;
import entity.Thoikhoabieu;

import javax.persistence.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainApp implements Observer {
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
    private JButton btnThemDSSV;
    private JPanel panelQLHSCard;
    private JPanel cardAdd;
    private JCheckBox checkBox1;
    private JPanel cardImport;
    private JCheckBox checkBox2;
    private JComboBox comboBoxMonHoc;

    private Thoikhoabieu thoikhoabieu;
    private final DefaultListModel<Thoikhoabieu> listModel = new DefaultListModel<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
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
        frame.setContentPane((new MainApp()).panel1);
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

    private void createUIComponents() throws ExecutionException, InterruptedException {
        initCardlayoutQLHS();

    }

    private void initCardlayoutQLHS() throws ExecutionException, InterruptedException {
        executorService=Executors.newFixedThreadPool(5);
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
            listSinhVien = new JList<>(checkListItems.toArray());
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

        String[] selections = { "Check chọn", "Nhập sinh viên mới", "Import từ CSV"};
        comboBoxPhuongThuc= new JComboBox(selections);
        //comboBoxPhuongThuc.setSelectedIndex(0);
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
