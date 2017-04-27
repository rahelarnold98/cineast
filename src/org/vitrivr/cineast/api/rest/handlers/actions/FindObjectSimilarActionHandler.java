package org.vitrivr.cineast.api.rest.handlers.actions;

import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.vitrivr.cineast.api.rest.handlers.abstracts.ParsingActionHandler;
import org.vitrivr.cineast.core.config.Config;
import org.vitrivr.cineast.core.config.QueryConfig;
import org.vitrivr.cineast.core.data.StringDoublePair;
import org.vitrivr.cineast.core.data.messages.query.SimilarityQuery;
import org.vitrivr.cineast.core.data.messages.query.QueryComponent;
import org.vitrivr.cineast.core.data.messages.query.QueryTerm;
import org.vitrivr.cineast.core.data.query.containers.QueryContainer;
import org.vitrivr.cineast.core.data.score.SegmentScoreElement;
import org.vitrivr.cineast.core.util.ContinuousRetrievalLogic;

/**
 * @author rgasser
 * @version 1.0
 * @created 11.01.17
 */
public class FindObjectSimilarActionHandler extends ParsingActionHandler<SimilarityQuery> {
  @Override
  public Object invoke(SimilarityQuery similarityQuery, Map<String, String> parameters) {

    /*
    * Prepare map that maps category to QueryTerm components.
      */
    HashMap<String, ArrayList<QueryContainer>> categoryMap = QueryComponent.toCategoryMap(similarityQuery.getComponents());

  /*
   * Execute similarity queries for all Category -> QueryContainer combinations in the map.
   */
    QueryConfig qconf = QueryConfig.newQueryConfigFromOther(Config.sharedConfig().getQuery());
    for (String category : categoryMap.keySet()) {
      TObjectDoubleHashMap<String> scoreBySegmentId = new TObjectDoubleHashMap<>();
      for (QueryContainer qc : categoryMap.get(category)) {
        float weight = qc.getWeight() > 0f ? 1f : -1f; //TODO better normalisation

        List<SegmentScoreElement> scoreResults;
        if (qc.hasId()) {
          scoreResults = ContinuousRetrievalLogic.retrieve(qc.getId(), category, qconf);
        } else {
          scoreResults = ContinuousRetrievalLogic.retrieve(qc, category, qconf);
        }

        for (SegmentScoreElement element : scoreResults) {
          String segmentId = element.getSegmentId();
          double score = element.getScore();
          if (Double.isInfinite(score) || Double.isNaN(score)) {
            continue;
          }
          double weightedScore = score * weight;
          scoreBySegmentId.adjustOrPutValue(segmentId, weightedScore, weightedScore);
        }

        final List<StringDoublePair> list = new ArrayList<>(scoreBySegmentId.size());
        scoreBySegmentId.forEachEntry((segmentId, score) -> {
          if (score > 0) {
            list.add(new StringDoublePair(segmentId, score));
          }
          return true;
        });

        Collections.sort(list, StringDoublePair.COMPARATOR);

        final int MAX_RESULTS = Config.sharedConfig().getRetriever().getMaxResults();
        List<StringDoublePair> resultList = list;
        if (list.size() > MAX_RESULTS) {
          resultList = resultList.subList(0, MAX_RESULTS);
        }
      }
    }

    return null;
  }

  @Override
  public Class<SimilarityQuery> inClass() {
    return SimilarityQuery.class;
  }
}
