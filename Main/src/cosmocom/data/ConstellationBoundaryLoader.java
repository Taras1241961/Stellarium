package src.cosmocom.data;

import src.cosmocom.model.BoundaryPoint;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConstellationBoundaryLoader {

    public static List<BoundaryPoint> loadBoundaries(String filename) throws IOException {
        List<BoundaryPoint> points = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filename));

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.trim().split("\\s+");
            if (parts.length < 3) continue;

            try {
                double raHours = Double.parseDouble(parts[0]);
                double decDeg = Double.parseDouble(parts[1]);
                String c1 = parts[2];
                String c2 = parts.length > 3 ? parts[3] : null;

                double raRad = raHours * 15 * Math.PI / 180;
                double decRad = decDeg * Math.PI / 180;

                points.add(new BoundaryPoint(raRad, decRad, c1, c2));

            } catch (NumberFormatException e) {}
        }

        System.out.println("Загружено границ созвездий: " + points.size());
        return points;
    }
}