package Server.Security;

import Securit.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlDatabase implements Database {


    private final String url = "jdbc:sqlite:Server/accounts.db";


    public SqlDatabase() {
        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "login TEXT PRIMARY KEY," +
                    "password BLOB NOT NULL," +"pubKey BLOB NOT NULL)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public byte[] read(byte[] data) {
        return new byte[0];
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
