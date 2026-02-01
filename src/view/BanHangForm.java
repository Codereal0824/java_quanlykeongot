package view;

import dao.SanPhamDAO;
import dao.HoaDonDAO;
import model.SanPham;
import model.CartItem;
import model.TaiKhoan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BanHangForm extends JFrame {
    private TaiKhoan taiKhoanHienTai; // Lưu người đang đăng nhập
    private JTable tblSanPham;
    private JTable tblGioHang;
    private DefaultTableModel modelSanPham;
    private DefaultTableModel modelGioHang;
    private JLabel lblTongTien;
    private List<CartItem> gioHang; // List lưu tạm các món đã chọn

    public BanHangForm(TaiKhoan tk) {
        this.taiKhoanHienTai = tk;
        this.gioHang = new ArrayList<>();
        
        setTitle("QUẢN LÝ BÁN HÀNG - Nhân viên: " + tk.getHoTen());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- PHẦN TRÁI: DANH SÁCH SẢN PHẨM ---
        JPanel pnlTrai = new JPanel(new BorderLayout());
        pnlTrai.setBorder(BorderFactory.createTitledBorder("Danh sách kẹo"));
        
        modelSanPham = new DefaultTableModel(new Object[]{"Mã", "Tên SP", "Giá bán", "Tồn kho"}, 0);
        tblSanPham = new JTable(modelSanPham);
        loadDataSanPham(); // Gọi hàm load dữ liệu
        pnlTrai.add(new JScrollPane(tblSanPham), BorderLayout.CENTER);

        JButton btnThem = new JButton("Thêm vào giỏ >>");
        pnlTrai.add(btnThem, BorderLayout.SOUTH);

        // --- PHẦN PHẢI: GIỎ HÀNG ---
        JPanel pnlPhai = new JPanel(new BorderLayout());
        pnlPhai.setBorder(BorderFactory.createTitledBorder("Giỏ hàng khách mua"));

        modelGioHang = new DefaultTableModel(new Object[]{"Mã", "Tên SP", "SL", "Đơn giá", "Thành tiền"}, 0);
        tblGioHang = new JTable(modelGioHang);
        pnlPhai.add(new JScrollPane(tblGioHang), BorderLayout.CENTER);

        JPanel pnlThanhToan = new JPanel(new GridLayout(3, 1));
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(Color.RED);
        
        JButton btnXoa = new JButton("Xóa món đã chọn");
        JButton btnThanhToan = new JButton("THANH TOÁN & IN HÓA ĐƠN");
        btnThanhToan.setBackground(new Color(0, 153, 76)); // Màu xanh lá
        btnThanhToan.setForeground(Color.WHITE);

        pnlThanhToan.add(lblTongTien);
        pnlThanhToan.add(btnXoa);
        pnlThanhToan.add(btnThanhToan);
        pnlPhai.add(pnlThanhToan, BorderLayout.SOUTH);

        // Chia đôi màn hình
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlTrai, pnlPhai);
        splitPane.setDividerLocation(500);
        add(splitPane, BorderLayout.CENTER);

        // --- SỰ KIỆN ---
        
        // 1. Thêm vào giỏ
        btnThem.addActionListener(e -> {
            int row = tblSanPham.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn kẹo cần bán trước!");
                return;
            }

            int maSP = (int) tblSanPham.getValueAt(row, 0);
            String tenSP = (String) tblSanPham.getValueAt(row, 1);
            double giaBan = (double) tblSanPham.getValueAt(row, 2);
            int tonKho = (int) tblSanPham.getValueAt(row, 3);

            String slStr = JOptionPane.showInputDialog(this, "Nhập số lượng khách mua (" + tenSP + "):");
            try {
                int slMua = Integer.parseInt(slStr);
                if (slMua <= 0) throw new NumberFormatException();
                if (slMua > tonKho) {
                    JOptionPane.showMessageDialog(this, "Kho không đủ hàng! Chỉ còn: " + tonKho);
                    return;
                }

                // Thêm vào list và bảng
                themVaoGio(maSP, tenSP, slMua, giaBan);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
            }
        });

        // 2. Xóa khỏi giỏ
        btnXoa.addActionListener(e -> {
            int row = tblGioHang.getSelectedRow();
            if (row != -1) {
                gioHang.remove(row);
                modelGioHang.removeRow(row);
                capNhatTongTien();
            }
        });

        // 3. Thanh toán
        btnThanhToan.addActionListener(e -> xuLyThanhToan());
    }

    // Load dữ liệu từ Database vào bảng bên trái
    private void loadDataSanPham() {
        modelSanPham.setRowCount(0);
        SanPhamDAO dao = new SanPhamDAO();
        // Giả sử hàm getAll() của bạn trả về List<SanPham>
        List<SanPham> list = dao.getAll(); 
        for (SanPham sp : list) {
            modelSanPham.addRow(new Object[]{
                sp.getMaSP(), sp.getTenSP(), sp.getGiaBan(), sp.getSoLuong()
            });
        }
    }

    private void themVaoGio(int ma, String ten, int sl, double gia) {
        // Kiểm tra xem đã có trong giỏ chưa để cộng dồn
        for (CartItem item : gioHang) {
            if (item.getMaSP() == ma) {
                item.setSoLuong(item.getSoLuong() + sl);
                refreshGioHang();
                return;
            }
        }
        // Nếu chưa có thì thêm mới
        gioHang.add(new CartItem(ma, ten, sl, gia));
        refreshGioHang();
    }

    private void refreshGioHang() {
        modelGioHang.setRowCount(0);
        for (CartItem item : gioHang) {
            modelGioHang.addRow(new Object[]{
                item.getMaSP(), item.getTenSP(), item.getSoLuong(), item.getDonGia(), item.getThanhTien()
            });
        }
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        double tong = 0;
        for (CartItem item : gioHang) tong += item.getThanhTien();
        DecimalFormat df = new DecimalFormat("#,###");
        lblTongTien.setText("Tổng tiền: " + df.format(tong) + " VNĐ");
    }

