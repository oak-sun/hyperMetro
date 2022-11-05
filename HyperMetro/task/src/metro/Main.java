package metro;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        LinkedList<String> stations = new LinkedList<>();

        try (var sc = new Scanner(new File(args[0]))) {
            while (sc.hasNext()) {
                var input = sc.nextLine();
                stations.add(input);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: No file found: " +
                    args[0]);
        }

        if (stations.size() == 0) {
            return;
        }
        stations.addFirst("depot");
        stations.addLast("depot");

        for (int i = 2; i < stations.size(); i++) {
            System.out.printf("%s - %s - %s\n",
                    stations.get(i - 2),
                    stations.get(i - 1),
                    stations.get(i));
        }
    }
}
