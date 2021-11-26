package org.vitrivr.cineast.core.features;

import boofcv.abst.segmentation.ImageSuperpixels;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.factory.segmentation.ConfigFh04;
import boofcv.factory.segmentation.FactoryImageSegmentation;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageInterleaved;
import boofcv.struct.image.ImageMultiBand;
import boofcv.struct.image.ImageType;
import boofcv.struct.image.InterleavedF32;
import boofcv.struct.image.InterleavedInteger;
import boofcv.struct.image.InterleavedU8;
import boofcv.struct.image.Planar;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graalvm.compiler.replacements.nodes.BitCountNode;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
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
import sun.jvm.hotspot.utilities.BitMap;

public class MedianColorSuperpixelWatershed extends AbstractFeatureModule {

  private static final Logger LOGGER = LogManager.getLogger();

  private final CacheConfig cacheConfig = new CacheConfig("AUTOMATIC", ".");
  private final CachedDataFactory factory = new CachedDataFactory(cacheConfig);

  public MedianColorSuperpixelWatershed() {
    super("features_MedianColorSuperpixelWatershed", 196f / 4f, 3);
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
    image = ConvertBufferedImage.stripAlphaChannel(image);
    BufferedImage image2 = segmentContainer.getMedianImg().getBufferedImage();
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
