package model;

public class SanPham {
    private int maSP;
    private String tenSP;
    private double gia;
    private int soLuong;

    // Constructor (Hàm khởi tạo)
    public SanPham(int maSP, String tenSP, double gia, int soLuong) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.gia = gia;
        this.soLuong = soLuong;
    }

    // Getter (Để lấy dữ liệu ra)
    public int getMaSP() { return maSP; }
    public String getTenSP() { return tenSP; }
    public double getGia() { return gia; }
    public int getSoLuong() { return soLuong; }

    // Setter (Nếu em muốn sửa dữ liệu, thêm vào sau nhé)
}