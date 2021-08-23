package jdbc2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static jdbc.JDBCConnection.getConnection;
import static jdbc.JDBCConnection.log;

public class Util {
    static String copyDatabaseStructure(String jdbcPropertyFile, String DBName) {
        String newDBName = DBName + "_copy";

        try(Connection connection = getConnection(jdbcPropertyFile)) {
            PreparedStatement ps = connection.prepareStatement("SHOW TABLES;");
            // Лист с таблицами исходной БД
            List<String> tables = new ArrayList<>();

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }
            // Создаем копию БД
            ps = connection.prepareStatement("DROP DATABASE IF EXISTS " + newDBName);
            ps.executeUpdate();
            ps = connection.prepareStatement("CREATE DATABASE " + newDBName);
            ps.executeUpdate();

            ps = connection.prepareStatement("USE " + newDBName);
            ps.executeUpdate();

            // Формируем select для создания поочередно каждой таблицы
            for (String tablesName : tables) {
                // Читаем названия всех колонок каждой таблицы
                ps = connection.prepareStatement("SHOW COLUMNS FROM " + DBName + "." + tablesName);

                ResultSet rs = ps.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();

                StringBuilder sbSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                        .append(newDBName)
                        .append(".")
                        .append(tablesName)
                        .append(" (\n");

                // Читаем поочередно тмп каждой колонки исходной таблицы и формируем из неё новый запрос для добавления
                while (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 1; j < rsmd.getColumnCount() + 1; j++) {
                        String columnType = rsmd.getColumnName(j);
                        // Обработка CONSTRAINT
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
                    // PRIMARY KEY для ID
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
        return newDBName;
    }

    static void copyDatabaseReverse(String jdbcPropertyFile, String DBName, boolean reverse) {
        try(Connection connection = getConnection(jdbcPropertyFile)) {
            String to = copyDatabaseStructure(jdbcPropertyFile, DBName);

            PreparedStatement ps = connection.prepareStatement("SHOW TABLES;");
            ResultSet resultSet = ps.executeQuery();
            List<String> tables = new ArrayList<>();

            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }

            for (String table : tables) {
                boolean isStringPresent = false;

                ps = connection.prepareStatement(String.format("SELECT * FROM %s.%s;", DBName, table));
                ResultSet rs = ps.executeQuery();
                ResultSetMetaData rsmd = rs.getMetaData();
                // Ищем что реверсировать
                for (int i = 1; i < rsmd.getColumnCount()+1; i++) {
                    String columnType = rsmd.getColumnTypeName(i);
                    if (columnType.matches("VARCHAR*")) {
                        isStringPresent = true;
                    }
                }
                // Если нашли построчно добавляем в новую базу
                if (isStringPresent && reverse) {
                    try {
                        while (rs.next()) {
                            StringBuilder sbSQL = new StringBuilder("INSERT INTO ")
                                    .append(to).append(".").append(table).append(" ")
                                    .append("VALUES (");

                            for (int j = 1; j < rsmd.getColumnCount()+1; j++) {
                                String type = rsmd.getColumnTypeName(j);
                                if (type.matches("VARCHAR*")) {
                                    sbSQL.append("'")
                                            .append(new StringBuilder(rs.getString(j).replaceAll("['\"]", "")).reverse())
                                            .append("'").append(", ");
                                } else if (type.matches("DATETIME")) {
                                    sbSQL.append("'")
                                            .append(rs.getString(j))
                                            .append("'").append(", ");
                                } else {
                                    sbSQL.append(rs.getString(j))
                                            .append(", ");
                                }
                            }
                            String SQL = sbSQL.substring(0, sbSQL.lastIndexOf(",")) + ");";
                            System.out.println(SQL);
                            ps = connection.prepareStatement(SQL);
                            ps.executeUpdate();
                        }
                    } catch (SQLException e) {
                        log.error(e);
                    }
                // Если нет, просто копируем всю сразу
                } else {
                    String SQL = String.format("INSERT INTO %s.%s SELECT * FROM %s.%s;", to, table, DBName, table);
                    ps = connection.prepareStatement(SQL);
                    ps.executeUpdate();
                }

                log.info(String.format("The database: %s.%s has been copied. Name of the copy: %s.%s", DBName, table, to, table));
            }
        } catch (SQLException e) {
            log.error(e);
        }
    }
}
