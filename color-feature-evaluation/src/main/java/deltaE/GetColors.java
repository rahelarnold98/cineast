package deltaE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.vitrivr.cineast.core.color.ColorConverter;
import org.vitrivr.cineast.core.color.LabContainer;

public class GetColors {

  public static void main(String[] args) {
    List<String> col = new ArrayList<>();
    List<Color> colRgb = new ArrayList<>();
    List<LabContainer> colLab = new ArrayList<>();
    List<DeltaE> color = new ArrayList<>();

    col = importColors();
    for (String c : col) {
      colRgb.add(hexRgbConv(c));
    }

    for (Color c : colRgb) {
      colLab.add(ColorConverter.RGBtoLab(c.getRed(), c.getGreen(), c.getBlue()));
    }

    for (LabContainer l1 : colLab) {
      for (LabContainer l2 : colLab) {
        if (!l1.equals(l2)) {
          color.add(new DeltaE(l1, l2));
        }
      }
    }

    for (DeltaE d : color) {
      d.print();
    }

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

  public static Color hexRgbConv(String hex) {
    return new Color(
        Integer.valueOf(hex.substring(1, 3), 16),
        Integer.valueOf(hex.substring(3, 5), 16),
        Integer.valueOf(hex.substring(5, 7), 16));
  }

}
