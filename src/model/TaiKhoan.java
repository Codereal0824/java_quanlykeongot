package model;

public class TaiKhoan {
    private String username;
    private String password;
    private String hoTen;
    private String vaiTro;

    public TaiKhoan() { }

    public TaiKhoan(String username, String password, String hoTen, String vaiTro) {
        this.username = username;
        this.password = password;
        this.hoTen = hoTen;
        this.vaiTro = vaiTro;
    }

    // Getter và Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }
    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
}