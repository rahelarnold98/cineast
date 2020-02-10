package org.vitrivr.cineast.core.extraction.decode.image;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.config.DecoderConfig;
import org.vitrivr.cineast.core.config.CacheConfig;
import org.vitrivr.cineast.core.extraction.decode.general.Decoder;
import org.vitrivr.cineast.core.util.MimeTypeHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Decoder for sequence of images in a single folder that, in terms of Cineast's data model, belong together.
 *
 * @author rgasser
 * @version 1.0
 */
public class ImageSequenceDecoder implements Decoder<ImageSequence> {

  /** Default logging facility. */
  private static final Logger LOGGER = LogManager.getLogger();

  private static final Set<String> SUPPORTED = new HashSet<>();
  static {
    SUPPORTED.add("application/octet-stream");
  }

  /** {@link DefaultImageDecoder} instance used for actual image decoding. */
  private final DefaultImageDecoder imageDecoder = new DefaultImageDecoder();

  private final DirectoryStream.Filter<Path> filter = file -> {
    if (Files.isRegularFile(file) && imageDecoder.supportedFiles().contains(MimeTypeHelper.getContentType(file))){
      return true;
    }
    return false;
  };

  /** Path to the folder that contains the next {@link ImageSequence}. */
  private Path path;

  /** {@link DecoderConfig} instance to use. */
  private DecoderConfig decoderConfig;

  /** {@link CacheConfig} instance to use. */
  private CacheConfig cacheConfig;

  /**
   * Initializes the decoder with a file. This is a necessary step before content can be retrieved from
   * the decoder by means of the getNext() method.
   *
   * @param path Path to the file that should be decoded.
   * @param decoderConfig {@link DecoderConfig} used by this {@link Decoder}.
   * @param cacheConfig The {@link CacheConfig} used by this {@link Decoder}
   * @return True if initialization was successful, false otherwise.
   */
  @Override
  public boolean init(Path path, DecoderConfig decoderConfig, CacheConfig cacheConfig) {
    this.path = path;
    this.decoderConfig = decoderConfig;
    this.cacheConfig = cacheConfig;
    return true;
  }

  @Override
  public void close() { }

  @Override
  public ImageSequence getNext() {
    if (this.path == null) {
      throw new IllegalStateException("Cannot invoke getNext() on ImageSequenceDecoder that has completed.");
    }
    final ImageSequence sequence = new ImageSequence(this.path.getFileName().toString());
    if (this.path != null) {
      if (Files.isDirectory(path)) {
        try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, this.filter)){
          for (Path p: directoryStream){
            this.imageDecoder.init(p, this.decoderConfig, this.cacheConfig);
            sequence.add(this.imageDecoder.getNext());
          }
        } catch (IOException e) {
          LOGGER.fatal("A severe error occurred while trying to decode an image file for image sequence sequence '{}'.", this.path.getFileName());
        }
      }
    }
    this.path = null;
    return sequence;
  }

  @Override
  public int count() {
    if (this.path != null) {
      return 1;
    } else {
      return 0;
    }
  }

  @Override
  public boolean complete() {
    return this.path == null;
  }

  @Override
  public Set<String> supportedFiles() {
    return SUPPORTED;
  }

  @Override
  public boolean canBeReused() {
    return false;
  }
}
