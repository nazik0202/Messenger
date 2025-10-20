import java.sql.*;

public class SQLiteTest {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:src/main/java/Server/DataBase/accounts.db"; // файл у робочій директорії

        String sql = "SELECT * FROM users WHERE login='nazik'";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            for(int i=1; i<4; i++) {
                System.out.println(rs.getString(i));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading from database", e);
        }
    }
}
