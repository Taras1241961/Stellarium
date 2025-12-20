package comcosmocom.data;

import comcosmocom.model.Star;
import comcosmocom.model.StarCatalogJson;
import comcosmocom.model.StarData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

public class JsonStarLoader {
    private List<Star> stars = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();

    // Метод для загрузки JSON файла
    public void loadJsonStars(String filename) throws IOException {
        stars.clear();

        System.out.println("Загрузка файла: " + filename);

        // Читаем JSON файл
        File jsonFile = new File(filename);
        StarCatalogJson catalog = mapper.readValue(jsonFile, StarCatalogJson.class);

        System.out.println("Найдено объектов: " + catalog.getStarCount());

        // Преобразуем JSON данные в наши Star объекты
        int count = 0;
        for (StarData starData : catalog.getFeatures()) {
            try {
                StarData.Geometry geom = starData.getGeometry();
                StarData.Properties props = starData.getProperties();

                if (geom == null || props == null) continue;

                // Получаем координаты
                double raRad = geom.getRightAscensionRad();
                double decRad = geom.getDeclinationRad();
                double magnitude = props.getMagnitude();
                String name = props.getName();

                // Определяем B-V индекс по цвету (примерная оценка)
                // В реальном каталоге может не быть этого поля
                double bvIndex = estimateBvIndex(magnitude, name);

                // Создаём объект Star
                Star star = new Star(count, raRad, decRad, magnitude, bvIndex);
                if (name != null) {
                    // Здесь можно сохранить имя, если добавить поле в Star
                }

                stars.add(star);
                count++;

                // Для отладки: выводим первые 5 звёзд
                if (count <= 5) {
                    System.out.printf("Звезда %d: RA=%.4f ч, Dec=%.4f°, mag=%.2f%n",
                            count,
                            geom.getCoordinates()[0],
                            geom.getCoordinates()[1],
                            magnitude);
                }

            } catch (Exception e) {
                System.err.println("Ошибка обработки звезды: " + e.getMessage());
            }
        }

        System.out.println("Успешно загружено звёзд: " + stars.size());
    }

    // Примерная оценка B-V индекса по звёздной величине
    private double estimateBvIndex(double mag, String name) {
        // Это упрощённая логика, в реальности нужны данные из каталога

        // Очень яркие звёзды часто белые/голубые
        if (mag < 0.5) return -0.1;

            // Средней яркости — жёлтые
        else if (mag < 2.0) return 0.6;

            // Слабые звёзды — красные
        else return 1.2;
    }

    public List<Star> getStars() {
        return stars;
    }

    // Метод для фильтрации по яркости
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