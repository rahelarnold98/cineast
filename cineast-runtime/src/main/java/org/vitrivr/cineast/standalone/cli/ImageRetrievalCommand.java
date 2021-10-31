package org.vitrivr.cineast.standalone.cli;


import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import java.util.ArrayList;
import java.util.List;
import org.vitrivr.cineast.core.data.query.containers.ImageQueryTermContainer;
import org.vitrivr.cineast.core.features.AudioTranscriptionSearch;
import org.vitrivr.cineast.core.features.AverageColorARP44;
import org.vitrivr.cineast.core.features.AverageColorARP44Normalized;
import org.vitrivr.cineast.core.features.AverageColorGrid8;
import org.vitrivr.cineast.core.features.AverageColorGrid8Normalized;
import org.vitrivr.cineast.core.features.AverageColorRaster;
import org.vitrivr.cineast.core.features.CLD;
import org.vitrivr.cineast.core.features.CLDNormalized;
import org.vitrivr.cineast.core.features.DescriptionTextSearch;
import org.vitrivr.cineast.core.features.MedianColorGrid8;
import org.vitrivr.cineast.core.features.OCRSearch;
import org.vitrivr.cineast.core.features.SubDivMedianFuzzyColor;
import org.vitrivr.cineast.core.features.SubtitleFulltextSearch;
import org.vitrivr.cineast.core.features.TagsFtSearch;
import org.vitrivr.cineast.core.features.retriever.Retriever;
import org.vitrivr.cineast.standalone.config.Config;
import org.vitrivr.cineast.standalone.util.ContinuousRetrievalLogic;

@Command(name = "retrieve-image", description = "Retrieves objects from the database using an image as query input.")
public class ImageRetrievalCommand implements Runnable {

  @Option(name = {"--image"}, title = "image input", description = "base 64 encoded image data to be used for retrieval.")
  private String image;

  @Option(name = {"--limit"}, title = "limit results", description = "Only show the first n results.")
  private Integer limit = 1;

  @Option(name = {"--detail"}, title = "detailed results", description = "also list detailed results for retrieved segments.")
  private Boolean printDetail = false;

  public void run() {
    final ContinuousRetrievalLogic retrieval = new ContinuousRetrievalLogic(Config.sharedConfig().getDatabase());
    System.out.println("Querying for image " + image);
    ImageQueryTermContainer qc = new ImageQueryTermContainer(image);
    List<Retriever> retrievers = new ArrayList<>();
    retrievers.add(new AverageColorARP44());
    retrievers.add(new AverageColorARP44Normalized());
    retrievers.add(new SubDivMedianFuzzyColor());
    retrievers.add(new AverageColorGrid8());
    retrievers.add(new AverageColorGrid8Normalized());
    retrievers.add(new CLD());
    retrievers.add(new CLDNormalized());
    retrievers.add(new MedianColorGrid8());
    retrievers.add(new AverageColorRaster());
    CliUtils.retrieveAndLog(retrievers, retrieval, limit, printDetail, qc);
    System.out.println("Done");
  }
}

