package entity;

import javax.persistence.*;

@Entity
public class Monhoc {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "MaMonHoc")
    private String maMonHoc;
    @Basic
    @Column(name = "TenMonHoc")
    private String tenMonHoc;
    @Basic
    @Column(name = "ThoiKhoaBieu")
    private String thoiKhoaBieu;

    public String getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(String maMonHoc) {
        this.maMonHoc = maMonHoc;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }

    public String getThoiKhoaBieu() {
        return thoiKhoaBieu;
    }

    public void setThoiKhoaBieu(String thoiKhoaBieu) {
        this.thoiKhoaBieu = thoiKhoaBieu;
    }
}
