import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ImageRenderer extends JLabel implements TableCellRenderer {

    private static final int WIDTH = 50;
    private static final int HEIGHT = 70;
    private static final String STUB_IMAGE = "cover1__w220.jpg"; // заглушка

    public ImageRenderer() {
        setOpaque(true);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }

        String imagePath = (value != null) ? value.toString() : null;
        ImageIcon icon = loadImage(imagePath);

        setIcon(icon);
        setText(null);

        return this;
    }

    /**
     * Загружает картинку. Если path пустой или файл не найден — заглушка.
     */
    private ImageIcon loadImage(String path) {
        ImageIcon icon = null;

        // 1. Пытаемся загрузить указанный файл
        if (path != null && !path.isEmpty()) {
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                icon = new ImageIcon(path);
            }
        }

        // 2. Если не нашли — берём заглушку из resources
        if (icon == null) {
            java.net.URL stubUrl = getClass().getClassLoader().getResource(STUB_IMAGE);
            if (stubUrl != null) {
                icon = new ImageIcon(stubUrl);
            }
        }

        if (icon == null) return null;

        // Масштабируем
        Image scaled = icon.getImage().getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}