package jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConnection {
    public static Logger log = LogManager.getRootLogger();

    public static Connection getConnectionT1() throws SQLException {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("src/main/resources/jdbc-t1.properties")) {
            prop.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("File not found " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        return DriverManager.getConnection(
                prop.getProperty("jdbc.url"),
                prop.getProperty("jdbc.username"),
                prop.getProperty("jdbc.password")
        );
    }

    public static Connection getConnectionT3() throws SQLException {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("src/main/resources/jdbc-t3.properties")) {
            prop.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            log.error("File not found " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

        return DriverManager.getConnection(
                prop.getProperty("jdbc.url"),
                prop.getProperty("jdbc.username"),
                prop.getProperty("jdbc.password")
        );
    }
}
