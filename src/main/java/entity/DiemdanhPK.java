package entity;

import java.io.Serializable;

public class DiemdanhPK implements Serializable {
    private int maThoiKhoaBieu;
    private String maSinhVien;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiemdanhPK that = (DiemdanhPK) o;

        if (maThoiKhoaBieu != that.maThoiKhoaBieu) return false;
        if (maSinhVien != null ? !maSinhVien.equals(that.maSinhVien) : that.maSinhVien != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = maThoiKhoaBieu;
        result = 31 * result + (maSinhVien != null ? maSinhVien.hashCode() : 0);
        return result;
    }
}
