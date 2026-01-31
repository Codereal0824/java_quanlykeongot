package model;

public class SanPham {
    private int maSP;
    private String tenSP;
    private int soLuong;
    private  double giaBan;
    private double giaNhap;
    private String donVi;
    private String hinhAnh;
    private int maLoai;
    
    
    // Constructor (Hàm khởi tạo)
    public SanPham(int maSP, String tenSP, int soLuong, double giaBan, double giaNhap, String donVi, String hinhAnh, int maLoai) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.soLuong = soLuong;
        this.giaBan = giaBan;
        this.giaNhap = giaNhap;
        this.donVi = donVi;
        this.hinhAnh = hinhAnh;
        this.maLoai = maLoai;
    }

    // Getter (Để lấy dữ liệu ra)
    public int getMaSP() { return maSP; }
    public String getTenSP() { return tenSP; }
    public int getSoLuong() { return soLuong; }
    public double getGiaBan() {return giaBan;}
    public double getGiaNhap() {return giaNhap;}
    public String getDonVi() { return donVi; }
    public String getHinhAnh() { return hinhAnh; }
    public int getMaLoai() { return maLoai; }

    // Setter (Nếu em muốn sửa dữ liệu, thêm vào sau nhé)
}