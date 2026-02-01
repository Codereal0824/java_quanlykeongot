package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import dao.ThongKeDAO;

public class ThongKeForm extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTieuDe;
    private ThongKeDAO tkDAO = new ThongKeDAO();

    public ThongKeForm() {
        setTitle("Hệ Thống Báo Cáo Thống Kê");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- PHẦN 1: 2 NÚT BẤM LỚN (NORTH) ---
        JPanel pnlHeader = new JPanel(new GridLayout(1, 2, 10, 10));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnDT = new JButton("Thống kê doanh thu");
        JButton btnSP = new JButton("Thống kê sản phẩm bán chạy");
        
        // Làm đẹp nút
        btnDT.setFont(new Font("Arial", Font.BOLD, 14));
        btnSP.setFont(new Font("Arial", Font.BOLD, 14));

        pnlHeader.add(btnDT);
        pnlHeader.add(btnSP);

        // --- PHẦN 2: HIỂN THỊ KẾT QUẢ (CENTER) ---
        JPanel pnlMain = new JPanel(new BorderLayout());
        lblTieuDe = new JLabel("Vui lòng chọn kiểu thống kê", JLabel.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.ITALIC, 16));
        lblTieuDe.setForeground(Color.BLUE);

        String[] columns = {"Tên Sản Phẩm", "Số Lượng Đã Bán", "Tổng Thu (VNĐ)"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        pnlMain.add(lblTieuDe, BorderLayout.NORTH);
        pnlMain.add(new JScrollPane(table), BorderLayout.CENTER);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlMain, BorderLayout.CENTER);

        // --- XỬ LÝ SỰ KIỆN ---
        btnDT.addActionListener(e -> showMenuThongKe(btnDT, true));
        btnSP.addActionListener(e -> showMenuThongKe(btnSP, false));
    }

    private void showMenuThongKe(JButton btn, boolean isDoanhThu) {
        JPopupMenu menu = new JPopupMenu();
        String[] options = {"Ngày", "Tuần", "Tháng", "Quý 1 (T1-3)", "Quý 2 (T4-6)", "Quý 3 (T7-9)", "Quý 4 (T10-12)"};

        for (String opt : options) {
            JMenuItem item = new JMenuItem(opt);
            item.addActionListener(e -> thucHienThongKe(opt, isDoanhThu));
            menu.add(item);
        }
        menu.show(btn, 0, btn.getHeight());
    }

    private void thucHienThongKe(String mốc, boolean isDoanhThu) {
        model.setRowCount(0);
        double tongDT = 0;
        ArrayList<Object[]> data = new ArrayList<>();

        // Logic phân tách thời gian
        int start = 0, end = 0;
        if (mốc.contains("Quý 1")) { start = 1; end = 3; }
        else if (mốc.contains("Quý 2")) { start = 4; end = 6; }
        else if (mốc.contains("Quý 3")) { start = 7; end = 9; }
        else if (mốc.contains("Quý 4")) { start = 10; end = 12; }

        if (start != 0) { // Thống kê theo Quý
            tongDT = tkDAO.getDoanhThuTheoKhoangThang(start, end);
            data = tkDAO.getSPTheoKhoangThang(start, end);
        } else { // Ngày, Tuần, Tháng
            tongDT = tkDAO.getTongDoanhThu(mốc);
            data = tkDAO.getSanPhamBanChay(mốc);
        }

        // Hiển thị kết quả
        if (isDoanhThu) {
            lblTieuDe.setText("Doanh thu " + mốc + ": " + String.format("%,.0f VNĐ", tongDT));
        } else {
            lblTieuDe.setText("Top sản phẩm bán chạy trong " + mốc);
        }

        for (Object[] row : data) model.addRow(row);
    }

    public static void main(String[] args) {
        new ThongKeForm().setVisible(true);
    }
}