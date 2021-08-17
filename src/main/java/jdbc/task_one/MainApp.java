package jdbc.task_one;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static jdbc.JDBCConnection.getConnectionT1;
import static jdbc.JDBCConnection.log;


public class MainApp {
    public static void main(String[] args) {

        //insert(new Employee("Test1", new BigDecimal("30000")));
        //delete(1);
        print();
    }

    static List<Employee> print() {
        List<Employee> result = new ArrayList<>();

        String SQL_SELECT = "Select * from employees.employee";

        try (Connection conn = getConnectionT1();
             PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                String name = resultSet.getString("NAME");
                BigDecimal salary = resultSet.getBigDecimal("SALARY");
                Timestamp createdDate = resultSet.getTimestamp("CREATED_DATE");

                Employee obj = new Employee();
                obj.setId(id);
                obj.setName(name);
                obj.setSalary(salary);
                // Timestamp -> LocalDateTime
                obj.setCreatedDate(createdDate.toLocalDateTime());

                result.add(obj);
            }
            result.forEach(System.out::println);
        } catch (SQLException e) {
            log.error("SQL State: " + e.getSQLState() + ": " + e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return result;
    }

    static int insert(Employee employee) {
        int result = 0;
        try (Connection conn = getConnectionT1();
             PreparedStatement insert = conn.prepareStatement("INSERT INTO employees.employee (NAME, SALARY) VALUES (?, ?)")) {

            insert.setString(1, employee.getName());
            insert.setBigDecimal(2, employee.getSalary());
            result = insert.executeUpdate();
            log.info("The employee: " + employee.getName() + " was successfully added, ID = " + result);
        } catch (Exception e) {
            log.error(e);
        }

        return result;
    }

    static void update(int id, Employee employee) {
        try (Connection conn = getConnectionT1();
             PreparedStatement update = conn.prepareStatement("UPDATE employees.employee SET NAME=?, SALARY=? WHERE ID=?")) {

            update.setString(1, employee.getName());
            update.setBigDecimal(2, employee.getSalary());
            update.setInt(3, id);
            update.executeUpdate();
            log.info("The employee: " + employee.getName() + " was successfully updated!");
        } catch (Exception e) {
            log.error("The employee with the " + id + " was not found in the database!");
            log.error(e);
        }
    }

    static void delete(int id) {
        try (Connection conn = getConnectionT1();
             PreparedStatement delete = conn.prepareStatement("DELETE FROM employees.employee WHERE ID=?")) {

            delete.setInt(1, id);
            delete.executeUpdate();
            log.info("The employee with the " + id + " was successfully deleted!");
        } catch (Exception e) {
            log.error("The employee with the " + id + " was not found in the database!");
            log.error(e);
        }
    }
}
