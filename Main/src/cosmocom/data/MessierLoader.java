package src.cosmocom.data;

import src.cosmocom.model.MessierObject;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MessierLoader {

    public static List<MessierObject> loadMessier(String filename) {
        List<MessierObject> objects = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            System.out.println("Файл Мессье: прочитано строк " + lines.size());

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                if (line.startsWith("#")) continue;

                String[] parts = line.split(",");

                if (parts.length < 7) {
                    System.out.println("Пропущена строка (мало полей): " + line);
                    continue;
                }

                try {
                    String mNumber = parts[0].trim();
                    int number = Integer.parseInt(mNumber.substring(1));

                    String name = parts[1].trim();
                    String type = parts[2].trim();
                    // RA в ЧАСАХ (0-24), а не в градусах!
                    double raHours = Double.parseDouble(parts[3].trim());
                    double decDeg = Double.parseDouble(parts[4].trim());
                    double mag = Double.parseDouble(parts[5].trim());
                    String constellation = parts[6].trim();

                    MessierObject obj = new MessierObject(number, name, type, raHours, decDeg, mag, constellation);
                    objects.add(obj);
                    System.out.println("Загружен M" + number + ": " + name + " (RA=" + raHours + "ч, Dec=" + decDeg + "°)");

                } catch (NumberFormatException e) {
                    System.out.println("Ошибка парсинга строки: " + line);
                }
            }

        } catch (Exception e) {
            System.out.println("Ошибка загрузки Мессье: " + e.getMessage());
        }

        System.out.println("Загружено Мессье: " + objects.size());
        return objects;
    }
}


