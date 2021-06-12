package org.vitrivr.cineast.core.iiif.imageapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Interface defining the common functionality implemented by ImageInformation objects of every version of the Image API
 */
public interface ImageInformation {

  /** String present at 0th index of {@link ImageInformation#profile} indicating that the server supports Image API version 2.1.1 **/
  private final String IMAGE_API_VERSION_2_1_1 = "http://iiif.io/api/image/2/level2.json";
  /** String present at 0th index of {@link ImageInformation#profile} indicating that the server supports Image API version 3.0 **/
  private final String IMAGE_API_VERSION_3_0 = "http://iiif.io/api/image/3/level1.json";

  /**
   * @param feature String denoting a feature whose support needs to be checked
   * @return false if server has advertised it's supported features and the doesn't include this specific feature
   */
  boolean isFeatureSupported(String feature);

  /**
   * @param quality String denoting a quality whose support needs to be checked
   * @return false if server has advertised it's supported qualities and the doesn't include this specific quality
   */
  boolean isQualitySupported(String quality);

  /**
   * @param format String denoting a format whose support needs to be checked
   * @return false if server has advertised it's supported formats and the doesn't include this specific format
   */
  boolean isFormatSupported(String format);

  /**
   * Get the actual width of the image
   */
  long getWidth();

  /**
   * Get the actual height of the image
   */
  long getHeight();

  /**
   * Get the {@link ImageApiVersion} of the ImageInformation
   */
  ImageApiVersion getImageApiVersion();

  /**
   * Get the max width supported by server
   */
  Long getMaxWidth();

  /**
   * Get the max height supported by server
   */
  Long getMaxHeight();

  public static IMAGE_API_VERSION getImageApiVersionNumeric(String input) {
    if (input.equals("2.1.1")) {
      return IMAGE_API_VERSION.TWO_POINT_ONE_POINT_ONE;
    } else if (input.equals("3.0") || input.equals("3.0.0")) {
      return IMAGE_API_VERSION.THREE_POINT_ZERO;
    }
    return IMAGE_API_VERSION.TWO_POINT_ONE_POINT_ONE;
  }

  /**
   * Get the max area supported by server
   */
  Long getMaxArea();


  public IMAGE_API_VERSION getImageApiVersion() {
    String apiLevelString = this.getProfile().first;
    switch (apiLevelString) {
      case IMAGE_API_VERSION_2_1_1:
        return IMAGE_API_VERSION.TWO_POINT_ONE_POINT_ONE;
      case IMAGE_API_VERSION_3_0:
        return IMAGE_API_VERSION.THREE_POINT_ZERO;
      default:
        return IMAGE_API_VERSION.TWO_POINT_ONE_POINT_ONE;
    }
  }

  /**
   * @param feature String denoting a feature whose support needs to be checked
   * @return true if server supports the feature
   */
  public boolean isFeatureSupported(String feature) {
    List<ProfileItem> profiles = this.getProfile().second;
    return profiles.stream().anyMatch(item -> {
      List<String> supports = item.getSupports();
      if (supports == null) {
        throw new NullPointerException("The server has not advertised the features supported by it");
      }
      return supports.stream().anyMatch(q -> q.equals(feature));
    });
  }

  /**
   * Enum to hold the various Image Api specification versions supported by the builder
   */
  public enum IMAGE_API_VERSION {
    TWO_POINT_ONE_POINT_ONE,
    THREE_POINT_ZERO
  }

  /**
   * Inner class used to parse the Image Information JSON response.
   */
  class SizesItem {

    @JsonProperty
    public Long width;

    @JsonProperty
    public Long height;

    public SizesItem(long width, long height) {
      this.width = width;
      this.height = height;
    }

    public SizesItem() {
    }

    @Override
    public String toString() {
      return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (!(o instanceof ImageInformation.SizesItem)) {
        return false;
      }
      final ImageInformation.SizesItem other = (ImageInformation.SizesItem) o;
      if (!other.canEqual(this)) {
        return false;
      }
      final Object this$width = this.getWidth();
      final Object other$width = other.getWidth();
      if (!Objects.equals(this$width, other$width)) {
        return false;
      }
      final Object this$height = this.getHeight();
      final Object other$height = other.getHeight();
      return Objects.equals(this$height, other$height);
    }

    protected boolean canEqual(final Object other) {
      return other instanceof ImageInformation.SizesItem;
    }

    public Long getWidth() {
      return this.width;
    }

    @JsonProperty
    public void setWidth(final Long width) {
      this.width = width;
    }

    public Long getHeight() {
      return this.height;
    }

    @JsonProperty
    public void setHeight(final Long height) {
      this.height = height;
    }
  }


  /**
   * Inner class used to parse the Image Information JSON response.
   */
  class TilesItem {

    @JsonProperty(required = true)
    public long width;
    @JsonProperty
    public long height;
    @JsonProperty(required = true)
    public List<Integer> scaleFactors;

    public TilesItem() {
    }

    @Override
    public String toString() {
      return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public boolean equals(final Object o) {
      if (o == this) {
        return true;
      }
      if (!(o instanceof ImageInformation.TilesItem)) {
        return false;
      }
      final ImageInformation.TilesItem other = (ImageInformation.TilesItem) o;
      if (!other.canEqual(this)) {
        return false;
      }
      if (this.getWidth() != other.getWidth()) {
        return false;
      }
      if (this.getHeight() != other.getHeight()) {
        return false;
      }
      final Object this$scaleFactors = this.getScaleFactors();
      final Object other$scaleFactors = other.getScaleFactors();
      return Objects.equals(this$scaleFactors, other$scaleFactors);
    }

    protected boolean canEqual(final Object other) {
      return other instanceof ImageInformation.TilesItem;
    }

    public long getWidth() {
      return this.width;
    }

    @JsonProperty(required = true)
    public void setWidth(final long width) {
      this.width = width;
    }

    public long getHeight() {
      return this.height;
    }

    @JsonProperty
    public void setHeight(final long height) {
      this.height = height;
    }

    public List<Integer> getScaleFactors() {
      return this.scaleFactors;
    }

    @JsonProperty(required = true)
    public void setScaleFactors(final List<Integer> scaleFactors) {
      this.scaleFactors = scaleFactors;
    }
  }
}
