import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductListFrame extends JFrame {
    private User currentUser;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private List<Product> productList = new ArrayList<>();

    public ProductListFrame(User user) {
        this.currentUser = user;
        setTitle("Список товаров — ЧитайГород");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ============ ВЕРХНЯЯ ПАНЕЛЬ ============
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0x23, 0xE1, 0xEF));
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel logoLabel = new JLabel("📚 ЧитайГород");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        logoLabel.setForeground(Color.WHITE);
        topPanel.add(logoLabel, BorderLayout.WEST);

        String userInfo = (user != null)
                ? user.getFullName() + " (" + user.getRoleName() + ")"
                : "Гость";
        JLabel userLabel = new JLabel(userInfo);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        topPanel.add(userLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Выйти");
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setForeground(Color.BLACK);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(logoutButton);
        topPanel.add(rightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ============ ТАБЛИЦА ============
        String[] columns = {"Фото", "Категория", "Наименование", "Производитель",
                "Цена", "Скидка", "Кол-во", "Ед. изм."};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        productTable.setRowHeight(70);
        productTable.setFont(new Font("Arial", Font.PLAIN, 12));
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        productTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(6).setPreferredWidth(70);
        productTable.getColumnModel().getColumn(7).setPreferredWidth(60);

        // Двойной клик → редактирование (только админ)
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && currentUser != null && currentUser.canEdit()) {
                    int row = productTable.getSelectedRow();
                    if (row >= 0 && row < productList.size()) {
                        Product selected = productList.get(row);
                        new AddProductFrame(ProductListFrame.this, selected).setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // ============ НИЖНЯЯ ПАНЕЛЬ ============
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> loadProducts());
        bottomPanel.add(refreshButton);

        if (user != null && user.canEdit()) {
            JButton addButton = new JButton("Добавить товар");
            addButton.setBackground(new Color(0x23, 0xE1, 0xEF));
            addButton.setForeground(Color.WHITE);
            addButton.setFocusPainted(false);
            addButton.addActionListener(e -> new AddProductFrame(this).setVisible(true));
            bottomPanel.add(addButton);
        }

        add(bottomPanel, BorderLayout.SOUTH);

        loadProducts();
    }

    public void loadProducts() {
        tableModel.setRowCount(0);
        productList.clear();
        productList = getProductsFromDB();

        // Цветной рендер для остальных колонок
        Product[] productsArray = productList.toArray(new Product[0]);
        ProductRenderer renderer = new ProductRenderer(productsArray);
        for (int i = 0; i < productTable.getColumnCount(); i++) {
            productTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // ⬇️ Рендер картинок для колонки «Фото» (индекс 0)
        productTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());

        // Заполняем таблицу
        for (Product p : productList) {
            Object[] row = {
                    p.getImagePath(),   // путь к фото или null (→ заглушка)
                    p.getCategoryName(),
                    p.getName(),
                    p.getManufacturerName(),
                    p.getPrice() + " ₽",
                    String.format("%.0f%%", p.getDiscount()),
                    p.getQuantity(),
                    p.getUnitName()
            };
            tableModel.addRow(row);
        }
    }

    private List<Product> getProductsFromDB() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.id, p.name, p.description, p.price, p.discount, p.quantity, p.imagePath, " +
                "c.name AS categoryName, m.name AS manufacturerName, " +
                "s.name AS supplierName, u.name AS unitName " +
                "FROM Products p " +
                "JOIN Categories c ON p.categoryId = c.id " +
                "JOIN Manufacturers m ON p.manufacturerId = m.id " +
                "JOIN Suppliers s ON p.supplierId = s.id " +
                "JOIN Units u ON p.unitId = u.id " +
                "ORDER BY p.id";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("categoryName"),
                        rs.getString("manufacturerName"),
                        rs.getString("supplierName"),
                        rs.getString("unitName"),
                        rs.getDouble("price"),
                        rs.getDouble("discount"),
                        rs.getInt("quantity"),
                        rs.getString("imagePath")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка загрузки товаров:\n" + e.getMessage(),
                    "Ошибка БД", JOptionPane.ERROR_MESSAGE);
        }
        return products;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}