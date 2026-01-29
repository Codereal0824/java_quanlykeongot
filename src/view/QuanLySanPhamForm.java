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
    private SanPhamDAO sanPhamDAO = new SanPhamDAO(); 
    
    // Khai báo các ô nhập liệu để dùng chung
    private JTextField txtTen, txtGia, txtSoLuong;

    public QuanLySanPhamForm() {
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("Quản Lý Cửa Hàng Kẹo Ngọt");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- PHẦN 1: FORM NHẬP LIỆU (Nằm ở trên cùng) ---
        JPanel panelInput = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 dòng, 2 cột
        panelInput.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm")); // Tạo khung viền

        panelInput.add(new JLabel("Tên Kẹo:"));
        txtTen = new JTextField();
        panelInput.add(txtTen);

        panelInput.add(new JLabel("Đơn Giá:"));
        txtGia = new JTextField();
        panelInput.add(txtGia);

        panelInput.add(new JLabel("Số Lượng:"));
        txtSoLuong = new JTextField();
        panelInput.add(txtSoLuong);

        // Đặt panel nhập liệu vào vùng Bắc (North)
        add(panelInput, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG DỮ LIỆU (Nằm ở giữa) ---
        String[] columnNames = {"Mã SP", "Tên Kẹo", "Đơn Giá", "Số Lượng"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
     // --- SỰ KIỆN CLICK CHUỘT VÀO BẢNG ---
        // Khi click vào dòng nào, dữ liệu sẽ đổ ngược lên ô nhập
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow(); // Lấy chỉ số dòng đang chọn
                
                // Lấy dữ liệu từ bảng (cột 1 là tên, cột 2 là giá, cột 3 là số lượng)
                // Lưu ý: Cột 0 là MaSP mình không hiển thị lên ô nhập nhưng sẽ dùng để xóa
                String ten = tableModel.getValueAt(row, 1).toString();
                String gia = tableModel.getValueAt(row, 2).toString();
                String sl = tableModel.getValueAt(row, 3).toString();

                // Đổ dữ liệu vào TextField
                txtTen.setText(ten);
                txtGia.setText(gia);
                txtSoLuong.setText(sl);
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PHẦN 3: CÁC NÚT CHỨC NĂNG (Nằm ở dưới cùng) ---
        JPanel panelButton = new JPanel();
        JButton btnSua = new JButton("Cập Nhật"); // Tạo nút mới
        panelButton.add(btnSua); // Thêm vào panel cùng hàng với nút Thêm, Xóa
        JButton btnThem = new JButton("Thêm Mới");
        JButton btnXoa = new JButton("Xóa"); // Nút Xóa để dành làm sau
        
        panelButton.add(btnThem);
        panelButton.add(btnXoa);
        add(panelButton, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN NÚT THÊM ---
        btnThem.addActionListener(e -> {
            try {
                // 1. Lấy dữ liệu từ ô nhập
                String ten = txtTen.getText();
                // Ép kiểu chuỗi sang số (Dễ lỗi nếu nhập chữ, nên cần try-catch)
                double gia = Double.parseDouble(txtGia.getText());
                int sl = Integer.parseInt(txtSoLuong.getText());

                // 2. Tạo đối tượng Model (Mã để 0 vì SQL tự tăng)
                SanPham sp = new SanPham(0, ten, gia, sl);

                // 3. Gọi DAO để lưu
                if (sanPhamDAO.addSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData(); // Load lại bảng để thấy dòng mới
                    xoaTrangForm(); // Xóa chữ trong ô nhập
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: Giá và Số lượng phải là số!");
            }
        });
     // --- XỬ LÝ SỰ KIỆN NÚT XÓA ---
        btnXoa.addActionListener(e -> {
            // 1. Kiểm tra xem người dùng đã chọn dòng nào chưa
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!");
                return; // Dừng lại, không làm gì tiếp
            }

            // 2. Hỏi xác nhận (Tránh lỡ tay xóa nhầm)
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa không?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // 3. Lấy Mã Sản Phẩm (MaSP) từ cột đầu tiên (index 0) của dòng đang chọn
                int maSP = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

                // 4. Gọi DAO để xóa
                if (sanPhamDAO.deleteSanPham(maSP)) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadData(); // Load lại bảng
                    xoaTrangForm(); // Xóa trắng ô nhập
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa thất bại!");
                }
            }
        });
     // --- XỬ LÝ SỰ KIỆN NÚT SỬA ---
        btnSua.addActionListener(e -> {
            // 1. Kiểm tra xem có chọn dòng nào không
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!");
                return;
            }

            try {
                // 2. Lấy MaSP từ dòng đang chọn (Cột 0 - ẩn hoặc hiện đều lấy được)
                // Bắt buộc phải có MaSP để SQL biết sửa dòng nào
                int maSP = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

                // 3. Lấy dữ liệu mới người dùng vừa chỉnh trong ô nhập
                String tenMoi = txtTen.getText();
                double giaMoi = Double.parseDouble(txtGia.getText());
                int slMoi = Integer.parseInt(txtSoLuong.getText());

                // 4. Tạo đối tượng SanPham với thông tin mới
                SanPham sp = new SanPham(maSP, tenMoi, giaMoi, slMoi);

                // 5. Gọi DAO để update
                if (sanPhamDAO.updateSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData(); // Load lại bảng để thấy dữ liệu mới
                    xoaTrangForm(); // Xóa trắng ô nhập
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: Giá và Số lượng phải là số!");
            }
        });
    }

    private void loadData() {
        tableModel.setRowCount(0);
        ArrayList<SanPham> list = sanPhamDAO.getAll();
        for (SanPham sp : list) {
            tableModel.addRow(new Object[]{sp.getMaSP(), sp.getTenSP(), sp.getGia(), sp.getSoLuong()});
        }
    }
    
    // Hàm xóa trắng các ô nhập sau khi thêm xong
    private void xoaTrangForm() {
        txtTen.setText("");
        txtGia.setText("");
        txtSoLuong.setText("");
        txtTen.requestFocus(); // Đưa con trỏ chuột về ô Tên
    }

    public static void main(String[] args) {
        new QuanLySanPhamForm().setVisible(true);
    }
    
    
}