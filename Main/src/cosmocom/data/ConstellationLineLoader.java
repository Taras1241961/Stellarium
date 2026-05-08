package src.cosmocom.data;

import src.cosmocom.model.HygStar;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConstellationLineLoader {

    public static List<int[]> loadLines(String filename, Map<Integer, HygStar> hipMap) throws IOException {
        List<int[]> lines = new ArrayList<>();

        List<String> fileLines = null;
        String[] possiblePaths = {
                filename,
                "data/" + filename,
                "src/resources/data/" + filename
        };

        for (String path : possiblePaths) {
            try {
                fileLines = Files.readAllLines(Paths.get(path));
                System.out.println("Найден файл линий: " + path);
                break;
            } catch (IOException e) {
            }
        }

        if (fileLines == null) {
            System.out.println("Файл линий не найден: " + filename);
            return lines;
        }

        for (String line : fileLines) {
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.trim().split("\\s+");
            if (parts.length < 3) continue;

            List<Integer> ids = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                try {
                    ids.add(Integer.parseInt(parts[i]));
                } catch (NumberFormatException e) {}
            }

            for (int i = 0; i < ids.size() - 1; i++) {
                HygStar s1 = hipMap.get(ids.get(i));
                HygStar s2 = hipMap.get(ids.get(i + 1));
                if (s1 != null && s2 != null) {
                    lines.add(new int[]{s1.getId(), s2.getId()});
                }
            }
        }

        System.out.println("Загружено линий созвездий: " + lines.size());
        return lines;
    }
}