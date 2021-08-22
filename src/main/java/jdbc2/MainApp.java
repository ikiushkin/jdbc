package jdbc2;

import static jdbc2.Util.copyDatabase;

public class MainApp {

    public static void main(String[] args) {
        String jdbcPropertyFile = "src/main/resources/jdbc-t3.properties";

        copyDatabase(jdbcPropertyFile, "social_network");
    }
}
