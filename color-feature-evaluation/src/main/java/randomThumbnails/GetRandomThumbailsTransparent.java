package randomThumbnails;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetRandomThumbailsTransparent {

  public static final String pathThumbnails = "/tank/thumbnails/";
  public static final String pathDirectory = "color-feature-eval";

  public static void main(String[] args) throws IOException {
    List<String> files = null;
    List<String> segmentsComplete = new ArrayList<>();
    List<String> segments = new ArrayList<>();

    try (Stream<Path> walk = Files.walk(Paths.get(pathThumbnails))) {
      files = walk.filter(Files::isRegularFile).map(Path::toString)
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }

    // determine first 50 thumbnails from list where also complete thumbnail exists
    ObjectMapper mapper = new ObjectMapper();
    try {
      segmentsComplete = mapper.readValue(new File(pathDirectory + "/Segments.json"),
          new TypeReference<List<String>>() {
          });
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<Integer> randomIndexesComplete = new ArrayList<>();
    Random randomomplete;
    randomomplete = new Random();
    for (int i = 0; i < 50; i++) {
      int r = randomomplete.nextInt(100);
      while (randomIndexesComplete.contains(r)) { // ensure 50 different segments
        r = randomomplete.nextInt(100);
      }
      randomIndexesComplete.add(r);
    }

    for (Integer randomIndex : randomIndexesComplete) {
      segments.add(segmentsComplete.get(randomIndex));
    }

    assert files != null;
    int numberOfFiles = files.size();
    List<Integer> randomIndexes = new ArrayList<>();
    Random random;
    random = new Random();
    for (int i = 0; i < 50; i++) {
      int r = random.nextInt(numberOfFiles);
      while (randomIndexes.contains(r)) { // ensure 50 different segments
        r = random.nextInt(numberOfFiles);
      }
      if (!segments.contains(files.get(r))) { // ensure 50 segments not yet sketched completely
        randomIndexes.add(r);
      } else {
        i--;
      }
    }

    for (Integer randomIndex : randomIndexes) {
      segments.add(files.get(randomIndex));
    }

    if (!Files.exists(Paths.get(pathDirectory))) {
      Files.createDirectories(Paths.get(pathDirectory));
    }

    try {
      mapper.writeValue(new File(pathDirectory + "/SegmentsTransparent.json"), segments);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
