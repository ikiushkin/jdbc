package jdbc.task_two;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static jdbc.JDBCConnection.getConnectionT3;
import static jdbc.JDBCConnection.log;

public class StoredProcedures {

    public static void createStoredProcedures() {
        try(Connection conn = getConnectionT3()) {
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
