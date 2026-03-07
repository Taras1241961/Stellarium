package src.comcosmocom.data;

import src.comcosmocom.model.HygStar;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class HygLoader {
    private List<HygStar> stars = new ArrayList<>();
    private Map<Integer, HygStar> hipMap = new HashMap<>();

    public void loadHygData(String filename) throws IOException {
        stars.clear();
        hipMap.clear();

        System.out.println("Загрузка HYG базы: " + filename);

        List<String> lines = Files.readAllLines(Paths.get(filename));
        System.out.println("Всего строк: " + lines.size());

        String header = lines.get(0);
        String[] columns = header.split(",");

        // Находим индексы нужных колонок
        int hipIdx = -1, raIdx = -1, decIdx = -1, magIdx = -1, bvIdx = -1, nameIdx = -1;

        for (int i = 0; i < columns.length; i++) {
            String col = columns[i].trim();
            switch(col) {
                case "hip": hipIdx = i; break;
                case "ra": raIdx = i; break;
                case "dec": decIdx = i; break;
                case "mag": magIdx = i; break;
                case "ci": bvIdx = i; break;
                case "proper": nameIdx = i; break;
            }
        }

        System.out.println("Индексы: hip=" + hipIdx + ", ra=" + raIdx + ", dec=" + decIdx +
                ", mag=" + magIdx + ", bv=" + bvIdx + ", name=" + nameIdx);

        int count = 0;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",");

            if (parts.length <= Math.max(hipIdx, Math.max(raIdx, decIdx))) continue;

            try {
                int hipId = hipIdx >= 0 && parts.length > hipIdx && !parts[hipIdx].isEmpty() ?
                        Integer.parseInt(parts[hipIdx]) : -1;

                // RA в часах (0-24) -> нужно преобразовать в радианы
                double raHours = raIdx >= 0 && parts.length > raIdx ? Double.parseDouble(parts[raIdx]) : 0;
                double raRad = raHours * 15 * Math.PI / 180; // часы -> градусы -> радианы

                // Dec в градусах (-90 до +90) -> радианы
                double decDeg = decIdx >= 0 && parts.length > decIdx ? Double.parseDouble(parts[decIdx]) : 0;
                double decRad = decDeg * Math.PI / 180;

                double mag = magIdx >= 0 && parts.length > magIdx ? Double.parseDouble(parts[magIdx]) : 99;
                double bv = bvIdx >= 0 && parts.length > bvIdx && !parts[bvIdx].isEmpty() ?
                        Double.parseDouble(parts[bvIdx]) : 0.6;
                String name = nameIdx >= 0 && parts.length > nameIdx ? parts[nameIdx] : "";

                // Берём только видимые звёзды (mag < 6.5)
                if (mag < 6.5) {
                    HygStar star = new HygStar(count, hipId, name, raRad, decRad, mag, bv);
                    stars.add(star);
                    if (hipId > 0) {
                        hipMap.put(hipId, star);
                    }
                    count++;
                }

            } catch (Exception e) {
                // Пропускаем ошибки парсинга
            }
        }

        System.out.println("Загружено звёзд: " + stars.size());
        System.out.println("Звёзд с HIP ID: " + hipMap.size());
    }

    public List<HygStar> getStars() { return stars; }
    public Map<Integer, HygStar> getHipMap() { return hipMap; }
}
