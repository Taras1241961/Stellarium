package src.comcosmocom.data;

import src.comcosmocom.model.Star;
import src.comcosmocom.model.StarCatalogJson;
import src.comcosmocom.model.StarData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;

public class JsonStarLoader {
    private List<Star> stars = new ArrayList<>();
    private Map<Integer, Star> hipToStarMap = new HashMap<>(); // Карта Hipparcos ID -> Star
    private ObjectMapper mapper = new ObjectMapper();

    public void loadJsonStars(String filename) throws IOException {
        stars.clear();
        hipToStarMap.clear();

        File jsonFile = new File(filename);
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("Файл звёзд не найден: " + filename);
        }

        StarCatalogJson catalog = mapper.readValue(jsonFile, StarCatalogJson.class);
        int count = 0;

        for (StarData starData : catalog.getFeatures()) {
            StarData.Properties props = starData.getProperties();
            StarData.Geometry geom = starData.getGeometry();

            if (geom == null || props == null) continue;

            double raRad = geom.getRightAscensionRad();
            double decRad = geom.getDeclinationRad();
            double mag = props.getMagnitude();

            // Используем индекс как временный HIP ID, т.к. в stars.8.json нет HIP
            int hipId = count;
            Star star = new Star(hipId, null, raRad, decRad, mag, 0.6);
            stars.add(star);
            hipToStarMap.put(hipId, star);
            count++;
        }
        System.out.println("Загружено звёзд из JSON: " + stars.size());
    }

    public Map<Integer, Star> getHipToStarMap() {
        return hipToStarMap;
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
