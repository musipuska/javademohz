import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/bookshop";
    private static final String USER = "postgres";  // Ваш пользователь
    private static final String PASSWORD = "postgres"; // Ваш пароль

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Подключение к БД успешно!");
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println("Ошибка подключения к БД: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Подключение закрыто.");
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии: " + e.getMessage());
            }
        }
    }
}