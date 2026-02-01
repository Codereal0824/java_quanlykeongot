package model;

public class CartItem {
    private int maSP;
    private String tenSP;
    private int soLuong;
    private double donGia;
    
    public CartItem(int maSP, String tenSP, int soLuong, double donGia) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }
    
    public double getThanhTien() { return soLuong * donGia; }
    
    // Getters
    public int getMaSP() { return maSP; }
    public String getTenSP() { return tenSP; }
    public int getSoLuong() { return soLuong; }
    public double getDonGia() { return donGia; }
    public void setSoLuong(int sl) { this.soLuong = sl; }
}