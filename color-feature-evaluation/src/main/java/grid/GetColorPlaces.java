package grid;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GetColorPlaces {

  public static final int grid = 4; // set grid
  public static final int colField = 4; // set colored fields
  public static final int count = 25; // number of grid
  public static final String pathDirectory = "color-feature-evaluation/color-feature-eval";


  public static void main(String[] args) throws IOException {

    List<String> col = importColors();

    List<Grid> grids = new ArrayList<>();

    Random random;
    random = new Random();

    for (int i = 0; i < count; i++) {
      List<Integer> randomIndexes = new ArrayList<>();
      Grid g = new Grid();
      for (int j = 0; j < colField; j++) {
        // get field number
        int f = random.nextInt((int) Math.pow(grid, 2));
        while (randomIndexes.contains(f)) { // ensure 100 different segments
          f = random.nextInt((int) Math.pow(grid, 2));
        }
        randomIndexes.add(f);
        // get color
        int cInt = random.nextInt(col.size() - 1);
        String c = col.get(cInt);

        Field field = new Field(f, c);
        g.append(field);
      }
      grids.add(g);
    }

    if (!Files.exists(Paths.get(pathDirectory))) {

      Files.createDirectories(Paths.get(pathDirectory));
    }

    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(pathDirectory + "/Grid" + grid + "_" + colField + ".json"),
          grids);

    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("es");
  }


  public static List<String> importColors() {
    ObjectMapper mapper = new ObjectMapper();
    List<String> col = new ArrayList<>();
    try {
      col = mapper.readValue(new File("color-feature-evaluation/src/main/java/deltaE/colors.json"),
          ArrayList.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return col;
  }
}
