package org.vitrivr.cineast.art.modules;

import org.vitrivr.cineast.api.WebUtils;
import org.vitrivr.cineast.art.modules.abstracts.AbstractVisualizationModule;
import org.vitrivr.cineast.art.modules.visualization.VisualizationResult;
import org.vitrivr.cineast.art.modules.visualization.VisualizationType;
import org.vitrivr.cineast.core.color.ColorConverter;
import org.vitrivr.cineast.core.color.RGBContainer;
import org.vitrivr.cineast.core.color.ReadableLabContainer;
import org.vitrivr.cineast.core.config.Config;
import org.vitrivr.cineast.core.data.providers.primitive.PrimitiveTypeProvider;
import org.vitrivr.cineast.core.db.DBSelector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by sein on 30.08.16.
 */
public class VisualizationMedianColorStripeVariable extends AbstractVisualizationModule {
  public VisualizationMedianColorStripeVariable() {
    super();
    tableNames.put("MedianColor", "features_MedianColor");
  }

  @Override
  public String getDisplayName() {
    return "VisualizationMedianColorStripeVariable";
  }

  public static void main(String[] args){
    VisualizationMedianColorStripeVariable vis = new VisualizationMedianColorStripeVariable();
    vis.init(Config.getDatabaseConfig().getSelectorSupplier());
    System.out.println(vis.visualizeMultimediaobject("11"));
    vis.finish();
  }

  @Override
  public String visualizeMultimediaobject(String multimediaobjectId) {
    DBSelector selector = selectors.get("MedianColor");
    DBSelector shotSelector = selectors.get(segmentTable);
    List<Map<String, PrimitiveTypeProvider>> shots = shotSelector.getRows("multimediaobject", multimediaobjectId);

    int count = 0;
    int totalWidth = 0;
    int[] colors = new int[shots.size()];
    int[] widths = new int[shots.size()];
    for (Map<String, PrimitiveTypeProvider> shot : shots) {
      List<Map<String, PrimitiveTypeProvider>> result = selector.getRows("id", shot.get("id").getString());
      widths[count] = (shot.get("segmentend").getInt() - shot.get("segmentstart").getInt())/10 + 1;
      totalWidth += widths[count];
      for (Map<String, PrimitiveTypeProvider> row : result) {
        float[] arr = row.get("feature").getFloatArray();
        for (int i = 0; i < 8; i++) {
          RGBContainer rgbContainer = ColorConverter.LabtoRGB(new ReadableLabContainer(arr[0], arr[1], arr[2]));
          colors[count] = rgbContainer.toIntColor();
        }
      }
      count++;
    }

    System.out.println(Arrays.toString(widths));
    System.out.println(totalWidth);

    BufferedImage image = new BufferedImage(totalWidth, 100, BufferedImage.TYPE_INT_RGB);
    Graphics2D graph = image.createGraphics();
    for(int i=0, pos=0;i<widths.length;i++){
      graph.setColor(new Color(colors[i]));
      graph.fillRect(pos, 0, widths[i], 100);
      pos += widths[i];
    }
    graph.dispose();

    try {
      ImageIO.write(image, "png", new File("src/resources/imageMedianColorStripeVariable.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return WebUtils.BufferedImageToDataURL(image, "png");
  }

  @Override
  public List<VisualizationType> getVisualizations() {
    List<VisualizationType> types = new ArrayList();
    types.add(VisualizationType.VISUALIZATION_VIDEO);
    return types;
  }

  @Override
  public VisualizationResult getResultType() {
    return VisualizationResult.IMAGE;
  }
}
