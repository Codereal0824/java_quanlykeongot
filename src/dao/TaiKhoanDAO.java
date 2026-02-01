package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.TaiKhoan;
import util.DBConnection;

public class TaiKhoanDAO {

    // Hàm kiểm tra đăng nhập
    public TaiKhoan checkLogin(String user, String pass) {
        TaiKhoan tk = null;
        String sql = "SELECT * FROM TAIKHOAN WHERE Username = ? AND Password = ?";
        
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                tk = new TaiKhoan();
                tk.setUsername(rs.getString("Username"));
                tk.setPassword(rs.getString("Password"));
                tk.setHoTen(rs.getString("HoTen"));
                tk.setVaiTro(rs.getString("VaiTro"));
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tk;
    }
}