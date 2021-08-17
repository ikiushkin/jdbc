package jdbc.task_three;

import java.util.List;

import static jdbc.task_three.Util.fillingLikesDB;
import static jdbc.task_three.Util.getDBSize;

public class MainApp {
    public static void main(String[] args) {

        // Блок наполнения и генерации случайных имен
        //List<String> femaleNames = FileParser.parseToStringArray("src/main/resources/init/task_three/female_names_rus.txt");
        //List<String> maleNames = FileParser.parseToStringArray("src/main/resources/init/task_three/male_names_rus.txt");
        //List<String> maleSurnames = FileParser.parseToStringArray("src/main/resources/init/task_three/male_surnames_rus.txt");
        //List<String> femaleSurnames = FileParser.parseToStringArray("src/main/resources/init/task_three/female_surnames_rus.txt");

        //List<String> males = FileParser.nameGenerator(maleNames, maleSurnames);
        //List<String> females = FileParser.nameGenerator(femaleNames, femaleSurnames);

        // Наполняем базу users
        //fillingUsersDB(males);
        //fillingUsersDB(females);
        //getAllUsersDB().forEach(System.out::println);

        // Наполняем базу friendships
        //Util.fillingFriendshipsDB(getAllUsersDB("users"));

        // Наполняем базу posts (предварительно парсим комменты из Онегина :) )
        List<String> onegin = FileParser.oneginParser("src/main/resources/init/task_three/yevgeniy-onegin.txt");
        //fillingPostsDB(getAllUsersDB("users"), onegin);

        // Наполняем базу likes
        fillingLikesDB(getDBSize("posts"), getDBSize("users"));

        //System.out.println(getUsersWithFriendsAndLikes());

    }
}
