package org.vitrivr.cineast.core.precisionrecall;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EvaluateColorFeatures {

  public static int numSeg;
  public static int numQue;
  public static String path;

  private static String[][] knowledge;
  private static String[][] precisionRecall;

  private static HashMap<String, Integer> queryIndex = new HashMap<>();
  private static HashMap<String, Integer> segmentIndex = new HashMap<>();

  private static ArrayList<QueryPerformance> queryPerformance = new ArrayList<>();

  public static void main(String[] args) {
    /*Scanner scanner = new Scanner(System.in);
    System.out.println("Please enter number of video segments");
    numSeg = Integer.parseInt(scanner.nextLine());
    System.out.println("Please enter number of queries to process");
    numQue = Integer.parseInt(scanner.nextLine());
    System.out.println("Please enter path to JSON files");
    path = scanner.nextLine();*/

    numSeg = 3087;
    numQue = 30;
    path = "query_26_9";

    knowledge = new String[numSeg][numQue + 4];
    precisionRecall = new String[2][numQue];

    fillS();
    //System.out.println(Arrays.deepToString(res));
    calculateP();

    calculatePrRe();

    pairInfoPrecision();

    export();

  }

  private static void fillS() {
    File dir = new File(path);
    File[] directoryListing = dir.listFiles();
    if (directoryListing != null) {
      int counterQuery = 0;
      int counterSegment = 0;
      for (File child : directoryListing) {
        //queryIndex.put(child.getName(), counterQuery);
        QueryInfo queryInfo = new QueryInfo();
        try {
          ObjectMapper mapper = new ObjectMapper();
          queryInfo = mapper.readValue(new File(child.getAbsolutePath()), QueryInfo.class);
        } catch (IOException e) {
          e.printStackTrace();
        }
        queryIndex.put(queryInfo.queryId, counterQuery);
        int i = 0;

        for (Segment s : queryInfo.segments) {
          if (i < 10) {
            int currentSegmentID;
            if (segmentIndex.get(s.segment_id) == null) {
              segmentIndex.put(s.segment_id, counterSegment);
              currentSegmentID = counterSegment;
              counterSegment++;
              knowledge[currentSegmentID][0] = s.segment_id;
            } else {
              currentSegmentID = segmentIndex.get(s.segment_id);
            }
            knowledge[currentSegmentID][counterQuery + 3] = Integer.toString(1);
            i++;
          }

        }
        counterQuery++;
      }

      for (int i = 0; i < numSeg; i++) {
        for (int j = 0; j < numQue + 4; j++) {
          if (j == 2) {
            // fill S+
            knowledge[i][j] = Integer.toString(1);
          }
          if (j == numQue + 3) {
            // fill S-
            knowledge[i][j] = Integer.toString(0);
          }
          if (j != 1 && knowledge[i][j] == null) {
            // fill empty fields with 0 expect column stored for P
            knowledge[i][j] = Integer.toString(0);
          }
        }
      }

    }
  }

  private static void calculateP() {
    for (int i = 0; i < numSeg; i++) {
      int a = 0;
      // sum up occurrences
      for (int j = 2; j < numQue + 3; j++) {
        a += Integer.parseInt(knowledge[i][j]);
      }
      // calculate probability
      float p = (float) a / (numQue + 2);
      knowledge[i][1] = Float.toString(p);
    }
  }

  private static void calculatePrRe() {
    float p = 0;
    for (int i = 0; i < numSeg; i++) {
      p += Float.parseFloat(knowledge[i][1]);
    }
    // numQue + 3 because last row not of interest
    for (int i = 3; i < numQue + 3; i++) {
      int s = 0;
      float ps = 0;
      for (int j = 0; j < numSeg; j++) {
        s += Integer.parseInt(knowledge[j][i]);
        ps += Integer.parseInt(knowledge[j][i]) * Float.parseFloat(knowledge[j][1]);
      }
      // calculate precision
      precisionRecall[0][i - 3] = Float.toString(ps / s);
      // calculate recall
      precisionRecall[1][i - 3] = Float.toString(ps / p);
    }
  }

  private static void pairInfoPrecision() {
    for (int i = 0; i < numQue; i++) {
      int finalI = i;
      Set<String> idx = queryIndex.entrySet()
          .stream()
          .filter(entry -> Objects.equals(entry.getValue(), finalI))
          .map(Entry::getKey)
          .collect(Collectors.toSet());
      for (String s : idx) {
        QueryPerformance seg = new QueryPerformance(s, Float.parseFloat(precisionRecall[0][i]),
            Float.parseFloat(precisionRecall[1][i]));
        queryPerformance.add(seg);
      }

    }


  }

  private static void export() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File("PrecisionRecall.json"), queryPerformance);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
