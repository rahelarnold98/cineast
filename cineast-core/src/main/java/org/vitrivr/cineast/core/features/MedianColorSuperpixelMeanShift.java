package org.vitrivr.cineast.core.features;

import boofcv.abst.segmentation.ImageSuperpixels;
import boofcv.factory.segmentation.ConfigFh04;
import boofcv.factory.segmentation.FactoryImageSegmentation;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import java.awt.image.BufferedImage;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.color.ColorConverter;
import org.vitrivr.cineast.core.color.LabContainer;
import org.vitrivr.cineast.core.color.ReadableRGBContainer;
import org.vitrivr.cineast.core.config.CacheConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.data.ReadableFloatVector;
import org.vitrivr.cineast.core.data.frames.VideoFrame;
import org.vitrivr.cineast.core.data.providers.MedianImgProvider;
import org.vitrivr.cineast.core.data.raw.CachedDataFactory;
import org.vitrivr.cineast.core.data.raw.images.MultiImage;
import org.vitrivr.cineast.core.data.score.ScoreElement;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.features.abstracts.AbstractFeatureModule;

public class MedianColorSuperpixelMeanShift extends AbstractFeatureModule {

  private static final Logger LOGGER = LogManager.getLogger();

  public MedianColorSuperpixelMeanShift() {
    super("features_MedianColorSuperpixelMeanShift", 196f / 4f, 3);
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
  public void processSegment(SegmentContainer shot) {
    if (shot.getMostRepresentativeFrame() == VideoFrame.EMPTY_VIDEO_FRAME) {
      return;
    }
    if (!phandler.idExists(shot.getId())) {
      CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
      CachedDataFactory factory = new CachedDataFactory(cacheConfig);
      BufferedImage superpixel = applySuperpixel(shot, factory);

      MultiImage multiImage = factory.newMultiImage(superpixel);
      LabContainer median = getMedian(multiImage);
      persist(shot.getId(), median);
    }
  }

  @Override
  public List<ScoreElement> getSimilar(SegmentContainer sc, ReadableQueryConfig qc) {
    CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
    CachedDataFactory factory = new CachedDataFactory(cacheConfig);
    BufferedImage superpixel = applySuperpixel(sc, factory);

    MultiImage multiImage = factory.newMultiImage(superpixel);
    LabContainer query = getMedian(multiImage);
    return getSimilar(ReadableFloatVector.toArray(query), qc);
  }

  private BufferedImage applySuperpixel(SegmentContainer segmentContainer,
      CachedDataFactory factory) {

    BufferedImage image = segmentContainer.getMedianImg().getBufferedImage();
    image = ConvertBufferedImage.stripAlphaChannel(image);
    ImageType<Planar<GrayF32>> imageType = ImageType.pl(3, GrayF32.class);
    ImageSuperpixels alg = FactoryImageSegmentation.meanShift(null, imageType);
    ImageBase color = imageType.createImage(image.getWidth(), image.getHeight());
    ConvertBufferedImage.convertFrom(image, color, true);
    BufferedImage superpixel = Superpixel.performSegmentation(alg, color);
    return superpixel;
  }

}
