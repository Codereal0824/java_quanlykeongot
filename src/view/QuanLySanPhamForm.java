package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import dao.SanPhamDAO;
import model.SanPham;

public class QuanLySanPhamForm extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private SanPhamDAO sanPhamDAO = new SanPhamDAO();

    // Các ô nhập liệu
    private JTextField txtTen, txtSoLuong, txtGiaBan, txtGiaNhap, txtDonVi, txtHinhAnh, txtMaLoai;
    
    // THÊM MỚI: Label để hiện ảnh và Nút chọn ảnh
    private JLabel lblAnhPreview;
    private JButton btnChonAnh;

    public QuanLySanPhamForm() {
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("Hệ Thống Quản Lý Cửa Hàng Bánh Kẹo - DNC");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

     // --- PHẦN 1: FORM NHẬP LIỆU (Đã sửa lại Layout cho đẹp) ---
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        panelTop.setPreferredSize(new Dimension(1100, 300)); // Tăng chiều cao một chút cho thoáng

        // A. Panel bên TRÁI: Dùng GridBagLayout để các ô nằm sát nhau đẹp mắt
        JPanel panelLeft = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Khoảng cách giữa các ô (Trên, Trái, Dưới, Phải)
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ô nhập tự dãn ngang

        // --- Dòng 1: Tên Bánh ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; // Cột nhãn không dãn
        panelLeft.add(new JLabel("Tên Bánh/Kẹo:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; // Cột nhập liệu dãn hết cỡ
        txtTen = new JTextField();
        panelLeft.add(txtTen, gbc);

        // --- Dòng 2: Số lượng & Đơn vị (Gộp chung 1 dòng cho gọn) ---
        // Chúng ta sẽ tạo 1 panel con cho dòng này
        JPanel row2 = new JPanel(new GridLayout(1, 4, 10, 0)); 
        row2.add(new JLabel("Số Lượng:"));
        txtSoLuong = new JTextField();
        row2.add(txtSoLuong);
        row2.add(new JLabel("Đơn Vị Tính:"));
        txtDonVi = new JTextField();
        row2.add(txtDonVi);
        
        gbc.gridx = 0; gbc.gridy = 1; 
        panelLeft.add(new JLabel("Kho / Đơn vị:"), gbc); // Nhãn chung
        gbc.gridx = 1; gbc.gridy = 1;
        panelLeft.add(row2, gbc);

        // --- Dòng 3: Giá Bán ---
        gbc.gridx = 0; gbc.gridy = 2;
        panelLeft.add(new JLabel("Giá Bán:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        txtGiaBan = new JTextField();
        panelLeft.add(txtGiaBan, gbc);

        // --- Dòng 4: Giá Nhập ---
        gbc.gridx = 0; gbc.gridy = 3;
        panelLeft.add(new JLabel("Giá Nhập:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        txtGiaNhap = new JTextField();
        panelLeft.add(txtGiaNhap, gbc);

        // --- Dòng 5: Mã Loại ---
        gbc.gridx = 0; gbc.gridy = 4;
        panelLeft.add(new JLabel("Mã Loại (1-5):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        txtMaLoai = new JTextField();
        panelLeft.add(txtMaLoai, gbc);

        // --- Dòng 6: Tên file Hình ---
        gbc.gridx = 0; gbc.gridy = 5;
        panelLeft.add(new JLabel("File Hình:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        txtHinhAnh = new JTextField();
        txtHinhAnh.setEditable(false); // Không cho nhập tay
        txtHinhAnh.setBackground(Color.WHITE); // Để nền trắng cho dễ nhìn
        panelLeft.add(txtHinhAnh, gbc);


        // B. Panel bên PHẢI: Chứa Ảnh (Giữ nguyên logic cũ nhưng chỉnh lại margin)
        JPanel panelRight = new JPanel(new BorderLayout());
        panelRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 30)); // Cách lề phải 30px
        
        lblAnhPreview = new JLabel("Chưa có ảnh");
        lblAnhPreview.setPreferredSize(new Dimension(200, 200)); // Ảnh to hơn chút
        lblAnhPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblAnhPreview.setHorizontalAlignment(JLabel.CENTER);
        
        btnChonAnh = new JButton("Chọn Ảnh");
        btnChonAnh.setIcon(UIManager.getIcon("FileView.directoryIcon"));
        JPanel panelBtnAnh = new JPanel(); // Panel phụ để nút không bị dãn
        panelBtnAnh.add(btnChonAnh);

        panelRight.add(lblAnhPreview, BorderLayout.CENTER);
        panelRight.add(panelBtnAnh, BorderLayout.SOUTH);

        // Đưa 2 bên vào Panel chính
        panelTop.add(panelLeft, BorderLayout.CENTER);
        panelTop.add(panelRight, BorderLayout.EAST);

        add(panelTop, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG DỮ LIỆU ---
        String[] columnNames = {"Mã SP", "Tên Sản Phẩm", "Số Lượng", "Giá Bán", "Giá Nhập", "Đơn Vị", "Hình Ảnh", "Mã Loại"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        
        // SỰ KIỆN: Click vào bảng -> Hiện ảnh lên khung preview
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.getSelectedRow();
                txtTen.setText(tableModel.getValueAt(row, 1).toString());
                txtSoLuong.setText(tableModel.getValueAt(row, 2).toString());
                txtGiaBan.setText(tableModel.getValueAt(row, 3).toString());
                txtGiaNhap.setText(tableModel.getValueAt(row, 4).toString());
                txtDonVi.setText(tableModel.getValueAt(row, 5).toString());
                
                Object hinh = tableModel.getValueAt(row, 6);
                String tenFileAnh = (hinh != null) ? hinh.toString() : "";
                txtHinhAnh.setText(tenFileAnh);
                txtMaLoai.setText(tableModel.getValueAt(row, 7).toString());
                
                // Gọi hàm hiển thị ảnh
                hienThiAnh(tenFileAnh);
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PHẦN 3: CÁC NÚT CHỨC NĂNG ---
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnThem = new JButton("Thêm Mới");
        JButton btnSua = new JButton("Cập Nhật");
        JButton btnXoa = new JButton("Xóa Sản Phẩm");
        
        // Làm đẹp nút
        Dimension btnSize = new Dimension(120, 40);
        btnThem.setPreferredSize(btnSize);
        btnSua.setPreferredSize(btnSize);
        btnXoa.setPreferredSize(btnSize);

        panelButton.add(btnThem);
        panelButton.add(btnSua);
        panelButton.add(btnXoa);
     // --- ĐOẠN CODE THÊM MỚI BẮT ĐẦU TỪ ĐÂY ---
        JButton btnLamMoi = new JButton("Làm Mới");
        btnLamMoi.setPreferredSize(btnSize); // Dùng chung kích thước với các nút kia
        panelButton.add(btnLamMoi);

        // Sự kiện cho nút Làm Mới
        btnLamMoi.addActionListener(e -> xoaTrangForm());
        // --- KẾT THÚC ĐOẠN CODE THÊM MỚI ---
        add(panelButton, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN CÁC NÚT ---

        // 1. Nút Chọn Ảnh (QUAN TRỌNG)
        btnChonAnh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                // Chỉ cho chọn file ảnh
                fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh (JPG, PNG)", "jpg", "png"));
                
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    
                    // Copy file ảnh vào thư mục dự án (images) để dễ quản lý
                    File destFolder = new File("images");
                    if (!destFolder.exists()) destFolder.mkdir(); // Tạo thư mục nếu chưa có
                    
                    File destFile = new File(destFolder, selectedFile.getName());
                    try {
                        Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        
                        // Cập nhật tên ảnh vào ô text và hiển thị lên khung
                        txtHinhAnh.setText(selectedFile.getName());
                        hienThiAnh(selectedFile.getName());
                        
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Lỗi copy ảnh: " + ex.getMessage());
                    }
                }
            }
        });

        // 2. Nút Thêm
        btnThem.addActionListener(e -> {
            try {
                SanPham sp = new SanPham(0, txtTen.getText(), 
                        Integer.parseInt(txtSoLuong.getText()), 
                        Double.parseDouble(txtGiaBan.getText()), 
                        Double.parseDouble(txtGiaNhap.getText()), 
                        txtDonVi.getText(), 
                        txtHinhAnh.getText(), // Lấy tên ảnh từ ô text
                        Integer.parseInt(txtMaLoai.getText()));

                if (sanPhamDAO.addSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData();
                    xoaTrangForm();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage());
            }
        });

        // 3. Nút Sửa
        btnSua.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần sửa!");
                return;
            }
            try {
                int maSP = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                SanPham sp = new SanPham(maSP, txtTen.getText(), 
                        Integer.parseInt(txtSoLuong.getText()), 
                        Double.parseDouble(txtGiaBan.getText()), 
                        Double.parseDouble(txtGiaNhap.getText()), 
                        txtDonVi.getText(), 
                        txtHinhAnh.getText(), 
                        Integer.parseInt(txtMaLoai.getText()));

                if (sanPhamDAO.updateSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData();
                    xoaTrangForm();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage());
            }
        });

        // 4. Nút Xóa
        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int maSP = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    if (sanPhamDAO.deleteSanPham(maSP)) {
                        JOptionPane.showMessageDialog(this, "Đã xóa!");
                        loadData();
                        xoaTrangForm();
                    }
                }
            }
        });
    }

    // --- HÀM HỖ TRỢ HIỂN THỊ ẢNH ---
    private void hienThiAnh(String tenFile) {
        if (tenFile == null || tenFile.isEmpty()) {
            lblAnhPreview.setIcon(null);
            lblAnhPreview.setText("Không có ảnh");
            return;
        }

        // Đường dẫn đến file ảnh trong thư mục images của dự án
        String path = "images/" + tenFile;
        ImageIcon icon = new ImageIcon(path);
        
        // Kiểm tra xem file có tồn tại và load được không
        if (icon.getImageLoadStatus() == MediaTracker.ERRORED) {
            lblAnhPreview.setIcon(null);
            lblAnhPreview.setText("Lỗi file ảnh");
        } else {
            // Resize ảnh cho vừa khung 180x180
            Image img = icon.getImage();
            Image newImg = img.getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            lblAnhPreview.setIcon(new ImageIcon(newImg));
            lblAnhPreview.setText(""); // Xóa chữ
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        ArrayList<SanPham> list = sanPhamDAO.getAll();
        for (SanPham sp : list) {
            tableModel.addRow(new Object[]{
                sp.getMaSP(), sp.getTenSP(), sp.getSoLuong(), 
                sp.getGiaBan(), sp.getGiaNhap(), sp.getDonVi(), 
                sp.getHinhAnh(), sp.getMaLoai()
            });
        }
    }

    private void xoaTrangForm() {
        txtTen.setText("");
        txtSoLuong.setText("");
        txtGiaBan.setText("");
        txtGiaNhap.setText("");
        txtDonVi.setText("");
        txtHinhAnh.setText("");
        txtMaLoai.setText("");
        lblAnhPreview.setIcon(null);
        lblAnhPreview.setText("Chưa có ảnh");
        txtTen.requestFocus();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        new QuanLySanPhamForm().setVisible(true);
    }
}