package org.vitrivr.cineast.core.iiif.imageapi.v2_1_1;

import org.vitrivr.cineast.core.iiif.imageapi.ImageRequest;

/**
 * @author singaltanmay
 * @version 1.0
 * @created 09.06.21
 */
public interface ImageRequestBuilder_v2_1_1 {

  String SIZE_MAX = "max";
  String SIZE_PERCENTAGE = "pct:";
  String SIZE_FULL = "full";

  /** The image or region is not scaled, and is returned at its full size. */
  ImageRequestBuilder_v2_1_1 setSizeFull();

  /**
   * The complete image is returned, without any cropping.
   *
   * @return this {@link ImageRequestBuilder_v2_1_1}
   */
  ImageRequestBuilder_v2_1_1 setRegionFull();

  /**
   * The region is defined as an area where the width and height are both equal to the length of the shorter dimension of the complete image. The region may be positioned anywhere in the longer dimension of the image content at the server’s discretion, and centered is often a reasonable default.
   *
   * @return this {@link ImageRequestBuilder_v2_1_1}
   */
  ImageRequestBuilder_v2_1_1 setRegionSquare();

  /**
   * The region of the full image to be returned is specified in terms of absolute pixel values.
   *
   * @param x Represents the number of pixels from the 0 position on the horizontal axis
   * @param y Represents the number of pixels from the 0 position on the vertical axis
   * @param w Represents the width of the region
   * @param h Represents the height of the region in pixels
   * @return this {@link ImageRequestBuilder_v2_1_1}
   */
  ImageRequestBuilder_v2_1_1 setRegionAbsolute(float x, float y, float w, float h);

  /**
   * The region to be returned is specified as a sequence of percentages of the full image’s dimensions, as reported in the image information document.
   *
   * @param x Represents the number of pixels from the 0 position on the horizontal axis, calculated as a percentage of the reported width
   * @param y Represents the number of pixels from the 0 position on the vertical axis, calculated as a percentage of the reported height
   * @param w Represents the width of the region, calculated as a percentage of the reported width
   * @param h Represents the height of the region, calculated as a percentage of the reported height
   * @return this {@link ImageRequestBuilder_v2_1_1}
   */
  ImageRequestBuilder_v2_1_1 setRegionPercentage(float x, float y, float w, float h);

  /**
   * The image or region is returned at the maximum size available, as indicated by maxWidth, maxHeight, maxArea in the profile description. This is the same as full if none of these properties are provided.
   *
   * @return this {@link ImageRequestBuilder_v2_1_1}
   */
  ImageRequestBuilder_v2_1_1 setSizeMax();

  /**
   * Returns an image scaled to the exact dimensions given in the parameters. If only height or width are provided then image is scaled to that dimension while maintaining the aspect ratio. If both height and width are given then image is scaled to those dimensions by ignoring the aspect ratio.
   *
   * @return this {@link ImageRequestBuilder_v2_1_1}
   * @throws IllegalArgumentException If both height and width are undefined then an IllegalArgumentException is thrown
   */
  ImageRequestBuilder_v2_1_1 setSizeScaledExact(Float width, Float height) throws IllegalArgumentException;

  /**
   * The image content is scaled for the best fit such that the resulting width and height are less than or equal to the requested width and height. The exact scaling may be determined by the service provider, based on characteristics including image quality and system performance. The dimensions of the returned image content are calculated to maintain the aspect ratio of the extracted region.
   *
   * @return this {@link ImageRequestBuilder_v2_1_1_Impl}
   * @throws IllegalArgumentException Behaviour of server when both width and height are overridable is undefined
   */
  ImageRequestBuilder_v2_1_1 setSizeScaledBestFit(float width, float height, boolean isWidthOverridable, boolean isHeightOverridable) throws IllegalArgumentException;

  /**
   * The width and height of the returned image is scaled to n% of the width and height of the extracted region. The aspect ratio of the returned image is the same as that of the extracted region.
   *
   * @return this {@link ImageRequestBuilder_v2_1_1}
   */
  ImageRequestBuilder_v2_1_1 setSizePercentage(float n);

  /**
   * This method is used to specify the mirroring and rotation applied to the image.
   *
   * @param degree Represents the number of degrees of clockwise rotation, and may be any floating point number from 0 to 360.
   * @param mirror Indicates if that the image should be mirrored by reflection on the vertical axis before any rotation is applied.
   * @return this {@link ImageRequestBuilder_v2_1_1}
   */
  ImageRequestBuilder_v2_1_1 setRotation(float degree, boolean mirror) throws IllegalArgumentException;

  /**
   * This method is used to specify the quality of the image.
   *
   * @param quality The quality of the image
   * @return this {@link ImageRequestBuilder_v2_1_1}
   */
  ImageRequestBuilder_v2_1_1 setQuality(String quality);

  /**
   * This method is used to specify the file extension of the image.
   *
   * @param extension The file extension of the image
   * @return this {@link ImageRequestBuilder_v2_1_1}
   */
  ImageRequestBuilder_v2_1_1 setFormat(String extension);

  /** This method builds a new ImageRequest with the parameters set using the dedicated setter methods */
  ImageRequest build();

}
