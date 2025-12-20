package comcosmocom.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class StarCatalogJson {

    @JsonProperty("type")
    private String type;

    @JsonProperty("features")
    private List<StarData> features;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<StarData> getFeatures() { return features; }
    public void setFeatures(List<StarData> features) { this.features = features; }

    // Метод для получения количества звёзд
    public int getStarCount() {
        return features != null ? features.size() : 0;
    }
}
