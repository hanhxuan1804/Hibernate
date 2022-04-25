package entity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@NamedQuery(name ="ListSinhVien", query = "SELECT u FROM Sinhvien u")
@NamedQuery(name ="SVByMaSinhVien", query = "SELECT COUNT(u) FROM Sinhvien u where u.maSinhVien = ?1")
@NamedQuery(name ="GetSVofMH", query = "SELECT u FROM Sinhvien u, Sinhvienmonhoc sm where u.maSinhVien = sm.maSinhVien and sm.maMonHoc = ?1")
@NamedQuery(name ="GetMSVofMH", query = "SELECT u.maSinhVien FROM Sinhvien u, Sinhvienmonhoc sm where u.maSinhVien = sm.maSinhVien and sm.maMonHoc = ?1")
public class Sinhvien implements Serializable {
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

    @Override
    public String toString()
    {
        return "MSSV: " + maSinhVien + ", Họ tên: " + hoTen;
    }
}
