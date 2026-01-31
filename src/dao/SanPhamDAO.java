package dao;

import java.sql.*;
import java.util.ArrayList;
import model.SanPham;
import util.DBConnection; // Kiểm tra xem file kết nối của bạn tên là DBConnection hay DBContext nhé

public class SanPhamDAO {

    // 1. Lấy toàn bộ danh sách (Đã bổ sung đủ cột)
    public ArrayList<SanPham> getAll() {
        ArrayList<SanPham> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        
        try {
            if (conn != null) {
                String sql = "SELECT * FROM SANPHAM";
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql);

                while (rs.next()) {
                    // Lấy dữ liệu từ cột SQL
                    int ma = rs.getInt("MaSP");
                    String ten = rs.getString("TenSP");
                    int sl = rs.getInt("SoLuong");
                    double giaBan = rs.getDouble("GiaBan");       // Sửa từ 'Gia' thành 'GiaBan'
                    double giaNhap = rs.getDouble("GiaNhap");     // Mới thêm
                    String donVi = rs.getString("DonViTinh");     // Mới thêm
                    String hinh = rs.getString("HinhAnh");        // Mới thêm
                    int maLoai = rs.getInt("MaLoai");             // Mới thêm

                    // Tạo đối tượng SanPham (Constructor phải khớp với thứ tự này trong Model)
                    SanPham sp = new SanPham(ma, ten, sl, giaBan, giaNhap, donVi, hinh, maLoai);
                    list.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 2. Thêm mới sản phẩm (Bỏ qua MaSP vì tự tăng)
    public boolean addSanPham(SanPham sp) {
        Connection conn = DBConnection.getConnection();
        // Câu lệnh INSERT đủ 7 cột (trừ MaSP)
        String sql = "INSERT INTO SANPHAM(TenSP, SoLuong, GiaBan, GiaNhap, DonViTinh, HinhAnh, MaLoai) VALUES(?, ?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sp.getTenSP());
            ps.setInt(2, sp.getSoLuong());
            ps.setDouble(3, sp.getGiaBan());
            ps.setDouble(4, sp.getGiaNhap());
            ps.setString(5, sp.getDonVi());
            ps.setString(6, sp.getHinhAnh());
            ps.setInt(7, sp.getMaLoai()); // Lưu ý: Nếu chưa có combo box chọn loại, tạm thời để mặc định là 1

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. Xóa sản phẩm
    public boolean deleteSanPham(int maSP) {
        Connection conn = DBConnection.getConnection();
        String sql = "DELETE FROM SANPHAM WHERE MaSP = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, maSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            // Nếu lỗi do khóa ngoại (đã bán trong hóa đơn), in ra để biết
            System.out.println("Không xóa được do sản phẩm này đã có trong hóa đơn!");
            e.printStackTrace();
        }
        return false;
    }

    // 4. Cập nhật sản phẩm (Sửa đủ thông tin)
    public boolean updateSanPham(SanPham sp) {
        Connection conn = DBConnection.getConnection();
        // Câu lệnh UPDATE cập nhật tất cả thông tin dựa trên MaSP
        String sql = "UPDATE SANPHAM SET TenSP=?, SoLuong=?, GiaBan=?, GiaNhap=?, DonViTinh=?, HinhAnh=?, MaLoai=? WHERE MaSP=?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, sp.getTenSP());
            ps.setInt(2, sp.getSoLuong());
            ps.setDouble(3, sp.getGiaBan());
            ps.setDouble(4, sp.getGiaNhap());
            ps.setString(5, sp.getDonVi());
            ps.setString(6, sp.getHinhAnh());
            ps.setInt(7, sp.getMaLoai());
            
            // Tham số cuối cùng là MaSP để làm điều kiện WHERE
            ps.setInt(8, sp.getMaSP());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}