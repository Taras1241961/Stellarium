package src.cosmocom.data;

import src.cosmocom.model.HygStar;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class HygLoader {
    private List<HygStar> stars = new ArrayList<>();
    private Map<Integer, HygStar> hipMap = new HashMap<>();

    public void loadHygData(String filename) throws IOException {
        stars.clear();
        hipMap.clear();

        List<String> lines = Files.readAllLines(Paths.get(filename));
        System.out.println("HYG: lines=" + lines.size());

        String header = lines.get(0);
        String[] columns = header.split(",");

        int hipIdx = -1, raIdx = -1, decIdx = -1, magIdx = -1, bvIdx = -1, nameIdx = -1;
        int bayerIdx = -1, flamIdx = -1, conIdx = -1, distIdx = -1, spectIdx = -1;

        for (int i = 0; i < columns.length; i++) {
            String col = columns[i].trim();
            switch (col) {
                case "hip": hipIdx = i; break;
                case "ra": raIdx = i; break;
                case "dec": decIdx = i; break;
                case "mag": magIdx = i; break;
                case "ci": bvIdx = i; break;
                case "proper": nameIdx = i; break;
                case "bayer": bayerIdx = i; break;
                case "flam": flamIdx = i; break;
                case "con": conIdx = i; break;
                case "dist": distIdx = i; break;
                case "spect": spectIdx = i; break;
            }
        }

        int count = 0;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",");

            try {
                int hipId = hipIdx >= 0 && parts.length > hipIdx && !parts[hipIdx].isEmpty() ?
                        Integer.parseInt(parts[hipIdx]) : -1;

                double raHours = raIdx >= 0 && parts.length > raIdx ? Double.parseDouble(parts[raIdx]) : 0;
                double raRad = raHours * 15 * Math.PI / 180;

                double decDeg = decIdx >= 0 && parts.length > decIdx ? Double.parseDouble(parts[decIdx]) : 0;
                double decRad = decDeg * Math.PI / 180;

                double mag = magIdx >= 0 && parts.length > magIdx ? Double.parseDouble(parts[magIdx]) : 99;
                double bv = bvIdx >= 0 && parts.length > bvIdx && !parts[bvIdx].isEmpty() ?
                        Double.parseDouble(parts[bvIdx]) : 0.6;

                double dist = distIdx >= 0 && parts.length > distIdx && !parts[distIdx].isEmpty() ?
                        Double.parseDouble(parts[distIdx]) : -1;

                String spect = spectIdx >= 0 && parts.length > spectIdx ? parts[spectIdx].trim() : "";

                String name = nameIdx >= 0 && parts.length > nameIdx && !parts[nameIdx].isEmpty() ?
                        parts[nameIdx].trim() : "";

                if (name.isEmpty()) {
                    String bayer = bayerIdx >= 0 && parts.length > bayerIdx && !parts[bayerIdx].isEmpty() ?
                            parts[bayerIdx].trim() : "";
                    String flam = flamIdx >= 0 && parts.length > flamIdx && !parts[flamIdx].isEmpty() ?
                            parts[flamIdx].trim() : "";
                    String con = conIdx >= 0 && parts.length > conIdx && !parts[conIdx].isEmpty() ?
                            parts[conIdx].trim() : "";

                    if (!bayer.isEmpty() && !con.isEmpty()) {
                        name = bayer + " " + con;
                    } else if (!flam.isEmpty() && !con.isEmpty()) {
                        name = flam + " " + con;
                    }
                }

                if (name != null && name.trim().equalsIgnoreCase("Sol")) {
                    System.out.println("Пропущена звезда: " + name);
                    continue;
                }

                if (mag < 6.5) {
                    HygStar star = new HygStar(count, hipId, name, raRad, decRad, mag, bv, dist, spect);
                    stars.add(star);
                    if (hipId > 0) hipMap.put(hipId, star);
                    count++;
                }
            } catch (Exception e) {
            }
        }

        System.out.println("Stars loaded: " + stars.size());
    }

    public List<HygStar> getStars() { return stars; }
    public Map<Integer, HygStar> getHipMap() { return hipMap; }
}


