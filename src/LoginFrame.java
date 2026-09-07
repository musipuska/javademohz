import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField loginField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("Авторизация - ЧитайГород");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Заголовок
        JLabel titleLabel = new JLabel("Добро пожаловать в ЧитайГород!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
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
        JButton guestButton = new JButton("Войти как гость");

        gbc.gridy = 3;
        gbc.gridx = 0;
        add(loginButton, gbc);
        gbc.gridx = 1;
        add(guestButton, gbc);

        // Обработчики
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });

        guestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProductList(null);
            }
        });

        setVisible(true);
    }

    private void authenticateUser() {
        String login = loginField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (login.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите логин и пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT * FROM Users WHERE login = ? AND passwordHash = MD5(?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getInt("roleId"),
                        rs.getString("fullName"),
                        rs.getString("login"),
                        rs.getString("passwordHash")
                );
                JOptionPane.showMessageDialog(this, "Добро пожаловать, " + user.getFullName() + "!");
                openProductList(user);
            } else {
                JOptionPane.showMessageDialog(this, "Неверный логин или пароль!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка БД: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openProductList(User user) {
        new ProductListFrame(user).setVisible(true);
        dispose(); // Закрываем окно авторизации
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}