package entity;

import javax.persistence.*;

@Entity
@NamedQuery(name ="ListDDofMH", query = "SELECT d FROM Diemdanh d , Thoikhoabieu t where d.maThoiKhoaBieu = t.maThoiKhoaBieu and t.monHoc = ?1")
@NamedQuery(name ="ListTuanOfSVMH", query = "SELECT d.tuan FROM Diemdanh d , Thoikhoabieu t where d.maThoiKhoaBieu = t.maThoiKhoaBieu and t.monHoc = ?1 and d.maSinhVien=?2")
@NamedQuery(name ="CheckExistDiemDanh", query = "SELECT COUNT(d) FROM Diemdanh d  where d.maThoiKhoaBieu = ?1 and d.maSinhVien = ?2 and d.tuan=?3")
public class Diemdanh {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "MaThoiKhoaBieu")
    private int maThoiKhoaBieu;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "MaSinhVien")
    private String maSinhVien;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Tuan")
    private Integer tuan;

    public int getMaThoiKhoaBieu() {
        return maThoiKhoaBieu;
    }

    public void setMaThoiKhoaBieu(int maThoiKhoaBieu) {
        this.maThoiKhoaBieu = maThoiKhoaBieu;
    }

    public String getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public Integer getTuan() {
        return tuan;
    }

    public void setTuan(Integer tuan) {
        this.tuan = tuan;
    }

    @Override
    public String toString(){
        return maThoiKhoaBieu+ ", MSSV: " + maSinhVien+ ", Tuần: "+tuan;
    }
}
