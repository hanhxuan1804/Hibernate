import entity.Thoikhoabieu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Time;
import java.text.*;
import java.util.Observable;
import java.util.Observer;


public class AddTimeTable extends JFrame {

    private JLabel labelMaMonHoc;
    private JFormattedTextField textMaTKB;
    private JFormattedTextField ngayBatDau;
    private JFormattedTextField ngayKetThuc;
    private JFormattedTextField thuTrongTuan;
    private JFormattedTextField gioBatDau;
    private JFormattedTextField gioKetThuc;
    private JTextField textTenPhongHoc;
    private JPanel tkbpanel;
    private JButton btnHuy;
    private JButton btnThem;

    Thoikhoabieu tkb = new Thoikhoabieu();





    public AddTimeTable(String MaMonHoc, MainApp mainApp)
    {
        // Create Observable and add Observer
        final MessageObservable observable = new MessageObservable();
        observable.addObserver( mainApp);

        this.setContentPane(tkbpanel);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);



        labelMaMonHoc.setText(MaMonHoc);

        btnHuy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();
            }
        });

        btnThem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tkb.setMonHoc(labelMaMonHoc.getText());
                tkb.setMaThoiKhoaBieu(Integer.parseInt(textMaTKB.getText()));
                SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd/MM/yyyy");
                try {
                    java.util.Date sd = simpleDateFormat.parse(ngayBatDau.getText());
                    Date startDate = new Date(sd.getTime());
                    tkb.setNgayBatDau(startDate);
                    java.util.Date ed =simpleDateFormat.parse(ngayKetThuc.getText());
                    Date endDate = new Date((ed.getTime()));
                    tkb.setNgayKetThuc(endDate);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                tkb.setThuTrongTuan(Integer.parseInt(thuTrongTuan.getText()));
                DateFormat timeFormat= new SimpleDateFormat("HH:mm");
                try {
                    java.util.Date stime = timeFormat.parse(gioBatDau.getText());
                    Time st = new Time(stime.getTime());
                    tkb.setGioBatDau(st);
                    java.util.Date etime = timeFormat.parse(gioKetThuc.getText());
                    Time et = new Time(etime.getTime());
                    tkb.setGioKetThuc(et);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
                tkb.setTenPhongHoc(textTenPhongHoc.getText());
                observable.changeData(tkb);
                dispose();
            }
        });
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        ngayBatDau = new JFormattedTextField(dateFormat);
        ngayKetThuc = new JFormattedTextField(dateFormat);
        thuTrongTuan = new JFormattedTextField(NumberFormat.getNumberInstance());
        textMaTKB = new JFormattedTextField(NumberFormat.getNumberInstance());
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        gioBatDau = new JFormattedTextField(timeFormat);
        gioKetThuc = new JFormattedTextField(timeFormat);
    }

    public Thoikhoabieu getTkb() {
        return tkb;
    }
}

class MessageObservable extends Observable {
    MessageObservable() {
        super();
    }
    void changeData(Object data) {
        setChanged(); // the two methods of Observable class
        notifyObservers(data);
    }
}
