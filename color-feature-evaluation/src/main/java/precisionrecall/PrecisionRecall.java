package precisionrecall;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PrecisionRecall {

  public static String path;

  private static int numberOfRelevantSegments;

  private static float x;


  public static void main(String[] args) {

    path = "results";
    int numberOfQueries = 100;

    numberOfRelevantSegments = 50;

    // not yet used
    int errorAcceptance = 1; // +/- 1 segments considered right --> looking for 3 segments

    x = 0;
    float y = numberOfQueries * numberOfRelevantSegments;
    float z = numberOfQueries * (2 + errorAcceptance + 1);

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
    File dir = new File(path);
    File[] directoryListing = dir.listFiles();
    if (directoryListing != null) {
      for (File child : directoryListing) {
        // get name of relevant segment
        if (child.getName().startsWith("sketch")) {
          String nameTopSegment = "v" + child.getName().substring(6);
          nameTopSegment = nameTopSegment.substring(0, nameTopSegment.length() - 5);

          String nameLowerSegment;
          int id = Integer.parseInt(
              nameTopSegment.substring(nameTopSegment.lastIndexOf("_") + 1).trim());
          int idm = id - 1;
          nameLowerSegment = nameTopSegment.substring(0, nameTopSegment.lastIndexOf("_") + 1) + idm;

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
