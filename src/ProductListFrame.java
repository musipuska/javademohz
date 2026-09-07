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

    public ProductListFrame(User user) {
        this.currentUser = user;
        setTitle("Список товаров - ЧитайГород");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Верхняя панель с информацией о пользователе
        JPanel topPanel = new JPanel(new BorderLayout());
        String userInfo = (user != null) ? "Пользователь: " + user.getFullName() + " (" + user.getRoleName() + ")" : "Гость";
        JLabel userLabel = new JLabel(userInfo);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(userLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Выйти");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Таблица товаров
        String[] columns = {"Фото", "Категория", "Название", "Производитель", "Цена", "Скидка", "Кол-во"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        // Устанавливаем размер колонок
        productTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        productTable.getColumnModel().getColumn(6).setPreferredWidth(60);

        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // Нижняя панель для кнопок
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> loadProducts());
        bottomPanel.add(refreshButton);

        if (user != null && user.getRoleId() == 4) { // Администратор
            JButton addButton = new JButton("Добавить товар");
            addButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Форма добавления товара (в разработке)"));
            bottomPanel.add(addButton);
        }

        add(bottomPanel, BorderLayout.SOUTH);

        // Загружаем товары
        loadProducts();
    }

    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = getProductsFromDB();

        for (Product p : products) {
            Object[] row = {
                    "📘", // Заглушка для фото
                    p.getCategoryName(),
                    p.getName(),
                    p.getManufacturerName(),
                    p.getPrice() + " ₽",
                    p.getDiscount() + "%",
                    p.getQuantity()
            };
            tableModel.addRow(row);
        }
    }

    private List<Product> getProductsFromDB() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, c.name as categoryName, m.name as manufacturerName, " +
                "s.name as supplierName, u.name as unitName " +
                "FROM Products p " +
                "JOIN Categories c ON p.categoryId = c.id " +
                "JOIN Manufacturers m ON p.manufacturerId = m.id " +
                "JOIN Suppliers s ON p.supplierId = s.id " +
                "JOIN Units u ON p.unitId = u.id";

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
                        rs.getInt("quantity")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки товаров: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        return products;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите выйти?", "Выход", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}