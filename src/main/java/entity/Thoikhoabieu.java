package entity;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;

@Entity
@NamedQuery(name ="GetTKBByMaMonHoc", query = "SELECT u FROM Thoikhoabieu u where u.monHoc= ?1")
public class Thoikhoabieu {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "MaThoiKhoaBieu")
    private int maThoiKhoaBieu;
    @Basic
    @Column(name = "MonHoc")
    private String monHoc;
    @Basic
    @Column(name = "NgayBatDau")
    private Date ngayBatDau;
    @Basic
    @Column(name = "NgayKetThuc")
    private Date ngayKetThuc;
    @Basic
    @Column(name = "ThuTrongTuan")
    private Integer thuTrongTuan;
    @Basic
    @Column(name = "GioBatDau")
    private Time gioBatDau;
    @Basic
    @Column(name = "GioKetThuc")
    private Time gioKetThuc;
    @Basic
    @Column(name = "TenPhongHoc")
    private String tenPhongHoc;

    public int getMaThoiKhoaBieu() {
        return maThoiKhoaBieu;
    }

    public void setMaThoiKhoaBieu(int maThoiKhoaBieu) {
        this.maThoiKhoaBieu = maThoiKhoaBieu;
    }

    public String getMonHoc() {
        return monHoc;
    }

    public void setMonHoc(String monHoc) {
        this.monHoc = monHoc;
    }

    public Date getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public Integer getThuTrongTuan() {
        return thuTrongTuan;
    }

    public void setThuTrongTuan(Integer thuTrongTuan) {
        this.thuTrongTuan = thuTrongTuan;
    }

    public Time getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(Time gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public Time getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(Time gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public String getTenPhongHoc() {
        return tenPhongHoc;
    }

    public void setTenPhongHoc(String tenPhongHoc) {
        this.tenPhongHoc = tenPhongHoc;
    }

    @Override
    public String toString(){
        return  "Môn học: " +  monHoc +",   Phòng: "+tenPhongHoc+
                ",   Ngày bắt đầu: "+ ngayBatDau +",    Thứ: "+thuTrongTuan+",    Giờ: "+gioBatDau+"->"+
                gioKetThuc;
    }

}
