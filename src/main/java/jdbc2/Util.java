package jdbc2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static jdbc.JDBCConnection.getConnection;
import static jdbc.JDBCConnection.log;

public class Util {

    static void copyDatabase(String jdbcPropertyFile, String DBName) {
        try(Connection connection = getConnection(jdbcPropertyFile)) {
            String to = copyDatabaseStructure(jdbcPropertyFile, DBName);

            PreparedStatement ps = connection.prepareStatement("SHOW TABLES;");

            List<String> tables = new ArrayList<>();

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }

            for (String table : tables) {
                String SQL = String.format("INSERT INTO %s.%s SELECT * FROM %s.%s;", to, table, DBName, table);
                ps = connection.prepareStatement(SQL);
                ps.executeUpdate();
                log.info(SQL);
            }

            log.info(String.format("The database: %s has been copied. Name of the copy: %s", DBName, to));
        } catch (SQLException e) {
            log.error(e);
        }
    }

    static String copyDatabaseStructure(String jdbcPropertyFile, String DBName) {
        String newBDName = DBName + "_copy";

        try(Connection connection = getConnection(jdbcPropertyFile)) {
            PreparedStatement ps = connection.prepareStatement("SHOW TABLES;");

            List<String> tables = new ArrayList<>();

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }

            ps = connection.prepareStatement("DROP DATABASE IF EXISTS " + newBDName);
            ps.executeUpdate();

            ps = connection.prepareStatement("CREATE DATABASE " + newBDName);
            ps.executeUpdate();

            ps = connection.prepareStatement("USE " + newBDName);
            ps.executeUpdate();

            for (String tablesName : tables) {
                ps = connection.prepareStatement("SHOW COLUMNS FROM " + DBName + "." + tablesName);

                ResultSet rs = ps.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();

                StringBuilder sbSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                        .append(newBDName)
                        .append(".")
                        .append(tablesName)
                        .append(" (\n");

                while (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 1; j < rsmd.getColumnCount() + 1; j++) {
                        String columnType = rsmd.getColumnName(j);

                        switch (columnType) {
                            case "Null":
                                if (rs.getString(j).equals("NO")) {
                                    sb.append("NOT NULL").append(" ");
                                }
                                continue;
                            case "Key":
                                continue;
                            case "Default":
                                String s1 = rs.getString(j);
                                if (s1 != null) {
                                    sb.append("DEFAULT ").append(s1);
                                }
                                continue;
                            case "Extra":
                                String s2 = rs.getString(j);
                                if (s2 != null) {
                                    if (s2.equals("auto_increment")) {
                                        sb.append(s2.toUpperCase(Locale.ROOT)).append(" ");
                                    }
                                    continue;
                                }
                                break;
                        }
                        sb.append(rs.getString(j)).append(" ");
                    }

                    if (sb.toString().startsWith("ID")) {
                        sb.append("PRIMARY KEY").append(" ");
                    }

                    sbSQL.append("\t")
                            .append(sb.toString().trim())
                            .append(",\n");
                }

                String SQL = sbSQL.substring(0, sbSQL.lastIndexOf(",")) + "\n)";
                ps = connection.prepareStatement(SQL);
                ps.executeUpdate();

                log.info(SQL + "\n");
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return newBDName;
    }
}
