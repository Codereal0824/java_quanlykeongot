package view;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import dao.SanPhamDAO;
import dao.LoaiDAO;
import model.SanPham;
import model.LoaiSanPham;	
import util.DataChangeListener;

// CHỈNH SỬA: Đổi từ JFrame sang JPanel để nhúng được vào MainForm
public class QuanLySanPhamPanel extends JPanel implements DataChangeListener{

    private JTable table;
    private DefaultTableModel tableModel;
    private SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private LoaiDAO loaiDAO = new LoaiDAO();

    private JTextField txtTen, txtSoLuong, txtGiaBan, txtGiaNhap, txtDonVi, txtHinhAnh;
    private JComboBox<LoaiSanPham> cbLoai;
    private JLabel lblAnhPreview;
    private JButton btnChonAnh;

    public QuanLySanPhamPanel() {
        initUI();
        loadData();
    }

    private void initUI() {
        // CHỈNH SỬA: JPanel sử dụng BorderLayout trực tiếp
        setLayout(new BorderLayout());

        // --- PHẦN 1: FORM NHẬP LIỆU ---
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBorder(BorderFactory.createTitledBorder("Thông tin sản phẩm"));
        panelTop.setPreferredSize(new Dimension(800, 300)); // Thu nhỏ chiều rộng cho khớp Sidebar

        JPanel panelLeft = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Tên
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panelLeft.add(new JLabel("Tên Bánh/Kẹo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtTen = new JTextField();
        panelLeft.add(txtTen, gbc);

        // Dòng 2: Số lượng & Đơn vị
        JPanel row2 = new JPanel(new GridLayout(1, 4, 10, 0)); 
        row2.add(new JLabel("Số Lượng:"));
        txtSoLuong = new JTextField();
        row2.add(txtSoLuong);
        row2.add(new JLabel("Đơn vị:"));
        txtDonVi = new JTextField();
        row2.add(txtDonVi);
        
        gbc.gridx = 0; gbc.gridy = 1; 
        panelLeft.add(new JLabel("Kho / Đơn vị:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panelLeft.add(row2, gbc);

        // Dòng 3: Giá Bán
        gbc.gridx = 0; gbc.gridy = 2;
        panelLeft.add(new JLabel("Giá Bán:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        txtGiaBan = new JTextField();
        panelLeft.add(txtGiaBan, gbc);

        // Dòng 4: Giá Nhập
        gbc.gridx = 0; gbc.gridy = 3;
        panelLeft.add(new JLabel("Giá Nhập:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        txtGiaNhap = new JTextField();
        panelLeft.add(txtGiaNhap, gbc);

        // Dòng 5: Loại Sản Phẩm
        gbc.gridx = 0; gbc.gridy = 4;
        panelLeft.add(new JLabel("Loại Sản Phẩm:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        cbLoai = new JComboBox<>();
        loadComboBoxLoai();
        panelLeft.add(cbLoai, gbc);

        // Dòng 6: Hình ảnh
        gbc.gridx = 0; gbc.gridy = 5;
        panelLeft.add(new JLabel("File Hình:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        txtHinhAnh = new JTextField();
        txtHinhAnh.setEditable(false);
        txtHinhAnh.setBackground(Color.WHITE);
        panelLeft.add(txtHinhAnh, gbc);

        // Panel bên PHẢI: Preview ảnh
        JPanel panelRight = new JPanel(new BorderLayout());
        panelRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblAnhPreview = new JLabel("Chưa có ảnh");
        lblAnhPreview.setPreferredSize(new Dimension(180, 180));
        lblAnhPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblAnhPreview.setHorizontalAlignment(JLabel.CENTER);
        btnChonAnh = new JButton("Chọn Ảnh");
        JPanel panelBtnAnh = new JPanel();
        panelBtnAnh.add(btnChonAnh);
        panelRight.add(lblAnhPreview, BorderLayout.CENTER);
        panelRight.add(panelBtnAnh, BorderLayout.SOUTH);

        panelTop.add(panelLeft, BorderLayout.CENTER);
        panelTop.add(panelRight, BorderLayout.EAST);
        add(panelTop, BorderLayout.NORTH);

        // --- PHẦN 2: BẢNG DỮ LIỆU ---
        String[] columnNames = {"Mã SP", "Tên Sản Phẩm", "Số Lượng", "Giá Bán", "Giá Nhập", "Đơn Vị", "Hình Ảnh", "Mã Loại"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        
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
                hienThiAnh(tenFileAnh);

                int maLoai = Integer.parseInt(tableModel.getValueAt(row, 7).toString());
                for (int i = 0; i < cbLoai.getItemCount(); i++) {
                    if (cbLoai.getItemAt(i).getMaLoai() == maLoai) {
                        cbLoai.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- PHẦN 3: CÁC NÚT CHỨC NĂNG ---
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnThem = new JButton("Thêm Mới");
        JButton btnSua = new JButton("Cập Nhật");
        JButton btnXoa = new JButton("Xóa Sản Phẩm");
        JButton btnLamMoi = new JButton("Làm Mới");
        
        panelButton.add(btnThem);
        panelButton.add(btnSua);
        panelButton.add(btnXoa);
        panelButton.add(btnLamMoi);
        add(panelButton, BorderLayout.SOUTH);

        // --- XỬ LÝ SỰ KIỆN ---
        btnChonAnh.addActionListener(e -> chonAnh());
        btnLamMoi.addActionListener(e -> xoaTrangForm());

        btnThem.addActionListener(e -> {
            try {
                LoaiSanPham selectedLoai = (LoaiSanPham) cbLoai.getSelectedItem();
                SanPham sp = new SanPham(0, txtTen.getText(), Integer.parseInt(txtSoLuong.getText()), 
                        Double.parseDouble(txtGiaBan.getText()), Double.parseDouble(txtGiaNhap.getText()), 
                        txtDonVi.getText(), txtHinhAnh.getText(), selectedLoai.getMaLoai());

                if (sanPhamDAO.addSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData(); xoaTrangForm();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnSua.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn sản phẩm cần sửa!"); return; }
            try {
                int maSP = (int) tableModel.getValueAt(row, 0);
                LoaiSanPham selectedLoai = (LoaiSanPham) cbLoai.getSelectedItem();
                SanPham sp = new SanPham(maSP, txtTen.getText(), Integer.parseInt(txtSoLuong.getText()), 
                        Double.parseDouble(txtGiaBan.getText()), Double.parseDouble(txtGiaNhap.getText()), 
                        txtDonVi.getText(), txtHinhAnh.getText(), selectedLoai.getMaLoai());

                if (sanPhamDAO.updateSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData();
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnXoa.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int maSP = (int) tableModel.getValueAt(row, 0);
                    if (sanPhamDAO.deleteSanPham(maSP)) { loadData(); xoaTrangForm(); }
                }
            }
        });
    }

    private void loadComboBoxLoai() {
        cbLoai.removeAllItems();
        ArrayList<LoaiSanPham> list = loaiDAO.getAll();
        for (LoaiSanPham loai : list) {
            cbLoai.addItem(loai);
        }
    }

    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "png"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File src = fileChooser.getSelectedFile();
            File destDir = new File("images");
            if (!destDir.exists()) destDir.mkdir();
            File dest = new File(destDir, src.getName());
            try {
                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                txtHinhAnh.setText(src.getName());
                hienThiAnh(src.getName());
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }

    private void hienThiAnh(String tenFile) {
        if (tenFile == null || tenFile.isEmpty()) {
            lblAnhPreview.setIcon(null); lblAnhPreview.setText("Chưa có ảnh"); return;
        }
        File f = new File("images/" + tenFile);
        if (f.exists()) {
            ImageIcon icon = new ImageIcon(f.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            lblAnhPreview.setIcon(new ImageIcon(img)); lblAnhPreview.setText("");
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        ArrayList<SanPham> list = sanPhamDAO.getAll();
        for (SanPham sp : list) {
            tableModel.addRow(new Object[]{
                sp.getMaSP(), sp.getTenSP(), sp.getSoLuong(), sp.getGiaBan(), 
                sp.getGiaNhap(), sp.getDonVi(), sp.getHinhAnh(), sp.getMaLoai()
            });
        }
    }

    private void xoaTrangForm() {
        txtTen.setText(""); txtSoLuong.setText(""); txtGiaBan.setText("");
        txtGiaNhap.setText(""); txtDonVi.setText(""); txtHinhAnh.setText("");
        if (cbLoai.getItemCount() > 0) cbLoai.setSelectedIndex(0);
        lblAnhPreview.setIcon(null); lblAnhPreview.setText("Chưa có ảnh");
        txtTen.requestFocus();
    }
    
    @Override
    public void onDataChange() {
        loadData(); // Tự động load lại bảng khi có người bán hàng thành công
    }
}