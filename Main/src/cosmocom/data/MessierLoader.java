package src.cosmocom.data;

import src.cosmocom.model.MessierObject;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MessierLoader {
    public static List<MessierObject> loadMessier(String filename) throws IOException {
        List<MessierObject> objects = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.trim().split(",");

            try {
                int number = Integer.parseInt(parts[0].substring(1));
                String name = parts[1].trim();
                String type = parts[2].trim();
                double raHours = Double.parseDouble(parts[3].trim());
                double decDeg = Double.parseDouble(parts[4].trim());
                double ra = raHours * 15 * Math.PI / 180;
                double dec = decDeg * Math.PI / 180;
                double magnitude = Double.parseDouble(parts[5].trim());
                String constellation = parts[6].trim();
                double distanceLy = parts.length > 7 ? Double.parseDouble(parts[7].trim()) : 0;
                double sizeArcmin = parts.length > 8 ? Double.parseDouble(parts[8].trim()) : 0;

                MessierObject obj = new MessierObject(number, name, type, ra, dec, magnitude, constellation, distanceLy, sizeArcmin);
                objects.add(obj);
            } catch (Exception e) {
                System.out.println("Error: " + line);
            }
        }
        System.out.println("Messier loaded: " + objects.size());
        return objects;
    }
}



