package dao;

import java.sql.*;
import java.util.ArrayList;
import model.SanPham;
import util.DBConnection;

public class SanPhamDAO {

    // Chức năng: Lấy toàn bộ danh sách kẹo từ SQL về
    public ArrayList<SanPham> getAll() {
        ArrayList<SanPham> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection(); // Gọi ông thần kết nối
        
        try {
            if (conn != null) {
                String sql = "SELECT * FROM SANPHAM";
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql); // Thực thi lệnh SQL

                // Duyệt qua từng dòng dữ liệu lấy được
                while (rs.next()) {
                    int ma = rs.getInt("MaSP");
                    String ten = rs.getString("TenSP");
                    double gia = rs.getDouble("Gia");
                    int sl = rs.getInt("SoLuong");

                    // Gói dữ liệu vào đối tượng SanPham và thêm vào list
                    SanPham sp = new SanPham(ma, ten, gia, sl);
                    list.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Em có thể viết thêm hàm addSanPham(), deleteSanPham() ở đây
 // Thêm phương thức này vào class SanPhamDAO
    public boolean addSanPham(SanPham sp) {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO SANPHAM(TenSP, Gia, SoLuong) VALUES(?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            // Gán giá trị cho 3 dấu chấm hỏi
            ps.setString(1, sp.getTenSP());
            ps.setDouble(2, sp.getGia());
            ps.setInt(3, sp.getSoLuong());

            // Thực thi lệnh. Nếu thêm thành công sẽ trả về số dòng > 0
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
 // Hàm xóa sản phẩm theo Mã
    public boolean deleteSanPham(int maSP) {
        Connection conn = DBConnection.getConnection();
        String sql = "DELETE FROM SANPHAM WHERE MaSP = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSP);
            
            // Thực thi lệnh xóa
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
 // Hàm cập nhật thông tin sản phẩm
    public boolean updateSanPham(SanPham sp) {
        Connection conn = DBConnection.getConnection();
        // Câu lệnh SQL Update
        String sql = "UPDATE SANPHAM SET TenSP=?, Gia=?, SoLuong=? WHERE MaSP=?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            
            // Gán giá trị mới vào dấu ?
            ps.setString(1, sp.getTenSP());
            ps.setDouble(2, sp.getGia());
            ps.setInt(3, sp.getSoLuong());
            
            // Quan trọng: Gán MaSP vào dấu ? cuối cùng (Điều kiện WHERE)
            ps.setInt(4, sp.getMaSP());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}