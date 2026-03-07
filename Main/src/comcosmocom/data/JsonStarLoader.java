package src.comcosmocom.data;

import src.comcosmocom.model.Star;
import src.comcosmocom.model.StarCatalogJson;
import src.comcosmocom.model.StarData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

public class JsonStarLoader {
    private List<Star> stars = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper();

    public void loadJsonStars(String resourcePath) throws IOException {
        stars.clear();

        System.out.println("Загрузка файла: " + resourcePath);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new FileNotFoundException("Файл не найден: " + resourcePath);
        }

        StarCatalogJson catalog = mapper.readValue(inputStream, StarCatalogJson.class);

        List<StarData> features = catalog.getFeatures();

        if (features == null) {
            System.out.println("Внимание: features = null");
            inputStream.close();
            return;
        }

        System.out.println("Найдено объектов в JSON: " + features.size());

        int count = 0;
        for (StarData starFeature : features) {
            try {
                StarData.Properties props = starFeature.getProperties();
                StarData.Geometry geom = starFeature.getGeometry();

                if (geom == null || props == null) {
                    continue;
                }

                double raRad = geom.getRightAscensionRad();
                double decRad = geom.getDeclinationRad();
                double mag = props.getMagnitude();
                String name = props.getName();

                Star star = new Star(count, name, raRad, decRad, mag, 0.6);
                stars.add(star);
                count++;

            } catch (Exception e) {
                System.out.println("Ошибка при обработке звезды: " + e.getMessage());
            }
        }

        System.out.println("Успешно загружено звёзд: " + stars.size());
        inputStream.close();
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
