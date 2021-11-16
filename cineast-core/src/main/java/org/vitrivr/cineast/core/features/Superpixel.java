package org.vitrivr.cineast.core.features;

import boofcv.abst.segmentation.ImageSuperpixels;
import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.alg.segmentation.ComputeRegionMeanColor;
import boofcv.alg.segmentation.ImageSegmentationOps;
import boofcv.factory.segmentation.FactorySegmentationAlg;
import boofcv.gui.ListDisplayPanel;
import boofcv.gui.feature.VisualizeRegions;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.feature.ColorQueue_F32;
import boofcv.struct.image.GrayS32;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageType;
import java.awt.image.BufferedImage;
import org.ddogleg.struct.FastQueue;
import org.ddogleg.struct.GrowQueue_I32;

public class Superpixel {

  /**
   * Code source: https://boofcv.org/index.php?title=Example_Superpixels
   * Segments and visualizes the image
   */
  public static <T extends ImageBase<T>>
  BufferedImage performSegmentation(ImageSuperpixels<T> alg, T color) {
    // Segmentation often works better after blurring the image. Reduces high frequency image components which
    // can cause over segmentation
    GBlurImageOps.gaussian(color, color, 0.5, -1, null);

    // Storage for segmented image. Each pixel will be assigned a label from 0 to N-1, where N is the number
    // of segments in the image
    GrayS32 pixelToSegment = new GrayS32(color.width, color.height);

    // Segmentation magic happens here
    alg.segment(color, pixelToSegment);

    // Displays the results
    return visualize(pixelToSegment, color, alg.getTotalSuperpixels());
  }

  /**
   * Code source: https://boofcv.org/index.php?title=Example_Superpixels
   *
   * Don't need visualization, just computation of Buffered Image!
   *
   * Visualizes results three ways. 1) Colorized segmented image where each region is given a random
   * color. 2) Each pixel is assigned the mean color through out the region. 3) Black pixels
   * represent the border between regions.
   */
  public static <T extends ImageBase<T>>
  BufferedImage visualize(GrayS32 pixelToRegion, T color, int numSegments) {
    // Computes the mean color inside each region
    ImageType<T> type = color.getImageType();
    ComputeRegionMeanColor<T> colorize = FactorySegmentationAlg.regionMeanColor(type);

    FastQueue<float[]> segmentColor = new ColorQueue_F32(type.getNumBands());
    segmentColor.resize(numSegments);

    GrowQueue_I32 regionMemberCount = new GrowQueue_I32();
    regionMemberCount.resize(numSegments);

    ImageSegmentationOps.countRegionPixels(pixelToRegion, numSegments, regionMemberCount.data);
    colorize.process(color, pixelToRegion, regionMemberCount, segmentColor);

    // Draw each region using their average color
    BufferedImage outColor = VisualizeRegions.regionsColor(pixelToRegion, segmentColor, null);
    // Draw each region by assigning it a random color

    // Don't need GUI to shoe images with superpixels

    /*BufferedImage outSegments = VisualizeRegions.regions(pixelToRegion, numSegments, null);

    // Make region edges appear red
    BufferedImage outBorder = new BufferedImage(color.width, color.height,
        BufferedImage.TYPE_INT_RGB);
    ConvertBufferedImage.convertTo(color, outBorder, true);
    VisualizeRegions.regionBorders(pixelToRegion, 0xFF0000, outBorder);

    // Show the visualization results
    ListDisplayPanel gui = new ListDisplayPanel();
    gui.addImage(outColor, "Color of Segments");
    gui.addImage(outBorder, "Region Borders");
    gui.addImage(outSegments, "Regions");
    ShowImages.showWindow(gui, "Superpixels", true);*/
    return outColor;
  }

}
