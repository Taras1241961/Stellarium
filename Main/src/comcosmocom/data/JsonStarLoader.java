package comcosmocom.data;

import comcosmocom.model.Star;
import comcosmocom.model.StarCatalogJson;
import comcosmocom.model.StarData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonStarLoader {
    private List<Star> stars = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();

    // ГЛАВНЫЙ МЕТОД ЗАГРУЗКИ
    public void loadStars() {
        stars.clear();

        System.out.println("🔄 Начинаю загрузку звёзд...");

        // 1. Пробуем найти файл
        File jsonFile = findStarFile();

        if (jsonFile != null && jsonFile.exists()) {
            // 2. Если файл найден — загружаем
            System.out.println("✅ Файл найден: " + jsonFile.getAbsolutePath());
            loadFromFile(jsonFile);
        } else {
            // 3. Если файл не найден — создаём тестовые данные
            System.out.println("⚠️ Файл не найден. Создаю тестовые звёзды...");
            createTestStars();
        }

        System.out.println("✨ Загружено звёзд: " + stars.size());
    }

    // Поиск файла в разных местах
    private File findStarFile() {
        System.out.println("🔍 Ищу файл со звёздами...");

        // Список возможных путей
        String[] possiblePaths = {
                "stars.8.json",                    // в корне проекта
                "stars.8.json",                 // другой вариант названия
                "stars.json",                   // самый простой вариант
                "data/stars.8.json",               // в папке data
                "src/comcosmocom/data/stars.8.json", // в src
                "stellarium/stars.8.json",         // в папке stellarium
                "Main/stars.8.json"                // в папке Main
        };

        // Сначала проверяем существующие файлы
        for (String path : possiblePaths) {
            File file = new File(path);
            System.out.println("  Проверяю: " + path);
            if (file.exists()) {
                return file;
            }
        }

        // Если файлов нет — создаём простой
        return createSimpleStarFile();
    }

    // Создаём простой файл если нет существующего
    private File createSimpleStarFile() {
        try {
            System.out.println("📝 Создаю простой файл со звёздами...");

            String simpleJson = createSimpleJson();
            File file = new File("my_stars.json");

            // Создаём файл
            Files.write(Paths.get(file.getPath()), simpleJson.getBytes());

            System.out.println("✅ Создан файл: " + file.getAbsolutePath());
            return file;

        } catch (Exception e) {
            System.out.println("❌ Не удалось создать файл: " + e.getMessage());
            return null;
        }
    }

    // Загрузка из найденного файла
    private void loadFromFile(File jsonFile) {
        try {
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            StarCatalogJson catalog = mapper.readValue(jsonFile, StarCatalogJson.class);
            System.out.println("📊 Найдено записей в JSON: " + catalog.getStarCount());

            int loaded = 0;
            int skipped = 0;

            for (StarData starData : catalog.getFeatures()) {
                try {
                    StarData.Geometry geom = starData.getGeometry();
                    StarData.Properties props = starData.getProperties();

                    if (geom == null || props == null) {
                        skipped++;
                        continue;
                    }

                    double[] coords = geom.getCoordinates();
                    if (coords == null || coords.length < 2) {
                        skipped++;
                        continue;
                    }

                    double raRad = geom.getRightAscensionRad();
                    double decRad = geom.getDeclinationRad();
                    double magnitude = props.getMagnitude();
                    String name = props.getName();

                    double bvIndex = estimateBvIndex(magnitude, name);

                    Star star = new Star(loaded, name, raRad, decRad, magnitude, bvIndex);
                    stars.add(star);
                    loaded++;

                    // Выводим первые 3 звезды
                    if (loaded <= 3) {
                        System.out.printf("   ★ %s (mag=%.2f)%n",
                                name != null ? name : "Звезда " + loaded,
                                magnitude);
                    }

                } catch (Exception e) {
                    skipped++;
                }
            }

            System.out.printf("📈 Загружено: %d, Пропущено: %d%n", loaded, skipped);

        } catch (Exception e) {
            System.out.println("❌ Ошибка чтения файла: " + e.getMessage());
            System.out.println("   Создаю тестовые данные...");
            createTestStars();
        }
    }

    // Создание простого JSON
    private String createSimpleJson() {
        return """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "id": "1",
                  "geometry": {
                    "type": "Point",
                    "coordinates": [6.7525, -16.716]
                  },
                  "properties": {
                    "mag": 0.45,
                    "name": "Сириус"
                  }
                },
                {
                  "type": "Feature",
                  "id": "2",
                  "geometry": {
                    "type": "Point",
                    "coordinates": [5.9195, 7.4071]
                  },
                  "properties": {
                    "mag": 1.50,
                    "name": "Бетельгейзе"
                  }
                },
                {
                  "type": "Feature",
                  "id": "3",
                  "geometry": {
                    "type": "Point",
                    "coordinates": [19.8464, 8.8922]
                  },
                  "properties": {
                    "mag": 1.25,
                    "name": "Альтаир"
                  }
                },
                {
                  "type": "Feature",
                  "id": "4",
                  "geometry": {
                    "type": "Point",
                    "coordinates": [18.6171, 38.7836]
                  },
                  "properties": {
                    "mag": 2.90,
                    "name": "Вега"
                  }
                },
                {
                  "type": "Feature",
                  "id": "5",
                  "geometry": {
                    "type": "Point",
                    "coordinates": [10.1396, 11.9672]
                  },
                  "properties": {
                    "mag": 3.53,
                    "name": "Процион"
                  }
                }
              ]
            }
            """;
    }

    // Создание тестовых звёзд
    private void createTestStars() {
        System.out.println("🎨 Создаю тестовые звёзды...");

        // Ярчайшие звёзды неба
        Object[][] brightStars = {
                {"Сириус", 6.7525, -16.716, 0.45},
                {"Канопус", 5.2423, -8.2016, 1.37},
                {"Альтаир", 19.8464, 8.8922, 1.25},
                {"Бетельгейзе", 5.9195, 7.4071, 1.50},
                {"Вега", 18.6171, 38.7836, 2.90},
                {"Капелла", 5.2782, 46.0423, 1.90},
                {"Ригель", 5.2423, -8.2016, 1.64},
                {"Процион", 10.1396, 11.9672, 3.53},
                {"Ахернар", 1.6286, -57.2368, 2.45},
                {"Хадар", 14.6608, -60.8352, 2.75}
        };

        for (int i = 0; i < brightStars.length; i++) {
            String name = (String) brightStars[i][0];
            double raHours = (double) brightStars[i][1];
            double decDeg = (double) brightStars[i][2];
            double mag = (double) brightStars[i][3];

            double raRad = raHours * (Math.PI / 12.0);
            double decRad = decDeg * (Math.PI / 180.0);

            Star star = new Star(i, name, raRad, decRad, mag, 0.6);
            stars.add(star);

            if (i < 3) {
                System.out.printf("   ★ %s (mag=%.2f)%n", name, mag);
            }
        }

        System.out.println("✅ Создано " + stars.size() + " тестовых звёзд");
    }

    private double estimateBvIndex(double mag, String name) {
        if (mag < 0.5) return -0.1;
        else if (mag < 2.0) return 0.6;
        else return 1.2;
    }

    public List<Star> getStars() {
        return stars;
    }

    public List<Star> getBrightStars(double maxMagnitude) {
        List<Star> result = new ArrayList<>();
        for (Star star : stars) {
            if (star.getMagnitude() <= maxMagnitude) {
                result.add(star);
            }
        }
        return result;
    }
}
