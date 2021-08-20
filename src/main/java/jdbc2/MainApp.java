package jdbc2;

import jdbc.JDBCConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MainApp {
    private static final String jdbcPropertyFile = "src/main/resources/jdbc-t3.properties";

    public static void main(String[] args) {
        try {
            Connection connection = JDBCConnection.getConnection(jdbcPropertyFile);

            PreparedStatement ps = connection.prepareStatement("SHOW TABLES;");

            List<String> tables = new ArrayList<>();

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {

                tables.add(resultSet.getString(1));
            }

            System.out.println(tables);
            //DatabaseMetaData metaData = connection.getMetaData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