//    private void xuLyThanhToan() {
//        if (gioHang.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán?", "Thanh toán", JOptionPane.YES_NO_OPTION);
//        if (confirm == JOptionPane.YES_OPTION) {
//            double tongTien = 0;
//            for (CartItem item : gioHang) tongTien += item.getThanhTien();
//
//            HoaDonDAO dao = new HoaDonDAO();
//            boolean ketQua = dao.thanhToan(taiKhoanHienTai.getUsername(), tongTien, gioHang);
//
//            if (ketQua) {
//                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
//                gioHang.clear();
//                refreshGioHang();
//                loadDataSanPham(); // Load lại để cập nhật số lượng tồn kho mới
//            } else {
//                JOptionPane.showMessageDialog(this, "Lỗi thanh toán! Vui lòng thử lại.");
//            }
//        }
//    }
    private void xuLyThanhToan() {
        if (gioHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán?", "Thanh toán", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            double tongTien = 0;
            for (CartItem item : gioHang) tongTien += item.getThanhTien();

            HoaDonDAO dao = new HoaDonDAO();
            
            // --- SỬA ĐỔI Ở ĐÂY: Hàm thanhToan cần trả về int (Mã HĐ) thay vì boolean ---
            // Bạn cần vào HoaDonDAO sửa hàm thanhToan return maHD vừa insert (nếu lỗi return -1)
            // Nếu không sửa được DAO, ta tạm thời lấy mã hóa đơn mới nhất
            
            boolean ketQua = dao.thanhToan(taiKhoanHienTai.getUsername(), tongTien, gioHang);

            if (ketQua) {
                // --- BẮT ĐẦU ĐOẠN CODE IN PDF ---
                try {
                    // Vì DAO hiện tại trả về boolean nên ta chưa lấy được mã HĐ thật từ DB.
                    // Tạm thời ta dùng mã giả lập theo thời gian hoặc số ngẫu nhiên để in PDF
                    int maHD_Tam = (int) (System.currentTimeMillis() / 100000); 
                    
                    util.PDFPrinter printer = new util.PDFPrinter();
                    printer.xuatHoaDon(maHD_Tam, taiKhoanHienTai.getHoTen(), tongTien, gioHang);
                    
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công & Đã in hóa đơn!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi in hóa đơn: " + ex.getMessage());
                    ex.printStackTrace();
                }
                // --- KẾT THÚC ĐOẠN CODE IN PDF ---

                // Reset lại giỏ hàng để bán đơn tiếp theo
                gioHang.clear();
                refreshGioHang();
                loadDataSanPham(); 
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thanh toán! Vui lòng thử lại.");
            }
        }
    }
}