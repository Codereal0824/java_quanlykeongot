package view;

import dao.TaiKhoanDAO;
import model.TaiKhoan;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginForm() {
        setTitle("Đăng Nhập Hệ Thống");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblUser = new JLabel("Tài khoản:");
        lblUser.setBounds(50, 50, 80, 25);
        add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(140, 50, 200, 25);
        add(txtUser);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setBounds(50, 100, 80, 25);
        add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(140, 100, 200, 25);
        add(txtPass);

        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setBounds(140, 150, 120, 30);
        add(btnLogin);

        // Xử lý sự kiện click chuột
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyDangNhap();
            }
        });

        // --- CẬP NHẬT MỚI: Thêm sự kiện nhấn Enter ---
        // Dòng lệnh này biến nút Login thành nút mặc định
        // Khi nhấn Enter ở bất cứ đâu trên form, nó sẽ gọi btnLogin.doClick()
        this.getRootPane().setDefaultButton(btnLogin);
    }

    private void xuLyDangNhap() {
        String user = txtUser.getText();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        TaiKhoanDAO dao = new TaiKhoanDAO();
        TaiKhoan tk = dao.checkLogin(user, pass);

        if (tk != null) {
            // Đăng nhập thành công thì đóng form Login và mở MainForm
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
            this.dispose(); 
            new MainForm(tk).setVisible(true); 
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Nên chạy trên Event Dispatch Thread để an toàn luồng giao diện
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}