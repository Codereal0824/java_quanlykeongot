package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import util.DBConnection;

public class LoginForm extends JFrame {
    JTextField txtUser;
    JPasswordField txtPass;

    public LoginForm() {
        setTitle("Đăng Nhập Hệ Thống");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // Layout tự do để dễ chỉnh vị trí

        JLabel l1 = new JLabel("Username:");
        l1.setBounds(50, 50, 100, 30);
        add(l1);

        txtUser = new JTextField();
        txtUser.setBounds(150, 50, 150, 30);
        add(txtUser);

        JLabel l2 = new JLabel("Password:");
        l2.setBounds(50, 100, 100, 30);
        add(l2);

        txtPass = new JPasswordField();
        txtPass.setBounds(150, 100, 150, 30);
        add(txtPass);

        JButton btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBounds(150, 150, 120, 30);
        add(btnLogin);

        // Xử lý sự kiện đăng nhập
        btnLogin.addActionListener(e -> checkLogin());
    }

    private void checkLogin() {
        String u = txtUser.getText();
        String p = new String(txtPass.getPassword());

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM TAIKHOAN WHERE Username=? AND Password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, u);
            ps.setString(2, p);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                this.dispose(); // Đóng form đăng nhập
                new QuanLySanPhamForm().setVisible(true); // Mở form chính
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new LoginForm().setVisible(true);
    }
}