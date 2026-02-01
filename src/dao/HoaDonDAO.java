package dao;

import java.sql.*;
import java.util.List;
import model.CartItem;
import util.DBConnection;

public class HoaDonDAO {

    /**
     * Hàm thanh toán quan trọng nhất:
     * 1. Tạo Hóa đơn
     * 2. Thêm Chi tiết hóa đơn
     * 3. Trừ kho sản phẩm
     * TẤT CẢ PHẢI THÀNH CÔNG HOẶC CÙNG THẤT BẠI (Transaction)
     */
    public boolean thanhToan(String username, double tongTien, List<CartItem> gioHang) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement psHD = null;
        PreparedStatement psCTHD = null;
        PreparedStatement psKho = null;
        
        try {
            // 1. Tắt chế độ tự động lưu để quản lý Transaction thủ công
            conn.setAutoCommit(false); 

            // --- BƯỚC 1: INSERT HÓA ĐƠN ---
            // Sử dụng Statement.RETURN_GENERATED_KEYS để lấy ID vừa tự tăng
            String sqlHD = "INSERT INTO HOADON(NgayLap, TongTien, Username) VALUES(GETDATE(), ?, ?)";
            psHD = conn.prepareStatement(sqlHD, Statement.RETURN_GENERATED_KEYS);
            psHD.setDouble(1, tongTien);
            psHD.setString(2, username);
            psHD.executeUpdate();

            // Lấy MaHD vừa sinh ra
            ResultSet rsKey = psHD.getGeneratedKeys();
            int maHD = 0;
            if (rsKey.next()) {
                maHD = rsKey.getInt(1);
            }

            // --- BƯỚC 2 & 3: INSERT CHI TIẾT & TRỪ KHO ---
            String sqlCTHD = "INSERT INTO CHITIETHOADON(MaHD, MaSP, SoLuong, DonGiaBan, ThanhTien) VALUES(?, ?, ?, ?, ?)";
            String sqlKho = "UPDATE SANPHAM SET SoLuong = SoLuong - ? WHERE MaSP = ?";

            psCTHD = conn.prepareStatement(sqlCTHD);
            psKho = conn.prepareStatement(sqlKho);

            for (CartItem item : gioHang) {
                // Thêm chi tiết
                psCTHD.setInt(1, maHD);
                psCTHD.setInt(2, item.getMaSP());
                psCTHD.setInt(3, item.getSoLuong());
                psCTHD.setDouble(4, item.getDonGia());
                psCTHD.setDouble(5, item.getThanhTien());
                psCTHD.addBatch(); // Gom lệnh lại chạy 1 lần cho nhanh

                // Trừ kho
                psKho.setInt(1, item.getSoLuong());
                psKho.setInt(2, item.getMaSP());
                psKho.addBatch();
            }

            psCTHD.executeBatch();
            psKho.executeBatch();

            // --- CHỐT TRANSACTION ---
            conn.commit(); // Lưu tất cả vào DB
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback(); // Gặp lỗi thì hoàn tác hết
            } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true); // Trả lại trạng thái mặc định
                if (conn != null) conn.close();
            } catch (Exception ex) {}
        }
    }
}