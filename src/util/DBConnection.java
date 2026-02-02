package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Cấu hình thông số kết nối
            // Lưu ý: encrypt=true;trustServerCertificate=true là BẮT BUỘC với SQL Server bản mới
            String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyCuaHangBanhNgot;encrypt=true;trustServerCertificate=true";
            String user = "sa"; // Tài khoản mặc định của SQL Server
            String pass = "123"; // Mật khẩu lúc em cài SQL Server

            // Đăng ký Driver (Bắt buộc với project cũ, project mới có thể bỏ qua nhưng nên giữ)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            // Mở kết nối
            conn = DriverManager.getConnection(url, user, pass);
            // System.out.println("Kết nối thành công!"); // Dùng để test
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Lỗi kết nối CSDL!");
        }
        return conn;
    }
}