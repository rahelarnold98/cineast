package org.vitrivr.cineast.standalone.importer.vbs2019;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.data.providers.primitive.PrimitiveTypeProvider;
import org.vitrivr.cineast.core.importer.Importer;

import javax.swing.text.html.Option;

public class MLTFeatureImporter implements Importer<Pair<String,float[]>> {

  private static final Logger LOGGER = LogManager.getLogger();
  private final BufferedReader reader;

  public MLTFeatureImporter(Path input) throws IOException {
    reader = new BufferedReader(new FileReader(input.toFile()));
  }

  private synchronized Optional<ImmutablePair<String, float[]>> nextPair() {
    try {
      String line = reader.readLine();
      if(line==null){
        LOGGER.info("Reached EoF");
        return Optional.empty();
      }
      String[] split = line.split(",");
      String id = split[0];
      float[] feature = new float[512];
      for (int i = 1; i < split.length; i++) {
        feature[i-1] = Float.parseFloat(split[i]);
      }
      return Optional.of(ImmutablePair.of(id, feature));
    } catch (IOException e) {
      LOGGER.error("IOException while reading");
      LOGGER.error(e);
      return Optional.empty();
    }
  }

  /**
   * @return Pair mapping a segmentID to a List of Descriptions
   */
  @Override
  public Pair<String, float[]> readNext() {
    try {
      Optional<ImmutablePair<String, float[]>> node = nextPair();
      return node.orElse(null);
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  @Override
  public Map<String, PrimitiveTypeProvider> convert(Pair<String, float[]> data) {
    final HashMap<String, PrimitiveTypeProvider> map = new HashMap<>(2);
    String id = data.getLeft();
    map.put("id", PrimitiveTypeProvider.fromObject(id));
    map.put("feature", PrimitiveTypeProvider.fromObject(data.getRight()));
    return map;
  }
}
