package src.comcosmocom.data;

import src.comcosmocom.model.HygStar;
import java.util.List;

public class CheckHygData {
    public static void main(String[] args) {
        try {
            HygLoader loader = new HygLoader();
            loader.loadHygData("hygdata_v3.csv");

            List<HygStar> stars = loader.getStars();

            System.out.println("\n=== Сириус (HIP 32349) ===");
            for (HygStar star : stars) {
                if (star.getHipId() == 32349) {
                    System.out.printf("RA: %.6f рад (должно быть ~1.767)%n", star.getRa());
                    System.out.printf("Dec: %.6f рад (должно быть ~-0.291)%n", star.getDec());
                    System.out.printf("Mag: %.2f%n", star.getMag());
                    System.out.printf("Имя: %s%n", star.getName());
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

