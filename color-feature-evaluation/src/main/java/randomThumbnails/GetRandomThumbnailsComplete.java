package randomThumbnails;

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

public class GetRandomThumbnailsComplete {

  public static final String pathThumbnails = "/tank/thumbnails/";
  public static final String pathDirectory = "color-feature-eval";


  public static void main(String[] args) throws IOException {
    List<String> files = null;
    List<String> segments = new ArrayList<>();

    try (Stream<Path> walk = Files.walk(Paths.get(pathThumbnails))) {
      files = walk.filter(Files::isRegularFile).map(Path::toString)
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }

    assert files != null;
    int numberOfFiles = files.size();
    List<Integer> randomIndexes = new ArrayList<>();
    Random random;
    random = new Random();
    for (int i = 0; i < 100; i++) {
      int r = random.nextInt(numberOfFiles);
      while (randomIndexes.contains(r)) { // ensure 100 different segments
        r = random.nextInt(numberOfFiles);
      }
      randomIndexes.add(r);
    }

    for (Integer randomIndex : randomIndexes) {
      segments.add(files.get(randomIndex));
    }

    if (!Files.exists(Paths.get(pathDirectory))) {
      Files.createDirectories(Paths.get(pathDirectory));
    }

    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(pathDirectory + "/Segments.json"), segments);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
