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
}