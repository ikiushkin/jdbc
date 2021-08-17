package jdbc.task_three;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileParser {
    public static List<String> parseToStringArray(String filePath) {
        List<String> result = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while (br.ready()) {
                String line = br.readLine().trim().replaceAll("[;.]", "");
                List<String> splitLine = Arrays.asList(line.split("\\s"));
                if (splitLine.size() > 0) {
                    result.addAll(splitLine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> nameGenerator(List<String> names, List<String> surnames) {
        Set<String> result = new HashSet<>();
        for (int i = 0; i < names.size(); i++) {
            String sb = names.get((int) (Math.random() * names.size())) +
                    " " +
                    surnames.get((int) (Math.random() * surnames.size()));
            result.add(sb);
        }
        return new ArrayList<>(result);
    }

    public static List<String> oneginParser(String filePath) {
        List<String> result = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "windows-1251"))) {
            while (br.ready()) {
                String line = br.readLine().trim().replaceAll("[;.*-]", "");
                if (line.length() > 15 && line.length() < 100) {
                    if (line.endsWith(",")) {
                        line = line.substring(0, line.length()-1);
                    }
                    byte[] bytes = line.getBytes();
                    result.add(new String(bytes, StandardCharsets.UTF_8));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}