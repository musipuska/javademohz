import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AddProductFrame extends JFrame {
    private JTextField nameField;
    private JTextField descField;
    private JTextField priceField;
    private JTextField discountField;
    private JTextField quantityField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> manufacturerCombo;
    private JComboBox<String> supplierCombo;
    private JComboBox<String> unitCombo;

    private JLabel photoPreview;
    private String selectedImagePath;

    private Map<String, Integer> categoryMap = new HashMap<>();
    private Map<String, Integer> manufacturerMap = new HashMap<>();
    private Map<String, Integer> supplierMap = new HashMap<>();
    private Map<String, Integer> unitMap = new HashMap<>();

    private ProductListFrame parentFrame;
    private Product editingProduct;

    private static final String STUB_IMAGE = "cover1__w220.jpg"; // заглушка

    // Конструктор для ДОБАВЛЕНИЯ
    public AddProductFrame(ProductListFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.editingProduct = null;
        setTitle("Добавление товара — ЧитайГород");
        initUI();
    }

    // Конструктор для РЕДАКТИРОВАНИЯ
    public AddProductFrame(ProductListFrame parentFrame, Product product) {
        this.parentFrame = parentFrame;
        this.editingProduct = product;
        setTitle("Редактирование товара — ЧитайГород");
        initUI();
        loadProductData();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(540, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Заголовок
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0x23, 0xE1, 0xEF));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel headerLabel = new JLabel(editingProduct == null
                ? "Добавление товара" : "Редактирование товара");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Форма
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Фото
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Фото товара:"), gbc);

        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        photoPanel.setBackground(Color.WHITE);

        photoPreview = new JLabel();
        photoPreview.setPreferredSize(new Dimension(80, 100));
        photoPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        photoPreview.setHorizontalAlignment(SwingConstants.CENTER);
        loadPreview(null);

        JButton choosePhotoButton = new JButton("Выбрать...");
        choosePhotoButton.addActionListener(e -> choosePhoto());

        photoPanel.add(photoPreview);
        photoPanel.add(choosePhotoButton);

        gbc.gridx = 1;
        formPanel.add(photoPanel, gbc);

        // Наименование
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Наименование:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Категория
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Категория:"), gbc);
        categoryCombo = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(categoryCombo, gbc);

        // Описание
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Описание:"), gbc);
        descField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(descField, gbc);

        // Производитель
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Производитель:"), gbc);
        manufacturerCombo = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(manufacturerCombo, gbc);

        // Поставщик
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Поставщик:"), gbc);
        supplierCombo = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(supplierCombo, gbc);

        // Цена
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Цена (₽):"), gbc);
        priceField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(priceField, gbc);

        // Единица
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Единица измерения:"), gbc);
        unitCombo = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(unitCombo, gbc);

        // Количество
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Количество:"), gbc);
        quantityField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(quantityField, gbc);

        // Скидка
        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Скидка (%):"), gbc);
        discountField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(discountField, gbc);

        // ID (только при редактировании)
        if (editingProduct != null) {
            gbc.gridx = 0; gbc.gridy = row++;
            formPanel.add(new JLabel("ID товара:"), gbc);
            JTextField idField = new JTextField(String.valueOf(editingProduct.getId()));
            idField.setEditable(false);
            idField.setBackground(new Color(0xEE, 0xEE, 0xEE));
            gbc.gridx = 1;
            formPanel.add(idField, gbc);
        }

        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // Кнопки
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton(editingProduct == null ? "Добавить" : "Сохранить");
        saveButton.setBackground(new Color(0x23, 0xE1, 0xEF));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 13));
        saveButton.addActionListener(e -> saveProduct());

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        loadComboBoxes();
    }

    private void loadComboBoxes() {
        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM Categories ORDER BY name");
            while (rs.next()) {
                categoryMap.put(rs.getString("name"), rs.getInt("id"));
                categoryCombo.addItem(rs.getString("name"));
            }

            rs = stmt.executeQuery("SELECT id, name FROM Manufacturers ORDER BY name");
            while (rs.next()) {
                manufacturerMap.put(rs.getString("name"), rs.getInt("id"));
                manufacturerCombo.addItem(rs.getString("name"));
            }

            rs = stmt.executeQuery("SELECT id, name FROM Suppliers ORDER BY name");
            while (rs.next()) {
                supplierMap.put(rs.getString("name"), rs.getInt("id"));
                supplierCombo.addItem(rs.getString("name"));
            }

            rs = stmt.executeQuery("SELECT id, name FROM Units ORDER BY name");
            while (rs.next()) {
                unitMap.put(rs.getString("name"), rs.getInt("id"));
                unitCombo.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка загрузки справочников:\n" + e.getMessage(),
                    "Ошибка БД", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProductData() {
        if (editingProduct == null) return;
        nameField.setText(editingProduct.getName());
        descField.setText(editingProduct.getDescription());
        priceField.setText(String.valueOf(editingProduct.getPrice()));
        discountField.setText(String.valueOf(editingProduct.getDiscount()));
        quantityField.setText(String.valueOf(editingProduct.getQuantity()));

        categoryCombo.setSelectedItem(editingProduct.getCategoryName());
        manufacturerCombo.setSelectedItem(editingProduct.getManufacturerName());
        supplierCombo.setSelectedItem(editingProduct.getSupplierName());
        unitCombo.setSelectedItem(editingProduct.getUnitName());

        if (editingProduct.getImagePath() != null && !editingProduct.getImagePath().isEmpty()) {
            selectedImagePath = editingProduct.getImagePath();
            loadPreview(selectedImagePath);
        }
    }

    private void choosePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Выберите изображение товара");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Изображения (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            selectedImagePath = file.getAbsolutePath();
            loadPreview(selectedImagePath);
        }
    }

    /**
     * Загружает картинку в preview. Если path == null — показывает заглушку.
     */
    private void loadPreview(String path) {
        ImageIcon icon = null;

        if (path != null && new File(path).exists()) {
            icon = new ImageIcon(path);
        } else {
            java.net.URL stubUrl = getClass().getClassLoader().getResource(STUB_IMAGE);
            if (stubUrl != null) icon = new ImageIcon(stubUrl);
        }

        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
            photoPreview.setIcon(new ImageIcon(scaled));
            photoPreview.setText(null);
        } else {
            photoPreview.setIcon(null);
            photoPreview.setText("Нет фото");
        }
    }

    private void saveProduct() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Введите наименование товара!");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim().replace(",", "."));
            if (price < 0) {
                showError("Цена не может быть отрицательной!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Введите корректную цену (например: 1500.50)");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity < 0) {
                showError("Количество не может быть отрицательным!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Введите корректное количество (целое число)");
            return;
        }

        double discount;
        try {
            String discountText = discountField.getText().trim();
            discount = discountText.isEmpty() ? 0 : Double.parseDouble(discountText.replace(",", "."));
            if (discount < 0 || discount > 100) {
                showError("Скидка должна быть от 0 до 100%!");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Введите корректную скидку (например: 15)");
            return;
        }

        int categoryId = categoryMap.get(categoryCombo.getSelectedItem());
        int manufacturerId = manufacturerMap.get(manufacturerCombo.getSelectedItem());
        int supplierId = supplierMap.get(supplierCombo.getSelectedItem());
        int unitId = unitMap.get(unitCombo.getSelectedItem());

        String description = descField.getText().trim();

        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt;

            if (editingProduct == null) {
                String query = "INSERT INTO Products " +
                        "(categoryId, manufacturerId, supplierId, unitId, name, description, " +
                        "price, discount, quantity, imagePath) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, categoryId);
                pstmt.setInt(2, manufacturerId);
                pstmt.setInt(3, supplierId);
                pstmt.setInt(4, unitId);
                pstmt.setString(5, name);
                pstmt.setString(6, description);
                pstmt.setDouble(7, price);
                pstmt.setDouble(8, discount);
                pstmt.setInt(9, quantity);
                pstmt.setString(10, selectedImagePath);
            } else {
                String query = "UPDATE Products SET " +
                        "categoryId = ?, manufacturerId = ?, supplierId = ?, unitId = ?, " +
                        "name = ?, description = ?, price = ?, discount = ?, quantity = ?, " +
                        "imagePath = ? " +
                        "WHERE id = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setInt(1, categoryId);
                pstmt.setInt(2, manufacturerId);
                pstmt.setInt(3, supplierId);
                pstmt.setInt(4, unitId);
                pstmt.setString(5, name);
                pstmt.setString(6, description);
                pstmt.setDouble(7, price);
                pstmt.setDouble(8, discount);
                pstmt.setInt(9, quantity);
                pstmt.setString(10, selectedImagePath);
                pstmt.setInt(11, editingProduct.getId());
            }

            pstmt.executeUpdate();
            pstmt.close();

            JOptionPane.showMessageDialog(this,
                    editingProduct == null ? "Товар успешно добавлен!" : "Товар успешно обновлён!",
                    "Успех", JOptionPane.INFORMATION_MESSAGE);

            if (parentFrame != null) parentFrame.loadProducts();
            dispose();

        } catch (SQLException e) {
            showError("Ошибка сохранения:\n" + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}