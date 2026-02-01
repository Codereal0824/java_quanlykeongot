package view;

import model.TaiKhoan;
import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame {
    
    private TaiKhoan taiKhoan; // Lưu thông tin người đang đăng nhập

    public MainForm(TaiKhoan tk) {
        this.taiKhoan = tk;
        initUI();
    }

    private void initUI() {
        setTitle("HỆ THỐNG QUẢN LÝ CỬA HÀNG KẸO NGỌT");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- HEADER: Xin chào ---
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(new Color(0, 102, 204)); // Màu xanh dương
        pnlHeader.setPreferredSize(new Dimension(800, 60));
        
        JLabel lblHello = new JLabel("Xin chào: " + taiKhoan.getHoTen() + " (" + taiKhoan.getVaiTro() + ")");
        lblHello.setForeground(Color.WHITE);
        lblHello.setFont(new Font("Arial", Font.BOLD, 18));
        pnlHeader.add(lblHello);
        
        add(pnlHeader, BorderLayout.NORTH);

        // --- BODY: Các nút chức năng ---
        JPanel pnlBody = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Khoảng cách giữa các nút

        JButton btnBanHang = new JButton("BÁN HÀNG");
        JButton btnSanPham = new JButton("QUẢN LÝ SẢN PHẨM");
        JButton btnDangXuat = new JButton("ĐĂNG XUẤT");

        // Style cho nút to đẹp
        Font fontBtn = new Font("Arial", Font.BOLD, 16);
        Dimension sizeBtn = new Dimension(250, 80);

        btnBanHang.setFont(fontBtn); btnBanHang.setPreferredSize(sizeBtn);
        btnBanHang.setIcon(UIManager.getIcon("FileView.floppyDriveIcon")); // Ví dụ icon
        
        btnSanPham.setFont(fontBtn); btnSanPham.setPreferredSize(sizeBtn);
        btnSanPham.setIcon(UIManager.getIcon("FileView.computerIcon"));

        btnDangXuat.setFont(fontBtn); btnDangXuat.setPreferredSize(sizeBtn);
        btnDangXuat.setBackground(Color.PINK);

        // Sắp xếp nút
        gbc.gridx = 0; gbc.gridy = 0;
        pnlBody.add(btnBanHang, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        pnlBody.add(btnSanPham, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2; // Nút đăng xuất nằm giữa
        pnlBody.add(btnDangXuat, gbc);

        add(pnlBody, BorderLayout.CENTER);

        // --- XỬ LÝ SỰ KIỆN ---

        // 1. Vào trang Bán Hàng
        btnBanHang.addActionListener(e -> {
            new BanHangForm(taiKhoan).setVisible(true);
            // Không dispose MainForm để bán xong quay lại
        });

        // 2. Vào trang Quản Lý Sản Phẩm
        btnSanPham.addActionListener(e -> {
            // Kiểm tra quyền: Chỉ Admin mới được vào kho
            if (taiKhoan.getVaiTro().equalsIgnoreCase("Admin")) {
                new QuanLySanPhamForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Bạn là Nhân viên, không có quyền quản lý kho!");
            }
        });

        // 3. Đăng xuất
        btnDangXuat.addActionListener(e -> {
            this.dispose();
            new LoginForm().setVisible(true);
        });
    }
}