package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import dao.SanPhamDAO;
import model.SanPham;

public class QuanLySanPhamForm extends JFrame {
    
    private JTable table;
    private DefaultTableModel tableModel;
    private SanPhamDAO sanPhamDAO = new SanPhamDAO(); // Gọi DAO

    public QuanLySanPhamForm() {
        initUI(); // Khởi tạo giao diện
        loadData(); // Tải dữ liệu lên bảng
    }

    private void initUI() {
        setTitle("Quản Lý Cửa Hàng Kẹo Ngọt");
        setSize(800, 500);
        setLocationRelativeTo(null); // Căn giữa màn hình
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- PHẦN 1: TIÊU ĐỀ ---
        JLabel lblTitle = new JLabel("DANH SÁCH SẢN PHẨM", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.BLUE);
        add(lblTitle, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG DỮ LIỆU (JTable) ---
        // Định nghĩa cột
        String[] columnNames = {"Mã SP", "Tên Kẹo", "Đơn Giá", "Số Lượng"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        
        // JScrollPane giúp bảng có thanh cuộn nếu dữ liệu dài
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- PHẦN 3: NÚT BẤM (Ví dụ) ---
        JPanel panelBot = new JPanel();
        JButton btnReload = new JButton("Tải lại danh sách");
        panelBot.add(btnReload);
        add(panelBot, BorderLayout.SOUTH);
        
        // Sự kiện nút Reload
        btnReload.addActionListener(e -> loadData());
    }

    // Hàm lấy dữ liệu từ DAO đổ vào Table
    private void loadData() {
        // Xóa dữ liệu cũ trên bảng
        tableModel.setRowCount(0);
        
        // Lấy danh sách mới từ Database
        ArrayList<SanPham> list = sanPhamDAO.getAll();
        
        // Duyệt list và thêm từng dòng vào bảng
        for (SanPham sp : list) {
            Object[] row = {sp.getMaSP(), sp.getTenSP(), sp.getGia(), sp.getSoLuong()};
            tableModel.addRow(row);
        }
    }

    // Hàm Main để chạy thử form này
    public static void main(String[] args) {
        // Khởi chạy trong luồng sự kiện của Swing
        SwingUtilities.invokeLater(() -> {
            new QuanLySanPhamForm().setVisible(true);
        });
    }
}