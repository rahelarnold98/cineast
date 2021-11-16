package org.vitrivr.cineast.core.features;

import boofcv.abst.segmentation.ImageSuperpixels;
import boofcv.factory.segmentation.ConfigFh04;
import boofcv.factory.segmentation.ConfigSlic;
import boofcv.factory.segmentation.FactoryImageSegmentation;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.color.ColorConverter;
import org.vitrivr.cineast.core.color.ReadableLabContainer;
import org.vitrivr.cineast.core.config.CacheConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.data.ReadableFloatVector;
import org.vitrivr.cineast.core.data.raw.CachedDataFactory;
import org.vitrivr.cineast.core.data.raw.images.MultiImage;
import org.vitrivr.cineast.core.data.score.ScoreElement;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.features.abstracts.AbstractFeatureModule;
import org.vitrivr.cineast.core.util.ColorUtils;

public class AverageColorSuperpixelSlic extends AbstractFeatureModule {

  private static final Logger LOGGER = LogManager.getLogger();

  public AverageColorSuperpixelSlic() {
    super("features_AverageColorSuperpixelSlic", 196f / 4f, 3);
  }

  public static ReadableLabContainer getAvg(MultiImage img) {
    int avg = ColorUtils.getAvg(img.getColors());
    return ColorConverter.cachedRGBtoLab(avg);
  }

  @Override
  public void processSegment(SegmentContainer shot) throws IOException {
    if (shot.getAvgImg() == MultiImage.EMPTY_MULTIIMAGE) {
      return;
    }
    if (!phandler.idExists(shot.getId())) {
      CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
      CachedDataFactory factory = new CachedDataFactory(cacheConfig);
      BufferedImage superpixel = applySuperpixel(shot, factory);

      MultiImage multiImage = factory.newMultiImage(superpixel);
      ReadableLabContainer avg = getAvg(multiImage);
      persist(shot.getId(), avg);
    }
  }


  @Override
  public List<ScoreElement> getSimilar(SegmentContainer sc, ReadableQueryConfig qc) {
    CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
    CachedDataFactory factory = new CachedDataFactory(cacheConfig);
    BufferedImage superpixel = applySuperpixel(sc, factory);

    MultiImage multiImage = factory.newMultiImage(superpixel);
    ReadableLabContainer query = getAvg(multiImage);
    return getSimilar(ReadableFloatVector.toArray(query), qc);
  }

  private BufferedImage applySuperpixel(SegmentContainer segmentContainer,
      CachedDataFactory factory) {

    BufferedImage image = segmentContainer.getAvgImg().getBufferedImage();
    image = ConvertBufferedImage.stripAlphaChannel(image);
    ImageType<Planar<GrayF32>> imageType = ImageType.pl(3, GrayF32.class);
    ImageSuperpixels alg = FactoryImageSegmentation.slic(new ConfigSlic(400), imageType);
    ImageBase color = imageType.createImage(image.getWidth(), image.getHeight());
    ConvertBufferedImage.convertFrom(image, color, true);
    BufferedImage superpixel = Superpixel.performSegmentation(alg, color);
    return superpixel;
  }

}
