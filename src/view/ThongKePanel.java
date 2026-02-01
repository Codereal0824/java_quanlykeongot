package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import dao.ThongKeDAO;

public class ThongKePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTieuDe;
    private ThongKeDAO tkDAO = new ThongKeDAO();
    private JPanel pnlChartContainer; // Panel chứa biểu đồ

    public ThongKePanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- HEADER: CÁC NÚT CHỨC NĂNG ---
        JPanel pnlHeader = new JPanel(new GridLayout(1, 2, 10, 10));
        pnlHeader.setBorder(BorderFactory.createTitledBorder("Công cụ thống kê"));
        pnlHeader.setBackground(Color.WHITE);

        JButton btnDT = new JButton("Thống kê doanh thu");
        JButton btnSP = new JButton("Thống kê sản phẩm bán chạy");
        
        // Style nút bấm
        btnDT.setFont(new Font("Arial", Font.BOLD, 14));
        btnSP.setFont(new Font("Arial", Font.BOLD, 14));
        btnDT.setBackground(new Color(52, 152, 219)); btnDT.setForeground(Color.WHITE);
        btnSP.setBackground(new Color(155, 89, 182)); btnSP.setForeground(Color.WHITE);

        pnlHeader.add(btnDT);
        pnlHeader.add(btnSP);

        // --- BODY: CHIA ĐÔI (TRÁI: BẢNG - PHẢI: BIỂU ĐỒ) ---
        JPanel pnlBody = new JPanel(new GridLayout(1, 2, 15, 0)); // Cách nhau 15px
        pnlBody.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlBody.setBackground(Color.WHITE);

        // 1. Panel Bảng (Bên Trái)
        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBackground(Color.WHITE);
        
        lblTieuDe = new JLabel("Vui lòng chọn chức năng...", JLabel.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.ITALIC, 16));
        lblTieuDe.setForeground(Color.BLUE);
        lblTieuDe.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Cấu hình bảng
        String[] columns = {"Tên Sản Phẩm", "Số Lượng", "Doanh Thu (VNĐ)"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        pnlTable.add(lblTieuDe, BorderLayout.NORTH);
        pnlTable.add(new JScrollPane(table), BorderLayout.CENTER);

        // 2. Panel Biểu đồ (Bên Phải)
        pnlChartContainer = new JPanel(new BorderLayout());
        pnlChartContainer.setBackground(Color.WHITE);
        pnlChartContainer.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Mặc định hiện chữ hướng dẫn
        JLabel lblChartGuide = new JLabel("<html><center>Biểu đồ trực quan<br>sẽ hiển thị tại đây</center></html>", JLabel.CENTER);
        lblChartGuide.setForeground(Color.GRAY);
        pnlChartContainer.add(lblChartGuide, BorderLayout.CENTER);

        // Thêm 2 phần vào Body
        pnlBody.add(pnlTable);
        pnlBody.add(pnlChartContainer);

        add(pnlHeader, BorderLayout.NORTH);
        add(pnlBody, BorderLayout.CENTER);

        // --- SỰ KIỆN ---
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

    private void thucHienThongKe(String moc, boolean isDoanhThu) {
        // 1. Reset dữ liệu cũ
        model.setRowCount(0);
        double tongDT = 0;
        ArrayList<Object[]> data = new ArrayList<>();

        // 2. Lấy dữ liệu từ DAO
        int start = 0, end = 0;
        if (moc.contains("Quý 1")) { start = 1; end = 3; }
        else if (moc.contains("Quý 2")) { start = 4; end = 6; }
        else if (moc.contains("Quý 3")) { start = 7; end = 9; }
        else if (moc.contains("Quý 4")) { start = 10; end = 12; }

        if (start != 0) { 
            tongDT = tkDAO.getDoanhThuTheoKhoangThang(start, end);
            data = tkDAO.getSPTheoKhoangThang(start, end);
        } else { 
            tongDT = tkDAO.getTongDoanhThu(moc);
            data = tkDAO.getSanPhamBanChay(moc);
        }

        // 3. Hiển thị lên Bảng
        if (isDoanhThu) {
            lblTieuDe.setText("Tổng doanh thu " + moc + ": " + String.format("%,.0f VNĐ", tongDT));
        } else {
            lblTieuDe.setText("Top sản phẩm bán chạy trong " + moc);
        }

        for (Object[] row : data) model.addRow(row);

        // 4. VẼ BIỂU ĐỒ (Logic Mới)
        pnlChartContainer.removeAll(); // Xóa biểu đồ cũ
        
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Double> values = new ArrayList<>();
        
        if (isDoanhThu) {
            // Nếu xem doanh thu, vẽ 1 cột tổng quan
            names.add("Tổng DT");
            values.add(tongDT);
        } else {
            // Nếu xem sản phẩm, lấy Top 5 để vẽ cho đẹp
            int limit = Math.min(data.size(), 5);
            for (int i = 0; i < limit; i++) {
                names.add(data.get(i)[0].toString()); // Tên SP
                values.add(Double.parseDouble(data.get(i)[1].toString())); // Số lượng
            }
        }
        
        // Tạo đối tượng biểu đồ và add vào panel
        SimpleBarChart chart = new SimpleBarChart(names, values, isDoanhThu ? "BIỂU ĐỒ DOANH THU" : "TOP 5 SẢN PHẨM");
        pnlChartContainer.add(chart, BorderLayout.CENTER);
        
        // Cập nhật giao diện
        pnlChartContainer.revalidate();
        pnlChartContainer.repaint();
    }
}

