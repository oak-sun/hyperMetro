import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testing.TestedProgram;

import java.util.Arrays;

public class Tests extends StageTest<String> {
    // test from the previous stage with one line
    @DynamicTest(order = 1)
    CheckResult oneLineTest() {

        TestedProgram main = new TestedProgram();
        main.start("./test/baltimore.json");
        String output = main.execute("/output \"SubwayLink\"").trim();

        String[] stations = new String[]{"Owings Mills", "Old Court", "Milford Mill", "Reiserstown Plaza",
            "Rogers Avenue", "West Cold Spring", "Mondawmin", "Penn North", "Uptown", "State Center",
            "Lexington Market", "Charles Center", "Shot Tower/Market Place", "Johns Hopkins Hospital"};

        checkDepots(output);
        checkOutputLength(output, stations.length + 2);
        assertStations(output, stations);

        return CheckResult.correct();
    }

    // test of example
    @DynamicTest(order = 2)
    CheckResult multipleLineOutputTest() {

        TestedProgram main = new TestedProgram();
        main.start("./test/lausanne.json");
        String output = main.execute("/output \"m1\"").trim();

        String[] stations = new String[]{"Renes—Gare", "Epenex", "Crochy", "Cerisaie",
            "Bassenges", "EPFL", "UNL—Sorge", "Mouline", "UNL—Chemberonne", "Bourdonnette", "Melley",
            "Provence", "Montelly", "Vigie", "Lausanne—Flon"};

        checkDepots(output);
        checkOutputLength(output, stations.length + 2);
        assertStations(output, stations);

        output = main.execute("/output \"m2\"");

        stations = new String[]{"Croisettes", "Vennes", "Fourmi", "Sallaz", "CHUV", "Ours",
            "Riponne M.Bejart", "Bessieres", "Lausanne—Flon", "Lausanne Gare CFF", "Grancy", "Delices", "Jourdils",
            "Ouchy—Olympique"};

        checkDepots(output);
        checkOutputLength(output, stations.length + 2);
        assertStations(output, stations);

        return CheckResult.correct();
    }

    // example test pt.2 (with addition)
    @DynamicTest(order = 3)
    CheckResult additionTest() {

        TestedProgram main = new TestedProgram();
        main.start("./test/lausanne.json");

        // added a station to the end of the line
        main.execute("/append \"m1\" \"Test station 1\"");
        String output = main.execute("/output \"m1\"");

        String[] stations = new String[]{"Renes—Gare", "Epenex", "Crochy", "Cerisaie",
            "Bassenges", "EPFL", "UNL—Sorge", "Mouline", "UNL—Chemberonne", "Bourdonnette", "Melley",
            "Provence", "Montelly", "Vigie", "Lausanne—Flon", "Test station 1"};

        checkDepots(output);
        checkOutputLength(output, stations.length + 2);
        assertStations(output, stations);

        // added another one
        main.execute("/append \"m1\" \"Test station 2\"");
        output = main.execute("/output \"m1\"");

        stations = new String[]{"Renes—Gare", "Epenex", "Crochy", "Cerisaie",
            "Bassenges", "EPFL", "UNL—Sorge", "Mouline", "UNL—Chemberonne", "Bourdonnette", "Melley",
            "Provence", "Montelly", "Vigie", "Lausanne—Flon", "Test station 1", "Test station 2"};

        checkDepots(output);
        checkOutputLength(output, stations.length + 2);
        assertStations(output, stations);

        // added one station to the beginning of the line
        main.execute("/add-head \"m1\" \"Head\"");
        output = main.execute("/output \"m1\"");

        stations = new String[]{"Head", "Renes—Gare", "Epenex", "Crochy", "Cerisaie",
            "Bassenges", "EPFL", "UNL—Sorge", "Mouline", "UNL—Chemberonne", "Bourdonnette", "Melley",
            "Provence", "Montelly", "Vigie", "Lausanne—Flon", "Test station 1", "Test station 2"};

        checkDepots(output);
        checkOutputLength(output, stations.length + 2);
        assertStations(output, stations);

        return CheckResult.correct();
    }

    // not existing file check
    @DynamicTest(order = 4)
    CheckResult nonexistingFileTest() {
        TestedProgram main = new TestedProgram();
        String output = main.start("tHiS_fIlE_DoEs_nOt_ExIsT.txt");
        if (output.trim().length() == 0) {
            return CheckResult.wrong("The program did not print anything when the file was not exist. ");
        }
        if (output.toLowerCase().startsWith("depot") || output.toLowerCase().endsWith("depot")) {
            return CheckResult.wrong("It looks like the program did not print an error message when the file was not exist.");
        }
        return CheckResult.correct();
    }

    // test of a case from the example
    @DynamicTest(order = 5)
    CheckResult outputLineWithTransfer() {
        TestedProgram main = new TestedProgram();
        main.start("./test/prague.json");

        String[][] stations = new String[][]{
            {"Nemocnice Motol", null},
            {"Petriny", null},
            {"Nadrazi Veleslavin", null},
            {"Borislavka", null},
            {"Dejvicka", null},
            {"Hradcanska", null},
            {"Malostranska", null},
            {"Staromestska", null},
            {"Mustek", "Linka B"},
            {"Muzeum", "Linka C"},
            {"Namesti Miru", null},
            {"Jiriho z Podebrad", null},
            {"Flora", null},
            {"Zelivskeho", null},
            {"Strasnicka", null},
            {"Skalka", null},
            {"Depo Hostivar", null}
        };

        String output = main.execute("/output \"Linka A\"");
        checkDepots(output);

        checkOutputLength(output, stations.length + 2);
        assertWithTransfer(output, stations);

        return CheckResult.correct();
    }

