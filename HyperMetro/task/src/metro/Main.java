package metro;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static String[] errorMsg = {
            "Error! Such a file doesn't exist!",
            "Incorrect file",
            "Invalid command"
    };

    static List<String> commList = List.of("/add",
            "/append",
            "/add-head",
            "/remove",
            "/output");
    static Pattern pattern = Pattern
            .compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
    static HashMap<String, LinkedList<String>> lineMap = new HashMap<>();

    public static void main(String[] args)  {
        try (var br = Files.newBufferedReader(
                Paths.get(args[0]), StandardCharsets.UTF_8)) {
            loadMap(new Gson().fromJson(br, new TypeToken<HashMap<String,
                            HashMap<Integer, String>>>(){}.getType()
            ));
            List<String> params = input();
            while (!"/exit".equals(params.get(0))) {
                if (!commList.contains(params.get(0))) {
                    printError(2);
                    params = input();
                    continue;
                }
                try {
                    switch (params.get(0)) {
                        case "/add", "/append" -> append(params.get(1),
                                params.get(2));
                        case "/add-head" -> addHead(params.get(1),
                                params.get(2));
                        case "/remove" -> remove(params.get(1),
                                params.get(2));
                        case "/output" -> output(params.get(1));
                    }
                } catch (Exception e) {
                    printError(2);
                }
                params = input();
            }
        } catch (IOException e) {
            printError(0);
        } catch (JsonSyntaxException e) {
            printError(1);
        }
    }
    static void loadMap(HashMap<String, HashMap<Integer, String>> raws) {
        for (var e : raws.keySet()) {
            HashMap<Integer, String> map = raws.get(e);
            LinkedList<String> line = new LinkedList<>();
            for (int i = 1; i <= map.size(); i++) {
                line.add(map.get(i));
            }
            lineMap.put(e, line);
        }
    }
    static void printError(int i) {
        System.out.println(errorMsg[i]);
    }
    private static List<String> input() {
        List<String> matchers = new ArrayList<>();
        var m = pattern.matcher(sc.nextLine());
        while (m.find()) {
            if (m.group(1) != null) {
                matchers.add(m.group(1));
            } else if (m.group(2) != null) {
                matchers.add(m.group(2));
            } else {
                matchers.add(m.group());
            }
        }
        return matchers;
    }

    private static void output(String rfrnc) {
        LinkedList<String> line = lineMap.get(rfrnc);
        String str1 = "depot",
                str2 = line.get(0),
                str3;

        for (int i = 1; i < line.size(); i++) {
            str3 = line.get(i);
            System.out.printf("%s - %s - %s\n",
                    str1,
                    str2,
                    str3);
            str1 = str2;
            str2 = str3;
        }
        System.out.printf("%s - %s - %s\n",
                str1,
                str2,
                "depot");
    }
    private static void append(String rfrnc,
                               String station) {
        lineMap.get(rfrnc).addLast(station);
    }

    private static void addHead(String rfrnc,
                                String station) {
        lineMap.get(rfrnc).addFirst(station);
    }

    private static void remove(String rfrnc,
                               String station) {
        lineMap.get(rfrnc).remove(station);
    }
}
