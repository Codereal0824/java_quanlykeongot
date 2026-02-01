package view;

import dao.SanPhamDAO;
import dao.HoaDonDAO;
import model.SanPham;
import model.CartItem;
import model.TaiKhoan;
import util.DataChangeListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BanHangPanel extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private JTable tblSanPham;
    private JTable tblGioHang;
    private DefaultTableModel modelSanPham;
    private DefaultTableModel modelGioHang;
    private JLabel lblTongTien;
    private List<CartItem> gioHang;
    private DataChangeListener listener;
    
    private JTextField txtTimKiem;
    private TableRowSorter<DefaultTableModel> sorter;

    // Các nút chức năng cần quản lý trạng thái
    private JButton btnThanhToan;
    private JButton btnInHoaDon;
    private JButton btnLamMoi;
    
    // Biến lưu mã hóa đơn vừa thanh toán xong (để in)
    private int maHDVuaThanhToan = -1;

    public BanHangPanel(TaiKhoan tk, DataChangeListener L) {
        this.taiKhoanHienTai = tk;
        this.listener = L;
        this.gioHang = new ArrayList<>();
        initUI();
        loadDataSanPham();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // --- PHẦN TRÁI: DANH SÁCH SẢN PHẨM ---
        JPanel pnlTrai = new JPanel(new BorderLayout());
        pnlTrai.setBorder(BorderFactory.createTitledBorder("Danh sách kẹo"));
        
        // Thanh tìm kiếm
        JPanel pnlTimKiem = new JPanel(new BorderLayout());
        pnlTimKiem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlTimKiem.add(new JLabel("Tìm nhanh: "), BorderLayout.WEST);
        txtTimKiem = new JTextField();
        pnlTimKiem.add(txtTimKiem, BorderLayout.CENTER);
        pnlTrai.add(pnlTimKiem, BorderLayout.NORTH);
        
        // Bảng sản phẩm
        modelSanPham = new DefaultTableModel(new Object[]{"Mã", "Tên SP", "Giá bán", "Tồn kho"}, 0);
        tblSanPham = new JTable(modelSanPham);
        sorter = new TableRowSorter<>(modelSanPham);
        tblSanPham.setRowSorter(sorter);
        pnlTrai.add(new JScrollPane(tblSanPham), BorderLayout.CENTER);
        
        // Sự kiện tìm kiếm
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void changedUpdate(DocumentEvent e) { search(); }
            private void search() {
                String text = txtTimKiem.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        }); 

        JButton btnThem = new JButton("THÊM VÀO GIỎ >>");
        btnThem.setFont(new Font("Arial", Font.BOLD, 13));
        btnThem.setBackground(new Color(52, 152, 219));
        btnThem.setForeground(Color.WHITE);
        pnlTrai.add(btnThem, BorderLayout.SOUTH);

        // --- PHẦN PHẢI: GIỎ HÀNG ---
        JPanel pnlPhai = new JPanel(new BorderLayout());
        pnlPhai.setBorder(BorderFactory.createTitledBorder("Giỏ hàng khách mua"));

        modelGioHang = new DefaultTableModel(new Object[]{"Mã", "Tên SP", "SL", "Đơn giá", "Thành tiền"}, 0);
        tblGioHang = new JTable(modelGioHang);
        pnlPhai.add(new JScrollPane(tblGioHang), BorderLayout.CENTER);

        // --- PANEL THANH TOÁN (SỬA ĐỔI LỚN TẠI ĐÂY) ---
        JPanel pnlSouthRight = new JPanel(new BorderLayout());
        
        // Tổng tiền
        lblTongTien = new JLabel("Tổng tiền: 0 VNĐ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 18));
        lblTongTien.setForeground(Color.RED);
        lblTongTien.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Các nút chức năng: Xóa món, Thanh Toán, In, Làm Mới
        JPanel pnlButtons = new JPanel(new GridLayout(2, 2, 5, 5));
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton btnXoa = new JButton("Xóa món");
        btnThanhToan = new JButton("THANH TOÁN (F5)");
        btnInHoaDon = new JButton("IN HÓA ĐƠN");
        btnLamMoi = new JButton("ĐƠN MỚI (F1)");

        // Màu sắc nút
        btnThanhToan.setBackground(new Color(39, 174, 96)); btnThanhToan.setForeground(Color.WHITE); // Xanh lá
        btnInHoaDon.setBackground(new Color(41, 128, 185)); btnInHoaDon.setForeground(Color.WHITE); // Xanh dương
        btnLamMoi.setBackground(new Color(243, 156, 18));  btnLamMoi.setForeground(Color.WHITE); // Cam
        
        // Font chữ
        Font fontBtn = new Font("Arial", Font.BOLD, 12);
        btnThanhToan.setFont(fontBtn);
        btnInHoaDon.setFont(fontBtn);
        btnLamMoi.setFont(fontBtn);

        // Ban đầu chưa thanh toán thì chưa được in
        btnInHoaDon.setEnabled(false); 

        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);
        pnlButtons.add(btnThanhToan);
        pnlButtons.add(btnInHoaDon);

        pnlSouthRight.add(lblTongTien, BorderLayout.NORTH);
        pnlSouthRight.add(pnlButtons, BorderLayout.CENTER);
        pnlPhai.add(pnlSouthRight, BorderLayout.SOUTH);

        // Chia đôi màn hình
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlTrai, pnlPhai);
        splitPane.setDividerLocation(450);
        add(splitPane, BorderLayout.CENTER);

        // --- SỰ KIỆN ---
        
        btnThem.addActionListener(e -> themSanPhamVaoGio());
        
        btnXoa.addActionListener(e -> {
            int row = tblGioHang.getSelectedRow();
            if (row != -1) {
                gioHang.remove(row);
                refreshGioHang();
                // Nếu xóa sửa giỏ hàng thì phải thanh toán lại mới được in
                btnThanhToan.setEnabled(true);
                btnInHoaDon.setEnabled(false);
            }
        });

        // 1. Nút Thanh Toán: Chỉ lưu DB
        btnThanhToan.addActionListener(e -> xuLyThanhToanOnly());

        // 2. Nút In Hóa Đơn: Chỉ xuất PDF
        btnInHoaDon.addActionListener(e -> xuLyInHoaDonOnly());

        // 3. Nút Làm Mới: Reset để bán khách sau
        btnLamMoi.addActionListener(e -> xuLyLamMoi());
    }

    // --- CÁC HÀM XỬ LÝ LOGIC ---

    private void themSanPhamVaoGio() {
        int row = tblSanPham.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn kẹo cần bán trước!");
            return;
        }
        
        int viewRow = tblSanPham.getSelectedRow();
        int modelRow = tblSanPham.convertRowIndexToModel(viewRow);

        int maSP = (int) modelSanPham.getValueAt(modelRow, 0);
        String tenSP = (String) modelSanPham.getValueAt(modelRow, 1);
        double giaBan = (double) modelSanPham.getValueAt(modelRow, 2);
        int tonKho = (int) modelSanPham.getValueAt(modelRow, 3);

        String slStr = JOptionPane.showInputDialog(this, "Nhập số lượng khách mua (" + tenSP + "):");
        try {
            if (slStr == null) return;
            int slMua = Integer.parseInt(slStr);
            if (slMua <= 0) throw new NumberFormatException();
            if (slMua > tonKho) {
                JOptionPane.showMessageDialog(this, "Kho không đủ! Chỉ còn: " + tonKho);
                return;
            }
            
            // Logic thêm vào list
            boolean found = false;
            for (CartItem item : gioHang) {
                if (item.getMaSP() == maSP) {
                    item.setSoLuong(item.getSoLuong() + slMua);
                    found = true; break;
                }
            }
            if (!found) gioHang.add(new CartItem(maSP, tenSP, slMua, giaBan));
            
            refreshGioHang();
            // Có thay đổi giỏ hàng thì phải thanh toán lại
            btnThanhToan.setEnabled(true);
            btnInHoaDon.setEnabled(false);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
        }
    }

    private void xuLyThanhToanOnly() {
        if (gioHang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ hàng trống!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán?", "Thanh toán", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            double tongTien = 0;
            for (CartItem item : gioHang) tongTien += item.getThanhTien();

            HoaDonDAO dao = new HoaDonDAO();
            boolean ketQua = dao.thanhToan(taiKhoanHienTai.getUsername(), tongTien, gioHang);

            if (ketQua) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công! Có thể in hóa đơn ngay.");
                
                // Cập nhật tồn kho bên Tab Sản Phẩm
                if (listener != null) listener.onDataChange();
                loadDataSanPham(); // Load lại bảng bên trái luôn
                
                // TẠO MÃ HÓA ĐƠN GIẢ ĐỊNH ĐỂ IN (Hoặc lấy từ DB nếu DAO trả về)
                maHDVuaThanhToan = (int) (System.currentTimeMillis() / 100000); 

                // CHUYỂN TRẠNG THÁI NÚT
                btnThanhToan.setEnabled(false); // Khóa nút thanh toán (tránh bấm 2 lần)
                btnInHoaDon.setEnabled(true);   // Mở nút in
                
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi thanh toán! Vui lòng thử lại.");
            }
        }
    }

    private void xuLyInHoaDonOnly() {
        if (maHDVuaThanhToan == -1) {
            JOptionPane.showMessageDialog(this, "Chưa có đơn hàng nào được thanh toán!");
            return;
        }
        
        try {
            double tongTien = 0;
            for (CartItem item : gioHang) tongTien += item.getThanhTien();

            util.PDFPrinter printer = new util.PDFPrinter();
            printer.xuatHoaDon(maHDVuaThanhToan, taiKhoanHienTai.getHoTen(), tongTien, gioHang);
            
            JOptionPane.showMessageDialog(this, "Đã in hóa đơn ra file PDF!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi in hóa đơn: " + ex.getMessage());
        }
    }
    
    private void xuLyLamMoi() {
        // Reset toàn bộ để bán cho khách mới
        gioHang.clear();
        refreshGioHang();
        
        maHDVuaThanhToan = -1;
        btnThanhToan.setEnabled(true);
        btnInHoaDon.setEnabled(false);
        txtTimKiem.setText("");
        txtTimKiem.requestFocus();
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
    
    private void loadDataSanPham() {
        modelSanPham.setRowCount(0);
        SanPhamDAO dao = new SanPhamDAO();
        List<SanPham> list = dao.getAll(); 
        for (SanPham sp : list) {
            modelSanPham.addRow(new Object[]{
                sp.getMaSP(), sp.getTenSP(), sp.getGiaBan(), sp.getSoLuong()
            });
        }
    }
}