    @DynamicTest(order = 6)
    CheckResult connectLinesTest() {
        TestedProgram main = new TestedProgram();
        main.start("./test/prague.json");

        String[][] stations = new String[][]{
            {"Nemocnice Motol", null},
            {"Petriny", "Linka C"},
            {"Nadrazi Veleslavin", null},
            {"Borislavka", null},
            {"Dejvicka", null},
            {"Hradcanska", null},
            {"Malostranska", null},
            {"Staromestska", null},
            {"Mustek", "Linka B"},
            {"Muzeum", "Linka C"},
            {"Namesti Miru", null},
            {"Jiriho z Podebrad", null},
            {"Flora", null},
            {"Zelivskeho", null},
            {"Strasnicka", null},
            {"Skalka", null},
            {"Depo Hostivar", null}
        };

        main.execute("/connect \"Linka C\" \"I.P.Pavlova\" \"Linka A\" \"Petriny\"");
        String output = main.execute("/output \"Linka A\"");

        checkDepots(output);
        checkOutputLength(output, stations.length + 2);
        assertWithTransfer(output, stations);

        return CheckResult.correct();
    }

    @DynamicTest(order = 7)
    CheckResult simpleRouteTest() {
        TestedProgram main = new TestedProgram();
        main.start("./test/prague.json");

        String[] correctRoute = {"Petriny", "Nadrazi Veleslavin", "Borislavka", "Dejvicka", "Hradcanska", "Malostranska",
            "Staromestska", "Mustek", "Muzeum", "Namesti Miru", "Jiriho z Podebrad", "Flora"};

        String[] sOutput = main.execute("/route \"Linka A\" \"Petriny\" \"Linka A\" \"Flora\"").toLowerCase().split("\n");

        if (sOutput.length != correctRoute.length) {
            return CheckResult.wrong("Your program printed an incorrect number of stations in the route.\n" +
                "Expected: " + correctRoute.length + " stations." +
                "\nYour output: " + sOutput.length + " stations.");
        }

        for (int i = 0; i < correctRoute.length; i++) {
            if (i == 0 && !correctRoute[i].toLowerCase().equals(sOutput[i].toLowerCase())) {
                return CheckResult.wrong("The first station in the output should be '" + correctRoute[i] + "'.");
            } else if (i != 0) {
                if (!correctRoute[i].toLowerCase().equals(sOutput[i].toLowerCase())) {
                    return CheckResult.wrong("After '" + correctRoute[i - 1] + "' should be '" + correctRoute[i] + "'.");
                }
            }
        }

        return CheckResult.correct();
    }

    @DynamicTest(order = 8)
    CheckResult advancedRouteTest() {

        TestedProgram main = new TestedProgram();
        main.start("./test/prague.json");

        String[] stations = {"Vysehrad", "I.P.Pavlova", "Muzeum",
            "Transition to line Linka A", "Muzeum", "Mustek",
            "Transition to line Linka B", "Mustek", "Namesti Republiky"};
        String[] sOutput = main.execute("/route \"Linka C\" \"Vysehrad\" \"Linka B\" \"Namesti Republiky\"").split("\n");

        if (sOutput.length != stations.length) {
            return CheckResult.wrong("Your program printed an incorrect number of stations in the route.\n" +
                "Expected: " + (stations.length - 4) + " stations and 2 transitions. Output should be " + stations.length + " lines." +
                "\nYour output: " + sOutput.length + " lines");
        }

        for (int i = 0; i < stations.length; i++) {
            if (i == 0 && !stations[i].toLowerCase().equals(sOutput[i].toLowerCase())) {
                return CheckResult.wrong("The first station in the output should be " + stations[i]);
            } else if (i != 0) {
                if (!stations[i].toLowerCase().equals(sOutput[i].toLowerCase())) {
                    return CheckResult.wrong("After '" + stations[i - 1] + "' should be '" + stations[i] + "'.");
                }
            }
        }
        return CheckResult.correct();
    }

    // checks for "depot" at the start and at the end
    void checkDepots(String output) {
        output = output.trim().toLowerCase();
        if (!output.startsWith("depot")) {
            throw new WrongAnswer("Your output should start with 'depot'.");
        } else if (!output.endsWith("depot")) {
            throw new WrongAnswer("Your output should end with 'depot'.");
        }
    }

    // checks number of stations in output
    void checkOutputLength(String output, int correctLength) {
        int length = output.trim().split("\n").length;
        if (length != correctLength) {
            throw new WrongAnswer("You output contains wrong number of lines.\n" +
                "Expected: " + correctLength + " lines\n" +
                "Your output: " + length + " lines");
        }
    }

    // checks stations
    void assertStations(String output, String[] stations) {

        String[] sOutput = output.trim().split("\n");

        for (int i = 0; i < stations.length; i++) {
            if (!sOutput[i + 1].equals(stations[i])) {
                throw new WrongAnswer("Can't find station '" + stations[i] + "' in the line number " + (i + 2));
            }
        }
    }

    void assertWithTransfer(String output, String[][] stations) {

        String[] sOutput = output.split("\n");

        for (int i = 0; i < stations.length; i++) {
            String currentLine = sOutput[i + 1].toLowerCase().trim();
            String currentStation = stations[i][0];
            String currentTransfer = stations[i][1];
            if (currentTransfer == null) {
                if (!currentLine.equals(currentStation.toLowerCase())) {
                    throw new WrongAnswer("There is an error in your program's reply. Some stations were not found.");
                }
            } else if (!currentLine.contains(currentStation.toLowerCase()) ||
                !currentLine.contains(currentTransfer.toLowerCase())) {
                throw new WrongAnswer("Expected transfer to '" + currentTransfer + "' from '" + currentStation + "' station.");
            }
        }
    }
}