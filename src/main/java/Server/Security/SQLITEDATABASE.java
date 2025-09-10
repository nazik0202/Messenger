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
                        "password BLOB NOT NULL, publicKey BLOB NOT NULL)");
                System.out.println("Database is ready!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public byte[] readPassword(String login) {
        String sql = "SELECT publicKey FROM users WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
//                byte[] bytes = rs.getBytes("publicKey");
                return rs.getBytes("password");
            } else {
                return null; // no such login
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error reading from database", e);
        }
    }

    @Override
    public PublicKey readPK(String login) {
        String sql = "SELECT publicKey FROM users WHERE login = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                byte[] bytes = rs.getBytes("publicKey");
                try {
                    X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
                    KeyFactory keyFactory = KeyFactory.getInstance("RRSA/ECB/PKCS1Padding");
                    return keyFactory.generatePublic(spec);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                return null; // no such login
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error reading from database", e);
        }
    }

    @Override
    public void edit(byte[] data, byte[] newData) {

    }

    @Override
    public void edit(byte[] data, byte[] newData, byte[] publicKey) {

    }

    @Override
    public void write(byte[] data, byte password, byte[] publicKey) {

    }

    @Override
    public void delete(byte[] data) {

    }
}
