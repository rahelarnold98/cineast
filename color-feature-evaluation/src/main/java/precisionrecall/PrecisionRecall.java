package precisionrecall;

import evaluationQueries.sketches.ColorFeatureEvaluation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PrecisionRecall {

  public static String path_results;

  private static int numberOfRelevantSegments;

  private static float x;

  private static ArrayList<Entry> dict = new ArrayList<>();


  public static void main(String[] args) {

    ColorFeatureEvaluation colorFeatureEvaluation = new ColorFeatureEvaluation();
    ObjectMapper mapper = new ObjectMapper();
    try {
      colorFeatureEvaluation = mapper.readValue(new File("ColorFeatureEvaluation.json"), ColorFeatureEvaluation.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    path_results = "json-queries";
    String path_dict = colorFeatureEvaluation.getPathDictionary() + "/" + colorFeatureEvaluation.getNameDictionary();
    int numberOfQueries = 3;

    numberOfRelevantSegments = 10;

    // not yet used
    int errorAcceptance = 1; // +/- 1 segments considered right --> looking for 3 segments

    x = 0;
    float y = numberOfQueries * numberOfRelevantSegments;
    float z = numberOfQueries * (2 + errorAcceptance + 1);

    try {
      dict = mapper.readValue(new File(path_dict),
          new TypeReference<ArrayList<Entry>>() {
          });
    } catch (IOException e) {
      e.printStackTrace();
    }

    checkSegments();
    //System.out.println(Arrays.deepToString(res));
    float p = x / y;
    float r = x / z;
    float beta = 1;
    float f = (float) ((Math.pow(beta, 2) + 1) * p * r / ((Math.pow(beta, 2) + 1) * p + r));

    System.out.println("Precision: " + p);
    System.out.println("Recall: " + r);
    System.out.println("F-Measure: " + f);


  }

  private static void checkSegments() {
    File dir = new File(path_results);
    File[] directoryListing = dir.listFiles();
    if (directoryListing != null) {
      for (File child : directoryListing) {
        String count = child.getName().substring(6, child.getName().indexOf("."));
        for (Entry entry : dict) {
          if (entry.getNumber() == Integer.parseInt(count)){
            // get name of relevant segment
            if (entry.getName().startsWith("sketch")) {
              String nameTopSegment = "v" + entry.getName().substring(6);
              nameTopSegment = nameTopSegment.substring(0, nameTopSegment.length() - 4);

              String nameLowerSegment;
              int id = Integer.parseInt(
                  nameTopSegment.substring(nameTopSegment.lastIndexOf("_") + 1).trim());
              int idm = id - 1;
              nameLowerSegment =
                  nameTopSegment.substring(0, nameTopSegment.lastIndexOf("_") + 1) + idm;

              int idp = id + 1;
              String nameHigherSegment;
              nameHigherSegment =
                  nameTopSegment.substring(0, nameTopSegment.lastIndexOf("_") + 1) + idp;

              QueryInfo queryInfo = new QueryInfo();
              try {
                ObjectMapper mapper = new ObjectMapper();
                queryInfo = mapper.readValue(new File(child.getAbsolutePath()), QueryInfo.class);
              } catch (IOException e) {
                e.printStackTrace();
              }

              int i = 0;
              for (Segment s : queryInfo.segments) {
                if (i < numberOfRelevantSegments) {
                  if (Objects.equals(s.segment_id, nameTopSegment)) {
                    x = x + 3;
                    break;
                  } else if (Objects.equals(s.segment_id, nameLowerSegment)) {
                    x = x + 1;
                  } else if (Objects.equals(s.segment_id, nameHigherSegment)) {
                    x = x + 1;
                  }
                  i++;
                } else {
                  break;
                }
              }
            }
          }
        }
      }
    }
  }

  static class Entry {

    String name;
    int number;

    Entry(String name, int number) {
      this.name = name;
      this.number = number;
    }

    Entry(){}

    public String getName() {
      return name;
    }

    public int getNumber() {
      return number;
    }
  }

}
