package jdbc2;

import static jdbc2.Util.copyDatabaseReverse;

public class MainApp {

    public static void main(String[] args) {
        String jdbcPropertyFile = "src/main/resources/jdbc-t3.properties";

        copyDatabaseReverse(jdbcPropertyFile, "social_network", true);
    }
}
