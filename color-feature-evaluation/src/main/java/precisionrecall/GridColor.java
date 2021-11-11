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
import org.vitrivr.cineast.core.util.ColorUtils;

public class GridColor {

  public static String path_results;

  private static int numberOfRelevantSegments;

  private static float x;

  private static ArrayList<PrecisionRecall.Entry> dictQuery = new ArrayList<>();
  private static ArrayList<Grid> dictGrid = new ArrayList<>();

  private static int falsePosPixelwise = 0;
  private static int falseNegPixelwise = 0;

  private static int falsePosAverage = 0;
  private static int falseNegAverage = 0;


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

    int correctPixelwise = 0;
    int correctAverage = 0;

    for (PrecisionRecall.Entry e : dictQuery) {
      Grid g = dictGrid.get(e.number);
      // Entry has String name and int number
      // System.out.println(e.name);
      //BufferedImage gridSketch = ImageIO.read(new File("grid/png/" + e.name));
      // set e.name to id
      int id = Integer.parseInt(e.name.substring(5, e.name.length() - 4));
      int gridSize = getGridSize(id);
      // System.out.println(gridSize);
      QueryInfo queryInfo = new QueryInfo();
      int idName = id - 1;
      try {
        queryInfo = mapper.readValue(new File("../json-queries-grids/query-" + idName + ".json"),
            QueryInfo.class);
      } catch (IOException exception) {
        exception.printStackTrace();
      }
      int[] res = getTopSegments(g, gridSize, queryInfo);
      correctPixelwise += res[0];
      correctAverage += res[1];
    }

    System.out.println(correctPixelwise);
    System.out.println(correctAverage);

    // Precision, recall computation

    //double p = correct / (correct + falsePos);
    double pPixelwise = (float) correctPixelwise / (correctPixelwise + falsePosPixelwise);
    double pAverage = (float) correctAverage / (correctAverage + falsePosAverage);

    // TODO go through all thumbnails to compute falseNeg!!!
    double rPixelwise = correctPixelwise / (correctPixelwise + falseNegPixelwise);
    double rAverage = correctAverage / (correctAverage + falseNegAverage);

    System.out.println("Calculation on occurring pixels : " + pPixelwise);

    System.out.println("Precision : " + pPixelwise);
    System.out.println("Recall: " + rPixelwise);

    System.out.println("-------------------------------------------");

    System.out.println("Calculation on average color : " + pPixelwise);

    System.out.println("Precision : " + pAverage);
    System.out.println("Recall: " + rAverage);

  }

  private static int[] getTopSegments(Grid grid, int size, QueryInfo queryInfo)
      throws IOException {
    int correctCliassifiedPixelwise = 0;
    int correctCliassifiedAverage = 0;
    int[] correct = new int[2];
    for (Segment segment : queryInfo.getSegments()) {
      int until = segment.segment_id.indexOf("_", segment.segment_id.indexOf("_") + 1);
      String video = segment.segment_id.substring(0, until);
      // System.out.println(video);
      String file = "/tank/thumbnails/" + video + "/" + segment.segment_id + ".png";
      BufferedImage thumb;
      try {
        thumb = ImageIO.read(new File(file));
      } catch (IOException ioException) {
        continue;
      }

      int h = thumb.getHeight();
      int w = thumb.getWidth();
      int h_p = h / size;
      int w_p = w / size;

      int containingPixelwise = 0;
      int containingAverage = 0;
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

        boolean containPixelwise = checkFieldPixelwise(startX, startY, endX, endY, col, thumb);
        boolean containAverage = checkFieldAverage(startX, startY, endX, endY, col, thumb);
        if (containPixelwise) {
          containingPixelwise++;
        }
        if (containAverage) {
          containingAverage++;
        }
      }
      if (containingPixelwise == grid.getGrid().size()) {
        correctCliassifiedPixelwise++;
      } else {
        falsePosPixelwise++;
      }

      if (containingAverage == grid.getGrid().size()) {
        correctCliassifiedAverage++;
      } else {
        falsePosAverage++;
      }

    }
    correct[0] = correctCliassifiedPixelwise;
    correct[1] = correctCliassifiedAverage;
    return correct;
  }

  private static boolean checkFieldPixelwise(int startX, int startY, int endX, int endY, Color org,
      BufferedImage b) {
    int countColor = 0;
    int pixel = 0;
    for (int i = startX; i < endX; i++) {
      for (int j = startY; j < endY; j++) {
        Color img = new Color(b.getRGB(i, j));
        countColor = countColor + checkColorInBounds(org, img);
        pixel++;
      }
    }
    // ration that needed to be colored = 1/4
    int min = pixel / 4;
    return countColor > min;
  }

  private static boolean checkFieldAverage(int startX, int startY, int endX, int endY, Color org,
      BufferedImage b) {
    int pixel = 0;
    int size = (endX - startX) * (endY - startY);
    int[] colors = new int[size];
    for (int i = startX; i < endX; i++) {
      for (int j = startY; j < endY; j++) {
        colors[pixel] = (b.getRGB(i, j));
        pixel++;
      }
    }

    int avg = ColorUtils.getAvg(colors);
    Color average = new Color(avg);
    int res = checkColorInBounds(org, average);

    return res == 1;
  }

  private static int checkColorInBounds(Color grid, Color thumbnail) {
    double deltaE = DeltaE.calculateDeltaE(grid, thumbnail);
    if (deltaE < 44.4) {
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
