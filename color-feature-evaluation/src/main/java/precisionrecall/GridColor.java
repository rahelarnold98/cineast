package precisionrecall;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import deltaE.DeltaE;
import deltaE.GetColors;
import evaluationQueries.ColorFeatureEvaluation;
import grid.Field;
import grid.Grid;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class GridColor {

  public static String path_results;

  private static int numberOfRelevantSegments;

  private static float x;

  private static ArrayList<PrecisionRecall.Entry> dictQuery = new ArrayList<>();
  private static ArrayList<Grid> dictGrid = new ArrayList<>();


  public static void main(String[] args) throws IOException {

    ColorFeatureEvaluation colorFeatureEvaluation = new ColorFeatureEvaluation();
    ObjectMapper mapper = new ObjectMapper();
    try {
      colorFeatureEvaluation = mapper.readValue(new File("../ColorFeatureEvaluation.json"),
          ColorFeatureEvaluation.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    path_results = "../json-queries-grids";
    String path_dict = "../" + colorFeatureEvaluation.getPathDictionary() + "/dict_grid.json";
    String path_grid = "../color-feature-evaluation/color-feature-eval/GridComplete.json";

    try {
      dictQuery = mapper.readValue(new File(path_dict),
          new TypeReference<ArrayList<PrecisionRecall.Entry>>() {
          });
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      dictGrid = mapper.readValue(new File(path_grid),
          new TypeReference<ArrayList<Grid>>() {
          });
    } catch (IOException e) {
      e.printStackTrace();
    }

    int correct = 0;

    for (PrecisionRecall.Entry e : dictQuery) {
      Grid g = dictGrid.get(e.number);
      // Entry has String name and int number
      System.out.println(e.name);
      //BufferedImage gridSketch = ImageIO.read(new File("grid/png/" + e.name));
      // set e.name to id
      int id = Integer.parseInt(e.name.substring(5, e.name.length() - 4));
      int gridSize = getGridSize(id);
      System.out.println(gridSize);
      QueryInfo queryInfo = new QueryInfo();
      int idName = id - 1;
      try {
        queryInfo = mapper.readValue(new File("../json-queries-grids/query-" + idName + ".json"),
            QueryInfo.class);
      } catch (IOException exception) {
        exception.printStackTrace();
      }
      correct += getTopSegments(g, gridSize, queryInfo);
    }

    System.out.println(correct);
  }

  /*private static void getTopSegments(Grid grid, int size, QueryInfo queryInfo)
      throws IOException {
    for (Field f : grid.getGrid()) {
      String colorString = f.getColor();
      int i = f.getIndex();
      Color col = GetColors.hexRgbConv(colorString);
      int containing = 0;
      for (Segment segment : queryInfo.getSegments()) {
        int until = segment.segment_id.indexOf("_", segment.segment_id.indexOf("_") + 1);
        String video = segment.segment_id.substring(0, until);
        System.out.println(video);
        String file = "/tank/" + video + "/" + segment.segment_id + ".png";
        BufferedImage thumb = ImageIO.read(new File(file));
        int h = thumb.getHeight();
        int w = thumb.getWidth();
        int h_p = h / size;
        int w_p = w / size;
        int startX = w_p * i;
        int startY = h_p * i;
        int endX = w_p * (i + 1);
        int endY = h_p * (i + 1);
        boolean contain = checkField(startX, startY, endX, endY, col, thumb);
        if (contain) {
          containing++;
        }
      }
    }
  }*/

  private static int getTopSegments(Grid grid, int size, QueryInfo queryInfo)
      throws IOException {
    int correctCliassified = 0;
    for (Segment segment : queryInfo.getSegments()) {
      int until = segment.segment_id.indexOf("_", segment.segment_id.indexOf("_") + 1);
      String video = segment.segment_id.substring(0, until);
      System.out.println(video);
      String file = "/tank/thumbnails/" + video + "/" + segment.segment_id + ".png";
      BufferedImage thumb;
      try {
        thumb = ImageIO.read(new File(file));
      } catch (IOException ioException){
        continue;
      }

      int h = thumb.getHeight();
      int w = thumb.getWidth();
      int h_p = h / size;
      int w_p = w / size;

      int containing = 0;
      for (Field f : grid.getGrid()) {
        String colorString = f.getColor();
        int i = f.getIndex();
        Color col = GetColors.hexRgbConv(colorString);

        int xCoord = xCoord(i, size);
        int yCoord = yCoord(i, size);

        int startX = w_p * xCoord;
        int startY = h_p * yCoord;
        int endX = w_p * (xCoord + 1);
        int endY = h_p * (yCoord + 1);

        boolean contain = checkField(startX, startY, endX, endY, col, thumb);

        if (contain) {
          containing++;
        }
      }
      if (containing == grid.getGrid().size()) {
        correctCliassified++;
      }
    }
    return correctCliassified;
  }

  private static boolean checkField(int startX, int startY, int endX, int endY, Color org,
      BufferedImage b) {
    int countColor = 0;
    for (int i = startX; i < endX; i++) {
      for (int j = startY; j < endY; j++) {
        Color img = new Color(b.getRGB(i, j));
        countColor = countColor + checkColorInBounds(org, img);
      }
    }
    return countColor > 9;
  }

  private static int checkColorInBounds(Color grid, Color thumbnail) {
    double deltaE = DeltaE.calculateDeltaE(grid, thumbnail);
    if (deltaE < 3) {
      return 1;
    }
    return 0;
  }

  private static int getGridSize(int grid) {
    if (0 <= grid && grid <= 49) {
      return 3;
    }
    return 4;
  }

  private static int xCoord(int field, int grid) {
    if (grid == 3) {
      switch (field) {
        case 0:
        case 3:
        case 6:
          return 0;
        case 1:
        case 4:
        case 7:
          return 1;
        case 2:
        case 5:
        case 8:
          return 2;
      }
    } else if (grid == 4) {
      switch (field) {
        case 0:
        case 4:
        case 8:
        case 12:
          return 0;
        case 1:
        case 5:
        case 9:
        case 13:
          return 1;
        case 2:
        case 6:
        case 10:
        case 14:
          return 2;
        case 3:
        case 7:
        case 11:
        case 15:
          return 3;
      }
    }
    return 0;
  }

  private static int yCoord(int field, int grid) {
    if (grid == 3) {
      switch (field) {
        case 0:
        case 1:
        case 2:
          return 0;
        case 3:
        case 4:
        case 5:
          return 1;
        case 6:
        case 7:
        case 8:
          return 2;
      }
    } else if (grid == 4) {
      switch (field) {
        case 0:
        case 1:
        case 2:
        case 3:
          return 0;
        case 4:
        case 5:
        case 6:
        case 7:
          return 1;
        case 8:
        case 9:
        case 10:
        case 11:
          return 2;
        case 12:
        case 13:
        case 14:
        case 15:
          return 3;
      }
    }
    return 0;
  }

}
