package automatedQueries;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Dictionary {

  //public static String path = "path to folder";
  public static String path = "/Users/rahelarnold/Desktop/Master Project/cineast/sketches";

  public static ArrayList<Entry> dict = new ArrayList<>();

  public static void main(String[] args) throws IOException {
    File dir = new File(path);
    File[] directoryListing = dir.listFiles();
    if (directoryListing != null) {
      for (File child : directoryListing) {

        if (FilenameUtils.getExtension(child.getName()).equals("png")) {
          byte[] fileContent = FileUtils.readFileToByteArray(new File(child.getAbsolutePath()));
          String encodedString = Base64.getEncoder().encodeToString(fileContent);
          dict.add(new Entry(child.getName(), encodedString));
        }
      }
    }
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File("Dictionary.json"), dict);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static class Entry {

    String name;
    String base64;

    Entry(String name, String base64) {
      this.name = name;
      this.base64 = base64;
    }

    public String getName() {
      return name;
    }

    public String getBase64() {
      return base64;
    }
  }

}
