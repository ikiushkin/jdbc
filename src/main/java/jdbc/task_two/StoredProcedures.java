package jdbc.task_two;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static jdbc.JDBCConnection.*;

public class StoredProcedures {

    public static void createStoredProcedures() {
        try(Connection conn = getConnection("src/main/resources/jdbc-t1.properties")) {
            PreparedStatement pr = conn.prepareStatement("CREATE PROCEDURE employees.get_all()\n" +
                    "BEGIN\n" +
                    "SELECT * FROM employee;\n" +
                    "END");
            pr.executeUpdate();
            log.info("Procedure added!");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}
