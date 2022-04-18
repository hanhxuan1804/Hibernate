package entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Sinhvien {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "MaSinhVien")
    private String maSinhVien;
    @Basic
    @Column(name = "HoTen")
    private String hoTen;
    @Basic
    @Column(name = "Email")
    private String email;
    @Basic
    @Column(name = "NgaySinh")
    private Date ngaySinh;

    public String getMaSinhVien() {
        return maSinhVien;
    }

    public void setMaSinhVien(String maSinhVien) {
        this.maSinhVien = maSinhVien;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
}
