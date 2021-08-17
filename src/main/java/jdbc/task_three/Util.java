package jdbc.task_three;

import jdbc.task_three.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static jdbc.JDBCConnection.getConnectionT3;
import static jdbc.JDBCConnection.log;

public class Util {

    public static Integer getDBSize(String DBName) {
        List<Object> objects = new ArrayList<>();
        try (Connection conn = getConnectionT3();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM social_network." + DBName)) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                objects.add(new Object());
            }
        } catch (SQLException e) {
            log.error(e);
        }
        return objects.size();
    }

    public static void insertUser(User user) {
        try (Connection conn = getConnectionT3();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO social_network.users (NAME, SURNAME) VALUES (?, ?)")) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.executeUpdate();
            log.info("The user: " + user.getName() + " " + user.getSurname() + " was successfully added");
        } catch (Exception e) {
            log.error(e);
        }
    }

    // Задание 3.2
    public static void fillingUsersDB(List<String> users) {
        long count = 0;
        for (String s : users) {
            String[] parsedName = s.split("\\s");
            if (parsedName.length == 2) {
                insertUser(new User(parsedName[0], parsedName[1]));
                count++;
            }
        }
        log.info("Added to the database: " + count);
    }

    public static void fillingFriendshipsDB(List<User> users) {
        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                long id1, id2;

                for (int j = 0; j < 30000; j++) {
                    id1 = (int) (Math.random() * users.size());
                    id2 = (int) (Math.random() * users.size());
                    if (id1 != id2) {
                        try (Connection conn = getConnectionT3();
                             PreparedStatement ps = conn.prepareStatement("INSERT INTO social_network.friendships (USER_ID1, USER_ID2, CREATED_DATE) VALUES (?, ?, ?)")) {

                            ps.setLong(1, id1);
                            ps.setLong(2, id2);
                            ps.setDate(3, createData(new Date(125, 2, 1), new Date(125, 7, 31)));
                            ps.executeUpdate();
                            log.info("The Friend was successfully added");
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                }
            }).start();
        }
    }

    public static void fillingPostsDB(List<User> users, List<String> post) {
        new Thread(() -> {
            for (int j = 0; j < users.size() * 3; j++) {
                try (Connection conn = getConnectionT3();
                     PreparedStatement ps = conn.prepareStatement("INSERT INTO social_network.posts (USER_ID, TEXT, CREATED_DATE) VALUES (?, ?, ?)")) {

                    ps.setLong(1, (long) (Math.random() * users.size()));
                    ps.setString(2, post.get((int) (Math.random() * post.size())));
                    ps.setDate(3, createData(new Date(125, 2, 1), new Date(125, 7, 31)));
                    ps.executeUpdate();
                    log.info("The Post was successfully added");
                } catch (Exception e) {
                    log.error(e);
                }
            }
        }).start();
    }

    public static void fillingLikesDB(Integer posts, Integer users) {
        for (int i = 0; i < 12; i++) {
            new Thread(() -> {
                for (int j = 0; j < 80000; j++) {
                    try (Connection conn = getConnectionT3();
                         PreparedStatement ps = conn.prepareStatement("INSERT INTO social_network.likes (POST_ID, USER_ID, CREATED_DATE) VALUES (?, ?, ?)")) {

                        ps.setLong(1, (long) (Math.random() * posts));
                        ps.setLong(2, (long) (Math.random() * users));
                        ps.setDate(3, createData(new Date(125, 1, 1), new Date(125, 8, 31)));

                        ps.executeUpdate();
                        log.info("The Like was successfully added");
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
            }).start();
        }
    }

    // Генерация случайной даты в диапазоне
    public static Date createData(Date startDate, Date endDate) {
        long random = ThreadLocalRandom.current().nextLong(startDate.getTime(), endDate.getTime());
        return new Date(random);
    }

    // Задача 3
    private static List<User> getUsersFromDB(String SQL_SELECT) {
        List<User> result = new ArrayList<>();
        try (Connection conn = getConnectionT3();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT)) {

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                long id = resultSet.getLong("ID");
                String name = resultSet.getString("NAME");
                String surname = resultSet.getString("SURNAME");
                Timestamp createdDate = resultSet.getTimestamp("CREATED_DATE");

                User obj = new User();
                obj.setId(id);
                obj.setName(name);
                obj.setSurname(surname);
                // Timestamp -> LocalDateTime
                obj.setCreatedDate(createdDate.toLocalDateTime());
                result.add(obj);
            }
        } catch (SQLException e) {
            log.error("SQL State: " + e.getSQLState() + ": " + e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return result;
    }

    // Все пользователи
    public static List<User> getAllUsersDB(String DBName) {
        String SQL_SELECT = "Select * from social_network." + DBName + ";";

        return getUsersFromDB(SQL_SELECT);
    }

    // Задание 3.3
    // У меня не получилось забить так много данных в БД чтобы всё было по 100, но смысл понятен
    public static List<User> getUsersWithFriendsAndLikes() {
        String SQL_SELECT = "select * from users\n" +
                "where\n" +
                "    ID in (\n" +
                "        select USER_ID from likes\n" +
                "        where DATE(CREATED_DATE) between '2025-03-01' and '2025-03-31'\n" +
                "        group by POST_ID\n" +
                "        having count(USER_ID) > 7\n" +
                "    )\n" +
                "AND\n" +
                "    ID in (\n" +
                "        select USER_ID1 from friendships\n" +
                "        where DATE(CREATED_DATE) between '2025-03-01' and '2025-03-31'\n" +
                "        group by USER_ID1\n" +
                "        having count(USER_ID2) > 20\n" +
                "    )\n" +
                "order by ID;";

        return getUsersFromDB(SQL_SELECT);
    }
}
