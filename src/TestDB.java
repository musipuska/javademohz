import java.sql.*;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("Проверка подключения к БД...");
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/bookshop",
                "postgres",
                "12345"
            );
            System.out.println("✅ Подключение к БД УСПЕШНО!");
            
            // Проверяем пользователей
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT login, fullName FROM Users");
            System.out.println("Пользователи в БД:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("login") + " (" + rs.getString("fullName") + ")");
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
