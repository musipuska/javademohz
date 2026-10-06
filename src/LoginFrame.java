import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Авторизация — ЧитайГород");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Логотип/заголовок
        JLabel titleLabel = new JLabel("ЧитайГород");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0x23, 0xE1, 0xEF));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Логин
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(new JLabel("Логин:"), gbc);
        loginField = new JTextField(15);
        gbc.gridx = 1;
        add(loginField, gbc);

        // Пароль
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Пароль:"), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Кнопки
        JButton loginButton = new JButton("Войти");
        loginButton.setBackground(new Color(0x23, 0xE1, 0xEF));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 13));
        loginButton.setFocusPainted(false);

        JButton guestButton = new JButton("Войти как гость");
        guestButton.setBackground(Color.LIGHT_GRAY);
        guestButton.setFocusPainted(false);

        gbc.gridy = 3;
        gbc.gridx = 0;
        add(loginButton, gbc);
        gbc.gridx = 1;
        add(guestButton, gbc);

        loginButton.addActionListener(e -> authenticateUser());
        guestButton.addActionListener(e -> openProductList(null));

        getRootPane().setDefaultButton(loginButton);
        setVisible(true);
    }

    private void authenticateUser() {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Введите логин и пароль!",
                    "Ошибка ввода",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT id, roleId, fullName, login FROM Users WHERE login = ? AND passwordHash = MD5(?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getInt("roleId"),
                        rs.getString("fullName"),
                        rs.getString("login")
                );
                JOptionPane.showMessageDialog(this,
                        "Добро пожаловать, " + user.getFullName() + "!",
                        "Успешный вход",
                        JOptionPane.INFORMATION_MESSAGE);
                openProductList(user);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Неверный логин или пароль!\nПроверьте раскладку клавиатуры.",
                        "Ошибка авторизации",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка подключения к БД:\n" + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openProductList(User user) {
        new ProductListFrame(user).setVisible(true);
        dispose();
    }
}