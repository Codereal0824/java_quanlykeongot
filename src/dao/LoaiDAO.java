package dao;

import java.sql.*;
import java.util.ArrayList;
import model.LoaiSanPham;
import util.DBConnection;

public class LoaiDAO {
    public ArrayList<LoaiSanPham> getAll() {
        ArrayList<LoaiSanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM LOAISANPHAM";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new LoaiSanPham(rs.getInt("MaLoai"), rs.getString("TenLoai")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}