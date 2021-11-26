package org.vitrivr.cineast.core.features;

import boofcv.abst.segmentation.ImageSuperpixels;
import boofcv.factory.segmentation.FactoryImageSegmentation;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.Planar;
import java.awt.Color;
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

public class CLDSuperpixelWatershed extends AbstractFeatureModule {

  private final CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
  private final CachedDataFactory factory = new CachedDataFactory(cacheConfig);

  public CLDSuperpixelWatershed() {
    super("features_CLDSuperpixelWatershed", 1960f / 4f, 12);
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
    BufferedImage image = segmentContainer.getMostRepresentativeFrame().getImage()
        .getBufferedImage();
    BufferedImage image2 = segmentContainer.getMostRepresentativeFrame().getImage()
        .getBufferedImage();
    image = ConvertBufferedImage.stripAlphaChannel(image);
    ImageType<Planar<GrayF32>> imageType = ImageType.pl(3, GrayF32.class);
    ImageSuperpixels alg = FactoryImageSegmentation.watershed(null, imageType);
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
    return image2;
  }

}
