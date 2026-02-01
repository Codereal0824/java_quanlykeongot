package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import model.CartItem;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PDFPrinter {

    // Định dạng tiền tệ
    private static final DecimalFormat df = new DecimalFormat("#,###");

    public void xuatHoaDon(int maHD, String thuNgan, double tongTien, List<CartItem> gioHang) {
        try {
            // 1. Tạo tên file: HoaDon_Ma_ThoiGian.pdf
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "HoaDon_" + maHD + "_" + timeStamp + ".pdf";

            // 2. Khởi tạo Document (Khổ A5 cho giống hóa đơn siêu thị)
            Document document = new Document(PageSize.A5);
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // 3. Cài đặt Font tiếng Việt (BẮT BUỘC CÓ FILE arial.ttf Ở THƯ MỤC GỐC)
            BaseFont bf = BaseFont.createFont("images/ARIAL.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font fontTieuDe = new Font(bf, 16, Font.BOLD, BaseColor.BLUE);
            Font fontDam = new Font(bf, 11, Font.BOLD);
            Font fontThuong = new Font(bf, 11, Font.NORMAL);

            // 4. Nội dung Header
            Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG - KẸO NGỌT DNC", fontTieuDe);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            document.add(new Paragraph("Đc: Đại học Nam Cần Thơ", fontThuong));
            document.add(new Paragraph("Ngày lập: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), fontThuong));
            document.add(new Paragraph("Thu ngân: " + thuNgan, fontDam));
            document.add(new Paragraph("Mã hóa đơn: #" + maHD, fontDam));
            document.add(Chunk.NEWLINE); // Xuống dòng

            // 5. Tạo bảng sản phẩm (5 cột)
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{10f, 40f, 15f, 20f, 25f}); // Tỉ lệ độ rộng cột

            // Header bảng
            addHeaderCell(table, "STT", fontDam);
            addHeaderCell(table, "Tên Kẹo", fontDam);
            addHeaderCell(table, "SL", fontDam);
            addHeaderCell(table, "Đơn giá", fontDam);
            addHeaderCell(table, "Thành tiền", fontDam);

            // Dữ liệu bảng
            int stt = 1;
            for (CartItem item : gioHang) {
                addCell(table, String.valueOf(stt++), fontThuong, Element.ALIGN_CENTER);
                addCell(table, item.getTenSP(), fontThuong, Element.ALIGN_LEFT);
                addCell(table, String.valueOf(item.getSoLuong()), fontThuong, Element.ALIGN_CENTER);
                addCell(table, df.format(item.getDonGia()), fontThuong, Element.ALIGN_RIGHT);
                addCell(table, df.format(item.getThanhTien()), fontThuong, Element.ALIGN_RIGHT);
            }
            document.add(table);

            // 6. Footer Tổng tiền
            document.add(Chunk.NEWLINE);
            Paragraph pTong = new Paragraph("TỔNG THANH TOÁN: " + df.format(tongTien) + " VNĐ", fontDam);
            pTong.setAlignment(Element.ALIGN_RIGHT);
            document.add(pTong);

            Paragraph pCamOn = new Paragraph("--- Cảm ơn quý khách! ---", fontThuong);
            pCamOn.setAlignment(Element.ALIGN_CENTER);
            pCamOn.setSpacingBefore(10);
            document.add(pCamOn);

            document.close();
            System.out.println("Xuất PDF thành công: " + fileName);

            // Tự động mở file sau khi xuất (Chỉ chạy trên Windows)
            try {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileName);
            } catch (Exception e) { /* Bỏ qua nếu không mở được */ }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hàm phụ để thêm Header bảng (In đậm, nền xám nhẹ)
    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(5);
        table.addCell(cell);
    }

    // Hàm phụ để thêm dữ liệu bảng
    private void addCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setPadding(5);
        table.addCell(cell);
    }
}