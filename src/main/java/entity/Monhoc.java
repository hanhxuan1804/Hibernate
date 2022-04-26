package entity;

import javax.persistence.*;

@Entity
@NamedQuery(name ="ListMonHoc", query = "SELECT u FROM Monhoc u")
@NamedQuery(name ="GetMonHocByMaMonHoc", query = "SELECT u FROM Monhoc u where u.maMonHoc= ?1")
public class Monhoc {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "MaMonHoc")
    private String maMonHoc;
    @Basic
    @Column(name = "TenMonHoc")
    private String tenMonHoc;

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
}
