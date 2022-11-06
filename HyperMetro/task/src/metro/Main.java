package metro;

import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        loadMap(args[0]).ifPresent(Main::parse);
    }

    private static Optional<LinkedHashMap<String, SubwayController>>
                                             loadMap(String path) {
        var file = Paths.get(path);
        if (Files.notExists(file)) {
            System.out.println(
                    "Error! Such a file doesn't exist!");
            return Optional.empty();
        } else {
            try {
                var br =
                        Files.newBufferedReader(file);
                var jsO =
                        JsonParser.parseReader(br).getAsJsonObject();
                LinkedHashMap<String, SubwayController> lineMap =
                        new LinkedHashMap<>();
                ArrayList<String[]> transfersList =
                        new ArrayList<>();
                for (var obj: jsO.entrySet()) {
                    var line =
                            obj.getKey().replace("\"", "");
                    var controller = new SubwayController(line);
                    for (var e : obj
                            .getValue()
                            .getAsJsonObject()
                            .entrySet()) {
                        var station = e
                                .getValue()
                                .getAsJsonObject()
                                .getAsJsonPrimitive("name")
                                .getAsString()
                                .replace("\"", "");
                        controller.append(station);
                        var transfer = e
                                .getValue()
                                .getAsJsonObject()
                                .getAsJsonArray("transfer");
                        if (transfer.size() != 0) {
                            var transferLine = transfer
                                    .get(0)
                                    .getAsJsonObject()
                                    .get("line")
                                    .getAsString()
                                    .replace("\"", "");
                            var transferSt = transfer
                                    .get(0)
                                    .getAsJsonObject()
                                    .get("station")
                                    .getAsString()
                                    .replace("\"", "");
                            transfersList.add(new String[]{
                                    line,
                                    station,
                                    transferLine,
                                    transferSt});
                        }
                    }
                    lineMap.put(line, controller);
                }
                for (String[] data : transfersList) {
                    lineMap.get(data[0]).addTransfer(
                            data[1],
                            lineMap.get(data[2]),
                            data[3]);
                }
                return Optional.of(lineMap);
            } catch (IOException e) {
                System.out.println("Incorrect file.");
                return Optional.empty();
            }
        }
    }

    private static void parse(LinkedHashMap<String, SubwayController> lineMap) {
        var sc = new Scanner(System.in);
        while (true) {
            var input = sc.nextLine();
            if ("/exit".equals(input)) {
                break;
            } else {
                List<String> parseList = new ArrayList<>();
                var m = Pattern
                        .compile("([^\"]\\S*|\".+?\")\\s*")
                        .matcher(input);
                while (m.find())
                    parseList
                            .add(m.group(1)
                            .replace("\"", ""));
                var commands =  parseList.toArray(String[]::new);
                if (commands.length == 2 &&
                        "/output".equals(commands[0])) {
                    if (lineMap.containsKey(commands[1])) {
                        lineMap.get(commands[1]).print();
                    } else {
                        System.out.println("Invalid command.");
                    }
                } else if (commands.length == 3 &&
                        lineMap.containsKey(commands[1])) {
                    var line = lineMap.get(commands[1]);
                    switch (commands[0]) {
                        case "/append" ->
                                line.append(commands[2]);
                        case "/add-head" ->
                                line.addHead(commands[2]);
                        case "/remove" ->
                        {
                            if (!line.remove(commands[2])) {
                                System.out.println(
                                        "Invalid command.");
                            }
                        }
                        default -> System.out.println(
                                "Invalid command.");
                    }
                } else if (commands.length == 5
                        && "/connect".equals(commands[0])
                        && lineMap.containsKey(commands[1])
                        && lineMap.containsKey(commands[3])) {
                    var contr1 = lineMap.get(commands[1]);
                    var contr2 = lineMap.get(commands[3]);
                    contr1.addTransfer(
                            commands[2],
                            contr2,
                            commands[4]);
                    contr2.addTransfer(
                            commands[4],
                            contr1,
                            commands[2]);

                } else {
                    System.out.println("Invalid command.");
                }
            }
        }
    }
}
