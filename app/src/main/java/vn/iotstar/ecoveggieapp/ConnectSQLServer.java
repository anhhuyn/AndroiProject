package vn.iotstar.ecoveggieapp;


import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectSQLServer {
    Connection conn;
    String classes = "net.sourceforge.jtds.jdbc.Driver";
    protected static String ip = "10.0.2.2";
    protected static String port = "1433";
    protected static String db = "EcoVeggie";
    protected static String un = "sa";
    protected static String password = "@Huyen171003";

    public Connection CONN(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        conn = null;
        try{
            Class.forName(classes);
            String conUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databaseName=" + db;
            conn = DriverManager.getConnection(conUrl,un,password);
        }
        catch (ClassNotFoundException | SQLException e ){
            throw  new RuntimeException(e);
        }
        return conn;
    }
    public boolean kiemTraDangNhap(String email, String matKhau) {
        try {
            String query = "{call SP_KiemTraDangNhap(?, ?)}";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            stmt.setString(2, matKhau);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "Success".equalsIgnoreCase(rs.getString("Status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean dangKy(String username, String email, String phone, String password) {
        try {
            String query = "INSERT INTO users (username, email, phone, password) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, password);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(String email, String newPassword) {
        try {
            String query = "UPDATE users SET password = ? WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, newPassword);
            stmt.setString(2, email);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
