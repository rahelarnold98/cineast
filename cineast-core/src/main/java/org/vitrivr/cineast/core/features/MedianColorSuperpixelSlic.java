package org.vitrivr.cineast.core.features;

import boofcv.abst.segmentation.ImageSuperpixels;
import boofcv.factory.segmentation.ConfigSlic;
import boofcv.factory.segmentation.FactoryImageSegmentation;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.color.ColorConverter;
import org.vitrivr.cineast.core.color.LabContainer;
import org.vitrivr.cineast.core.color.ReadableRGBContainer;
import org.vitrivr.cineast.core.config.CacheConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.data.ReadableFloatVector;
import org.vitrivr.cineast.core.data.frames.VideoFrame;
import org.vitrivr.cineast.core.data.raw.CachedDataFactory;
import org.vitrivr.cineast.core.data.raw.images.MultiImage;
import org.vitrivr.cineast.core.data.score.ScoreElement;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.features.abstracts.AbstractFeatureModule;

public class MedianColorSuperpixelSlic extends AbstractFeatureModule {

  private static final Logger LOGGER = LogManager.getLogger();

  private final CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
  private final CachedDataFactory factory = new CachedDataFactory(cacheConfig);

  public MedianColorSuperpixelSlic() {
    super("features_MedianColorSuperpixelSlic", 196f / 4f, 3);
  }

  public static LabContainer getMedian(MultiImage img) {
    int[] r = new int[256], g = new int[256], b = new int[256];
    int[] colors = img.getColors();

    for (int color : colors) {
      if (ReadableRGBContainer.getAlpha(color) < 127) {
        continue;
      }
      r[ReadableRGBContainer.getRed(color)]++;
      g[ReadableRGBContainer.getGreen(color)]++;
      b[ReadableRGBContainer.getBlue(color)]++;
    }

    return ColorConverter
        .RGBtoLab(medianFromHistogram(r), medianFromHistogram(g), medianFromHistogram(b));
  }

  private static int medianFromHistogram(int[] hist) {
    int pos_l = 0, pos_r = hist.length - 1;
    int sum_l = hist[pos_l], sum_r = hist[pos_r];

    while (pos_l < pos_r) {
      if (sum_l < sum_r) {
        sum_l += hist[++pos_l];
      } else {
        sum_r += hist[--pos_r];
      }
    }
    return pos_l;
  }

  @Override
  public void processSegment(SegmentContainer shot) throws IOException {
    if (shot.getMostRepresentativeFrame() == VideoFrame.EMPTY_VIDEO_FRAME) {
      return;
    }
    if (!phandler.idExists(shot.getId())) {
      BufferedImage superpixel = applySuperpixel(shot);

      MultiImage multiImage = factory.newMultiImage(superpixel);
      LabContainer median = getMedian(multiImage);
      persist(shot.getId(), median);
    }
  }

  @Override
  public List<ScoreElement> getSimilar(SegmentContainer sc, ReadableQueryConfig qc)
      throws IOException {
    BufferedImage superpixel = applySuperpixel(sc);

    MultiImage multiImage = factory.newMultiImage(superpixel);
    LabContainer query = getMedian(multiImage);
    return getSimilar(ReadableFloatVector.toArray(query), qc);
  }

  private BufferedImage applySuperpixel(SegmentContainer segmentContainer) throws IOException {

    BufferedImage image = segmentContainer.getMedianImg().getBufferedImage();
    BufferedImage image2 = segmentContainer.getMedianImg().getBufferedImage();
    image = ConvertBufferedImage.stripAlphaChannel(image);
    ImageType<Planar<GrayF32>> imageType = ImageType.pl(3, GrayF32.class);
    ImageSuperpixels alg = FactoryImageSegmentation.slic(new ConfigSlic(400), imageType);
    ImageBase color = imageType.createImage(image.getWidth(), image.getHeight());
    ConvertBufferedImage.convertFrom(image, color, true);
    BufferedImage superpixel = Superpixel.performSegmentation(alg, color);

    for (int x = 0; x < superpixel.getWidth(); x++){
      for (int y = 0; y < superpixel.getHeight(); y++) {
        java.awt.Color c = new Color(image2.getRGB(x,y), true);
        if (c.getAlpha() == 1){
          Color cS = new Color(superpixel.getRGB(x,y), true);
          image2.setRGB(x,y, cS.getRGB());
        }
      }
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(image2, "png", baos);
    byte[] bytes = baos.toByteArray();
    System.out.println(encodeFileToBase64Binary(bytes));
    return image2;
  }

  private static String encodeFileToBase64Binary(byte[] fileContent) throws IOException {
    String encodedString =
        "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent);
    return encodedString;
  }

}
