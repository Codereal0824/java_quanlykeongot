package main;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.LoginForm;

public class Main {
    public static void main(String[] args) {
        // Chạy ứng dụng trong luồng sự kiện của Swing (Chuẩn an toàn luồng)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // (Tùy chọn) Chỉnh giao diện cho giống hệ điều hành Windows/Mac
                    // Giúp nút bấm đẹp hơn, không bị thô như giao diện Java gốc
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // KHỞI ĐỘNG MÀN HÌNH ĐĂNG NHẬP
                new LoginForm().setVisible(true);
            }
        });
    }
}