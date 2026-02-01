package dao;

import java.sql.*;
import java.util.ArrayList;
import util.DBConnection;

public class ThongKeDAO {

    // 1. HÀM LẤY TỔNG DOANH THU (Ngày/Tuần/Tháng)
    public double getTongDoanhThu(String kieuThongKe) {
        double tong = 0;
        String sql = "SELECT SUM(TongTien) FROM HoaDon WHERE ";
        
        if (kieuThongKe.equals("Ngày")) {
            sql += "CAST(NgayLap AS DATE) = CAST(GETDATE() AS DATE)";
        } else if (kieuThongKe.equals("Tuần")) {
            sql += "DATEPART(WEEK, NgayLap) = DATEPART(WEEK, GETDATE()) AND YEAR(NgayLap) = YEAR(GETDATE())";
        } else { // Tháng
            sql += "MONTH(NgayLap) = MONTH(GETDATE()) AND YEAR(NgayLap) = YEAR(GETDATE())";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) tong = rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return tong;
    }

    // 2. HÀM LẤY DANH SÁCH SẢN PHẨM BÁN CHẠY (Ngày/Tuần/Tháng)
    public ArrayList<Object[]> getSanPhamBanChay(String kieuThongKe) {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT sp.TenSP, SUM(ct.SoLuong) as DaBan, SUM(ct.ThanhTien) as TongThu "
                   + "FROM ChiTietHoaDon ct JOIN SANPHAM sp ON ct.MaSP = sp.MaSP "
                   + "JOIN HoaDon hd ON ct.MaHD = hd.MaHD WHERE ";

        if (kieuThongKe.equals("Ngày")) sql += "CAST(hd.NgayLap AS DATE) = CAST(GETDATE() AS DATE) ";
        else if (kieuThongKe.equals("Tuần")) sql += "DATEPART(WEEK, hd.NgayLap) = DATEPART(WEEK, GETDATE()) ";
        else sql += "MONTH(hd.NgayLap) = MONTH(GETDATE()) ";
        
        sql += "GROUP BY sp.TenSP ORDER BY DaBan DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{rs.getString("TenSP"), rs.getInt("DaBan"), rs.getDouble("TongThu")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 3. HÀM DÙNG CHO CÁC QUÝ (Tháng bắt đầu -> Tháng kết thúc)
    public double getDoanhThuTheoKhoangThang(int start, int end) {
        double tong = 0;
        String sql = "SELECT SUM(TongTien) FROM HoaDon WHERE MONTH(NgayLap) BETWEEN ? AND ? AND YEAR(NgayLap) = YEAR(GETDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, start);
            ps.setInt(2, end);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) tong = rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return tong;
    }

    public ArrayList<Object[]> getSPTheoKhoangThang(int start, int end) {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT sp.TenSP, SUM(ct.SoLuong) as DaBan, SUM(ct.ThanhTien) as TongThu "
                   + "FROM ChiTietHoaDon ct JOIN SANPHAM sp ON ct.MaSP = sp.MaSP "
                   + "JOIN HoaDon hd ON ct.MaHD = hd.MaHD "
                   + "WHERE MONTH(hd.NgayLap) BETWEEN ? AND ? AND YEAR(hd.NgayLap) = YEAR(GETDATE()) "
                   + "GROUP BY sp.TenSP ORDER BY DaBan DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, start);
            ps.setInt(2, end);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{rs.getString("TenSP"), rs.getInt("DaBan"), rs.getDouble("TongThu")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}