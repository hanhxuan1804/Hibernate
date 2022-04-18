package entity;

import java.io.Serializable;

public class SinhvienmonhocPK implements Serializable {
    private String maMonHoc;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SinhvienmonhocPK that = (SinhvienmonhocPK) o;

        if (maMonHoc != null ? !maMonHoc.equals(that.maMonHoc) : that.maMonHoc != null) return false;
        if (maSinhVien != null ? !maSinhVien.equals(that.maSinhVien) : that.maSinhVien != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = maMonHoc != null ? maMonHoc.hashCode() : 0;
        result = 31 * result + (maSinhVien != null ? maSinhVien.hashCode() : 0);
        return result;
    }
}
