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
import java.util.List;
import org.vitrivr.cineast.core.color.ColorConverter;
import org.vitrivr.cineast.core.color.HSVContainer;
import org.vitrivr.cineast.core.color.ReadableRGBContainer;
import org.vitrivr.cineast.core.config.CacheConfig;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.data.FloatVectorImpl;
import org.vitrivr.cineast.core.data.frames.VideoFrame;
import org.vitrivr.cineast.core.data.raw.CachedDataFactory;
import org.vitrivr.cineast.core.data.raw.images.MultiImage;
import org.vitrivr.cineast.core.data.score.ScoreElement;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.features.abstracts.AbstractFeatureModule;

public class HueHistogramSuperpixelSlic extends AbstractFeatureModule {

  public HueHistogramSuperpixelSlic() {
    super("features_huehistogramSuperpixelSlic", 16f, 16);
  }

  @Override
  public void processSegment(SegmentContainer shot) {
    if (shot.getMostRepresentativeFrame() == VideoFrame.EMPTY_VIDEO_FRAME) {
      return;
    }

    float[] hist = new float[16];
    CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
    CachedDataFactory factory = new CachedDataFactory(cacheConfig);

    for (VideoFrame frame : shot.getVideoFrames()){
      BufferedImage superpixel = applySuperpixel(frame.getImage().getBufferedImage(), factory);
      MultiImage multiImage = factory.newMultiImage(superpixel);

      updateHist(hist, multiImage.getThumbnailColors());
    }

    float sum = 0;
    for(int i = 0; i < hist.length; ++i){
      sum += hist[i];
    }
    if(sum > 1f){
      for(int i = 0; i < hist.length; ++i){
        hist[i] /= sum;
      }
    }

    persist(shot.getId(), new FloatVectorImpl(hist));

  }

  @Override
  public List<ScoreElement> getSimilar(SegmentContainer sc, ReadableQueryConfig qc) {
    CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
    CachedDataFactory factory = new CachedDataFactory(cacheConfig);
    BufferedImage superpixel = applySuperpixel(sc, factory);
    MultiImage multiImage = factory.newMultiImage(superpixel);

    float[] query = updateHist(new float[16], multiImage.getThumbnailColors());
    return getSimilar(query, qc);

  }

  private static float[] updateHist(float[] hist, int[] colors){
    for(int color : colors){
      HSVContainer container = ColorConverter.RGBtoHSV(new ReadableRGBContainer(color));
      if(container.getS() > 0.2f && container.getV() > 0.3f){
        float h = container.getH() * hist.length;
        int idx = (int) h;
        hist[idx] += h - idx;
        hist[(idx + 1) % hist.length] += idx + 1 - h;
      }
    }
    return hist;
  }

  private BufferedImage applySuperpixel(BufferedImage image,
      CachedDataFactory factory) {
    image = ConvertBufferedImage.stripAlphaChannel(image);
    ImageType<Planar<GrayF32>> imageType = ImageType.pl(3, GrayF32.class);
    ImageSuperpixels alg = FactoryImageSegmentation.slic(new ConfigSlic(400), imageType);
    ImageBase color = imageType.createImage(image.getWidth(), image.getHeight());
    ConvertBufferedImage.convertFrom(image, color, true);
    BufferedImage superpixel = Superpixel.performSegmentation(alg, color);
    return superpixel;
  }

  private BufferedImage applySuperpixel(SegmentContainer segmentContainer,
      CachedDataFactory factory) {

    BufferedImage image = segmentContainer.getMostRepresentativeFrame().getImage().getBufferedImage();
    image = ConvertBufferedImage.stripAlphaChannel(image);
    ImageType<Planar<GrayF32>> imageType = ImageType.pl(3, GrayF32.class);
    ImageSuperpixels alg = FactoryImageSegmentation.slic(new ConfigSlic(400), imageType);
    ImageBase color = imageType.createImage(image.getWidth(), image.getHeight());
    ConvertBufferedImage.convertFrom(image, color, true);
    BufferedImage superpixel = Superpixel.performSegmentation(alg, color);
    return superpixel;
  }


}
