package org.vitrivr.cineast.core.extraction.segmenter.image;

import org.vitrivr.cineast.core.data.entities.MediaObjectDescriptor;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.extraction.ExtractionContextProvider;
import org.vitrivr.cineast.core.extraction.decode.general.Decoder;
import org.vitrivr.cineast.core.extraction.segmenter.general.Segmenter;

import java.awt.image.BufferedImage;

public class ImageSequenceSegmenter implements Segmenter<BufferedImage> { //TODO implement

  public ImageSequenceSegmenter(ExtractionContextProvider context){

  }

  @Override
  public void init(Decoder<BufferedImage> decoder, MediaObjectDescriptor object) {

  }

  @Override
  public SegmentContainer getNext() throws InterruptedException {
    return null;
  }

  @Override
  public boolean complete() {
    return false;
  }

  @Override
  public void close() {

  }

  @Override
  public void run() {

  }
}
