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
import org.vitrivr.cineast.core.config.CacheConfig;
import org.vitrivr.cineast.core.config.QueryConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig.Distance;
import org.vitrivr.cineast.core.data.ReadableFloatVector;
import org.vitrivr.cineast.core.data.frames.VideoFrame;
import org.vitrivr.cineast.core.data.raw.CachedDataFactory;
import org.vitrivr.cineast.core.data.score.ScoreElement;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.extraction.segmenter.FuzzyColorHistogram;
import org.vitrivr.cineast.core.extraction.segmenter.FuzzyColorHistogramCalculator;
import org.vitrivr.cineast.core.features.abstracts.AbstractFeatureModule;

public class MedianFuzzyHistSuperpixelFh04 extends AbstractFeatureModule {

  private static final Logger LOGGER = LogManager.getLogger();

  private final CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
  private final CachedDataFactory factory = new CachedDataFactory(cacheConfig);

  public MedianFuzzyHistSuperpixelFh04() {
    super("features_MedianFuzzyHistSuperpixelFh04", 2f / 4f, 15);
  }

  @Override
  public void processSegment(SegmentContainer shot) {
    if (shot.getMostRepresentativeFrame() == VideoFrame.EMPTY_VIDEO_FRAME) {
      return;
    }
    if (!phandler.idExists(shot.getId())) {
      BufferedImage superpixel = applySuperpixel(shot);

      FuzzyColorHistogram fch = FuzzyColorHistogramCalculator.getHistogramNormalized(superpixel);
      persist(shot.getId(), fch);
    }
  }

  @Override
  public List<ScoreElement> getSimilar(SegmentContainer sc, ReadableQueryConfig qc) {
    BufferedImage superpixel = applySuperpixel(sc);

    FuzzyColorHistogram query = FuzzyColorHistogramCalculator.getHistogramNormalized(superpixel);
    return getSimilar(ReadableFloatVector.toArray(query), qc);
  }

  @Override
  protected QueryConfig setQueryConfig(ReadableQueryConfig qc) {
    return QueryConfig.clone(qc).setDistanceIfEmpty(Distance.chisquared);
  }

  private BufferedImage applySuperpixel(SegmentContainer segmentContainer) {

    BufferedImage image = segmentContainer.getMedianImg().getBufferedImage();
    image = ConvertBufferedImage.stripAlphaChannel(image);
    ImageType<Planar<GrayF32>> imageType = ImageType.pl(3, GrayF32.class);
    ImageSuperpixels alg = FactoryImageSegmentation.fh04(new ConfigFh04(100, 30), imageType);
    ImageBase color = imageType.createImage(image.getWidth(), image.getHeight());
    ConvertBufferedImage.convertFrom(image, color, true);
    BufferedImage superpixel = Superpixel.performSegmentation(alg, color);
    return superpixel;
  }

}
