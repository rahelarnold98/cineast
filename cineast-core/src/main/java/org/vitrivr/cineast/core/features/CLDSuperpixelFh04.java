package org.vitrivr.cineast.core.features;

import java.awt.image.BufferedImage;
import java.util.List;
import org.vitrivr.cineast.core.config.CacheConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.data.FloatVector;
import org.vitrivr.cineast.core.data.ReadableFloatVector;
import org.vitrivr.cineast.core.data.frames.VideoFrame;
import org.vitrivr.cineast.core.data.raw.CachedDataFactory;
import org.vitrivr.cineast.core.data.raw.images.MultiImage;
import org.vitrivr.cineast.core.data.score.ScoreElement;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.features.abstracts.AbstractFeatureModule;
import org.vitrivr.cineast.core.util.ColorLayoutDescriptor;

public class CLDSuperpixelFh04 extends AbstractFeatureModule {

  private final CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
  private final CachedDataFactory factory = new CachedDataFactory(cacheConfig);

  public CLDSuperpixelFh04() {
    super("features_CLDSuperpixelFh04", 1960f / 4f, 12);
  }


  @Override
  public void processSegment(SegmentContainer shot) {
    if (shot.getMostRepresentativeFrame() == VideoFrame.EMPTY_VIDEO_FRAME) {
      return;
    }
    if (!phandler.idExists(shot.getId())) {
      BufferedImage superpixel = applySuperpixel(shot);

      MultiImage multiImage = factory.newMultiImage(superpixel);
      FloatVector fv = ColorLayoutDescriptor.calculateCLD(multiImage);
      persist(shot.getId(), fv);
    }
  }

  @Override
  public List<ScoreElement> getSimilar(SegmentContainer sc, ReadableQueryConfig qc) {
    BufferedImage superpixel = applySuperpixel(sc);

    MultiImage multiImage = factory.newMultiImage(superpixel);
    FloatVector query = ColorLayoutDescriptor.calculateCLD(multiImage);
    return getSimilar(ReadableFloatVector.toArray(query), qc);
  }

  private BufferedImage applySuperpixel(SegmentContainer segmentContainer) {
    return Superpixel.applySuperpixelSC(segmentContainer, Superpixel.IMG_REP, Superpixel.ALG_FH04);
  }

}
