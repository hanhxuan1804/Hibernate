package entity;

import javax.persistence.*;

@Entity
@IdClass(SinhvienmonhocPK.class)
@NamedQuery(name ="SVMHbyMaMonHoc", query = "SELECT u FROM Sinhvienmonhoc u WHERE u.maMonHoc = ?1 ")
@NamedQuery(name ="ListSVMH", query = "SELECT u FROM Sinhvienmonhoc u ")
public class Sinhvienmonhoc {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "MaMonHoc")
    private String maMonHoc;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "MaSinhVien")
    private String maSinhVien;

    public String getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(String maMonHoc) {
        this.maMonHoc = maMonHoc;
    }

    public String getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSinhVien = maSinhVien;
    }
}
