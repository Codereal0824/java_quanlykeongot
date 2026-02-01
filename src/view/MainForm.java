package view;

import model.TaiKhoan;
import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame {
    
    private TaiKhoan taiKhoan;
    private JPanel pnlRight;
    private CardLayout cardLayout;
    
    // BIẾN MỚI: Lưu nút đang được chọn
    private JButton btnActive = null;
    
    // Khai báo biến nút bấm để dùng chung
    private JButton btnBanHang, btnSanPham, btnThongKe, btnDangXuat;

    public MainForm(TaiKhoan tk) {
        this.taiKhoan = tk;
        initUI();
    }

    private void initUI() {
        setTitle("HỆ THỐNG QUẢN LÝ CỬA HÀNG KẸO NGỌT");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR ---
        JPanel pnlSidebar = new JPanel();
        pnlSidebar.setBackground(new Color(44, 62, 80));
        pnlSidebar.setPreferredSize(new Dimension(250, 800));
        pnlSidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        JLabel lblUser = new JLabel("<html><center>QUẢN LÝ<br>" + taiKhoan.getHoTen().toUpperCase() + "</center></html>");
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Arial", Font.BOLD, 16));
        lblUser.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        pnlSidebar.add(lblUser);

        // Tạo các nút
        btnBanHang = createMenuButton(" BÁN HÀNG", "FileView.floppyDriveIcon");
        btnSanPham = createMenuButton(" SẢN PHẨM", "FileView.computerIcon");
        btnThongKe = createMenuButton(" THỐNG KÊ", "FileView.hardDriveIcon");
        btnDangXuat = createMenuButton(" ĐĂNG XUẤT", null);
        btnDangXuat.setBackground(new Color(192, 57, 43));

        pnlSidebar.add(btnBanHang);
        pnlSidebar.add(btnSanPham);
        pnlSidebar.add(btnThongKe);
        pnlSidebar.add(new JLabel("                                        "));
        pnlSidebar.add(btnDangXuat);

        add(pnlSidebar, BorderLayout.WEST);

        // --- 2. MAIN CONTENT ---
        cardLayout = new CardLayout();
        pnlRight = new JPanel(cardLayout);
        pnlRight.setBackground(Color.WHITE);

        QuanLySanPhamPanel sanPhamPanel = new QuanLySanPhamPanel();
        BanHangPanel banHangPanel = new BanHangPanel(taiKhoan, sanPhamPanel);

        pnlRight.add(new JPanel(), "TrangChu"); 
        pnlRight.add(banHangPanel, "BanHang");
        pnlRight.add(sanPhamPanel, "SanPham");
        pnlRight.add(new ThongKePanel(), "ThongKe");

        add(pnlRight, BorderLayout.CENTER);

        // --- 3. XỬ LÝ SỰ KIỆN ---

        btnBanHang.addActionListener(e -> {
            cardLayout.show(pnlRight, "BanHang");
            setActiveButton(btnBanHang); // Kích hoạt hiệu ứng đổi màu
        });

        btnSanPham.addActionListener(e -> {
            if (taiKhoan.getVaiTro().equalsIgnoreCase("Admin")) {
                cardLayout.show(pnlRight, "SanPham");
                setActiveButton(btnSanPham);
            } else {
                JOptionPane.showMessageDialog(this, "Bạn không có quyền vào Quản lý sản phẩm!");
            }
        });

        btnThongKe.addActionListener(e -> {
            if (taiKhoan.getVaiTro().equalsIgnoreCase("Admin")) {
                cardLayout.show(pnlRight, "ThongKe");
                setActiveButton(btnThongKe);
            } else {
                JOptionPane.showMessageDialog(this, "Bạn không có quyền vào Thống kê!");
            }
        });

        btnDangXuat.addActionListener(e -> {
            this.dispose();
            new LoginForm().setVisible(true);
        });
        
        // Mặc định chọn Bán Hàng khi mở lên
        setActiveButton(btnBanHang);
        cardLayout.show(pnlRight, "BanHang");
    }

    // Hàm tạo nút có hiệu ứng Hover
    private JButton createMenuButton(String text, String iconKey) {
        JButton btn = new JButton(text);
        if (iconKey != null) btn.setIcon(UIManager.getIcon(iconKey));
        
        btn.setPreferredSize(new Dimension(230, 50));
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);

        // Hover: Chỉ đổi màu nếu nút đó KHÔNG phải là nút đang Active
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != btnActive) btn.setBackground(new Color(70, 90, 110));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != btnActive) btn.setBackground(new Color(52, 73, 94));
            }
        });
        return btn;
    }

    // Hàm xử lý đổi màu nút Active
    private void setActiveButton(JButton btn) {
        // 1. Trả nút cũ về màu gốc
        if (btnActive != null) {
            btnActive.setBackground(new Color(52, 73, 94));
        }
        // 2. Nút mới thành màu nổi (Xanh lá - Teal)
        btnActive = btn;
        btnActive.setBackground(new Color(26, 188, 156)); 
    }
}