// --- CLASS VẼ BIỂU ĐỒ CỘT ĐƠN GIẢN (Tích hợp sẵn) ---
class SimpleBarChart extends JPanel {
    private ArrayList<String> names;
    private ArrayList<Double> values;
    private String title;

    public SimpleBarChart(ArrayList<String> n, ArrayList<Double> v, String t) {
        this.names = n;
        this.values = v;
        this.title = t;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (values.isEmpty()) return;

        // Chuyển sang Graphics2D để vẽ đẹp hơn (khử răng cưa)
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int pad = 50; // Khoảng cách lề
        
        // Tính độ rộng mỗi cột
        int barWidth = (w - 2 * pad) / Math.max(1, values.size()) / 2;

        // Tìm giá trị lớn nhất để chia tỉ lệ chiều cao
        double maxVal = 1;
        for (Double d : values) maxVal = Math.max(maxVal, d);

        // Vẽ khung trục
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(pad, h - pad, w - pad, h - pad); // Trục X
        g2.drawLine(pad, pad, pad, h - pad); // Trục Y
        
        // Vẽ tiêu đề
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(title, w / 2 - 50, pad / 2);

        // Vẽ các cột
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        for (int i = 0; i < values.size(); i++) {
            // Tính toán tọa độ
            int x = pad + (i * 2 + 1) * barWidth; // Vị trí X
            int barHeight = (int) ((values.get(i) / maxVal) * (h - 2 * pad)); // Chiều cao cột
            int y = h - pad - barHeight; // Vị trí Y (vẽ từ dưới lên)

            // Vẽ cột màu
            g2.setColor(new Color(52, 152, 219)); // Xanh dương
            g2.fillRect(x, y, barWidth, barHeight);
            
            // Vẽ viền cột
            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth, barHeight);

            // Vẽ số liệu trên đỉnh cột
            g2.setColor(new Color(192, 57, 43)); // Đỏ
            String valStr = String.format("%,.0f", values.get(i));
            // Căn giữa số liệu trên cột
            int strWidth = g2.getFontMetrics().stringWidth(valStr);
            g2.drawString(valStr, x + (barWidth - strWidth) / 2, y - 5);
            
            // Vẽ tên dưới chân cột
            g2.setColor(Color.BLACK);
            String name = names.get(i);
            // Cắt bớt tên nếu quá dài
            if (name.length() > 10) name = name.substring(0, 8) + "..";
            int nameWidth = g2.getFontMetrics().stringWidth(name);
            g2.drawString(name, x + (barWidth - nameWidth) / 2, h - pad + 20);
        }
    }
}