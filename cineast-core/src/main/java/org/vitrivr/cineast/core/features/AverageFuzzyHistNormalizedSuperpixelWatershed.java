package org.vitrivr.cineast.core.features;

import boofcv.abst.segmentation.ImageSuperpixels;
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
import org.vitrivr.cineast.core.data.raw.CachedDataFactory;
import org.vitrivr.cineast.core.data.raw.images.MultiImage;
import org.vitrivr.cineast.core.data.score.ScoreElement;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.extraction.segmenter.FuzzyColorHistogram;
import org.vitrivr.cineast.core.extraction.segmenter.FuzzyColorHistogramCalculator;
import org.vitrivr.cineast.core.features.abstracts.AbstractFeatureModule;
import org.vitrivr.cineast.core.util.ImageHistogramEqualizer;

public class AverageFuzzyHistNormalizedSuperpixelWatershed extends AbstractFeatureModule {

  private static final Logger LOGGER = LogManager.getLogger();

  private final CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
  private final CachedDataFactory factory = new CachedDataFactory(cacheConfig);

  public AverageFuzzyHistNormalizedSuperpixelWatershed() {
    super("features_AverageFuzzyHistNormalizedSuperpixelWatershed", 2f / 4f, 15);
  }

  @Override
  public void processSegment(SegmentContainer shot) {
    if (shot.getAvgImg() == MultiImage.EMPTY_MULTIIMAGE) {
      return;
    }
    if (!phandler.idExists(shot.getId())) {
      BufferedImage superpixel = applySuperpixel(shot);

      MultiImage multiImage = factory.newMultiImage(superpixel);
      FuzzyColorHistogram fch = FuzzyColorHistogramCalculator.getHistogramNormalized(ImageHistogramEqualizer.getEqualized(multiImage).getBufferedImage());
      persist(shot.getId(), fch);
    }
  }

  @Override
  public List<ScoreElement> getSimilar(SegmentContainer sc, ReadableQueryConfig qc) {
    BufferedImage superpixel = applySuperpixel(sc);

    MultiImage multiImage = factory.newMultiImage(superpixel);
    FuzzyColorHistogram query = FuzzyColorHistogramCalculator.getHistogramNormalized((ImageHistogramEqualizer.getEqualized(multiImage)).getBufferedImage());
    return getSimilar(ReadableFloatVector.toArray(query), qc);
  }

  @Override
  protected ReadableQueryConfig setQueryConfig(ReadableQueryConfig qc) {
    return QueryConfig.clone(qc).setDistanceIfEmpty(Distance.chisquared);
  }

  private BufferedImage applySuperpixel(SegmentContainer segmentContainer) {

    BufferedImage image = segmentContainer.getAvgImg().getBufferedImage();
    image = ConvertBufferedImage.stripAlphaChannel(image);
    ImageType<Planar<GrayF32>> imageType = ImageType.pl(3, GrayF32.class);
    ImageSuperpixels alg = FactoryImageSegmentation.watershed(null, imageType);
    ImageBase color = imageType.createImage(image.getWidth(), image.getHeight());
    ConvertBufferedImage.convertFrom(image, color, true);
    BufferedImage superpixel = Superpixel.performSegmentation(alg, color);
    return superpixel;
  }

}
