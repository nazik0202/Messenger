package Server.Security;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;

public class SQLITEDATABASE implements Database{

    private static final String  url = "jdbc:sqlite:src/main/java/Server/DataBase/accounts.db";

    public SQLITEDATABASE() {
        // файл у робочій директорії

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                // Створюємо таблицю користувачів
                stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                        "login TEXT PRIMARY KEY," +
                        "password BLOB NOT NULL, salt BLOB NOT NULL)");
                System.out.println("Database is ready!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public byte[] readPassword(String login) {
        String sql = "SELECT password FROM users WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
//                byte[] bytes = rs.getBytes("salt");
                return rs.getBytes("password");
            } else {
                return null; // no such login
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error reading from database", e);
        }
    }

    @Override
    public byte[] readSL(String login) {
        String sql = "SELECT salt FROM users WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBytes("salt");

            } else {
                return null; // no such login
            }
        }catch (NullPointerException n){
            return null;
        } catch (SQLException e) {
            System.out.println("Error "+ e);
            throw new RuntimeException("Error reading from database"+e, e);
        }
    }

    @Override
    public void edit(String login, byte[] newData) {

    }

    @Override
    public void edit(String login, byte[] newData, byte[] publicKey) {

    }


    @Override
    public void write(String login, byte[] password, byte[] salt) {
        String sql = "INSERT INTO users(login,password,salt)" +
                "VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setBytes(2, password);
            pstmt.setBytes(3, salt);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error reading from database", e);
        }
    }

    @Override
    public void delete(String login) {

    }

}
