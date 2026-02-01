package model;

public class LoaiSanPham {
    private int maLoai;
    private String tenLoai;

    public LoaiSanPham(int maLoai, String tenLoai) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
    }

    public int getMaLoai() { return maLoai; }
    public String getTenLoai() { return tenLoai; }

    // QUAN TRỌNG: JComboBox sẽ hiển thị giá trị từ hàm toString()
    @Override
    public String toString() {
        return tenLoai; 
    }
}