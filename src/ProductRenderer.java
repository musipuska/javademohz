import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ProductRenderer extends DefaultTableCellRenderer {

    private static final Color DISCOUNT_COLOR = new Color(0x23, 0xE1, 0xEF);
    private static final Color OUT_OF_STOCK_COLOR = new Color(0xD3, 0xD3, 0xD3);
    private static final Color RED_COLOR = new Color(0xD3, 0x2F, 0x2F);
    private static final Color BLACK_COLOR = Color.BLACK;

    private final Product[] products;

    public ProductRenderer(Product[] products) {
        this.products = products;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        JLabel label = (JLabel) c;

        if (row < products.length) {
            Product product = products[row];

            // Фон строки
            if (product.isOutOfStock()) {
                c.setBackground(OUT_OF_STOCK_COLOR);
            } else if (product.isBigDiscount()) {
                c.setBackground(DISCOUNT_COLOR);
            } else {
                c.setBackground(Color.WHITE);
            }

            if (column == 4) {
                if (product.getDiscount() > 0) {
                    String html = "<html><strike><font color='#D32F2F'>" +
                            String.format("%.2f", product.getPrice()) + " ₽</font></strike> " +
                            "<font color='black'><b>" +
                            String.format("%.2f", product.getFinalPrice()) + " ₽</b></font></html>";
                    label.setText(html);
                } else {
                    label.setText(String.format("%.2f ₽", product.getPrice()));
                }
            }
        }

        return c;
    }
}