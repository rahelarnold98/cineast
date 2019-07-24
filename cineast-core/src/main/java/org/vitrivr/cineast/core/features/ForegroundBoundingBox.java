package org.vitrivr.cineast.core.features;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.data.FloatVector;
import org.vitrivr.cineast.core.data.FloatVectorImpl;
import org.vitrivr.cineast.core.data.Pair;
import org.vitrivr.cineast.core.data.ReadableFloatVector;
import org.vitrivr.cineast.core.data.score.ScoreElement;
import org.vitrivr.cineast.core.data.segments.SegmentContainer;
import org.vitrivr.cineast.core.db.PersistencyWriterSupplier;
import org.vitrivr.cineast.core.db.PersistentTuple;
import org.vitrivr.cineast.core.features.abstracts.AbstractFeatureModule;
import org.vitrivr.cineast.core.db.setup.AttributeDefinition;
import org.vitrivr.cineast.core.db.setup.AttributeDefinition.AttributeType;
import org.vitrivr.cineast.core.db.setup.EntityCreator;
import org.vitrivr.cineast.core.util.MaskGenerator;
import org.vitrivr.cineast.core.util.TimeHelper;

public class ForegroundBoundingBox extends AbstractFeatureModule {

  public ForegroundBoundingBox() {
    super("features_ForegroundBoundingBox", 0.5f, 4);
  }

  private static final Logger LOGGER = LogManager.getLogger();

  @Override
  public void processSegment(SegmentContainer shot) {
    TimeHelper.tic();
    LOGGER.traceEntry();
    if (!phandler.idExists(shot.getId())) {
      ArrayList<Pair<Long, ArrayList<Float>>> bboxs = MaskGenerator
          .getNormalizedBbox(shot.getVideoFrames(), shot.getPaths(), shot.getBgPaths());
      for (Pair<Long, ArrayList<Float>> bbox : bboxs) {
        FloatVectorImpl fv = new FloatVectorImpl(bbox.second);
        persist(shot.getId(), bbox.first, fv);
      }
      LOGGER.debug("ForegroundBoundingBox.processShot() done in {}",
          TimeHelper.toc());
    }
    LOGGER.traceExit();
  }

  @Override
  public List<ScoreElement> getSimilar(SegmentContainer sc, ReadableQueryConfig qc) {
    ArrayList<Pair<Long, ArrayList<Float>>> bboxs = MaskGenerator
        .getNormalizedBbox(sc.getVideoFrames(), sc.getPaths(), sc.getBgPaths());
    FloatVectorImpl fv = new FloatVectorImpl(bboxs.get(0).second);

    return getSimilar(ReadableFloatVector.toArray(fv), qc);
  }

  private float[] arrayCache = null;

  protected void persist(String shotId, long frameIdx, FloatVector fs) {
    PersistentTuple tuple = this.phandler
        .generateTuple(shotId, frameIdx, arrayCache = ReadableFloatVector
            .toArray(fs, arrayCache));
    this.phandler.persist(tuple);
  }

  @Override
  public void initalizePersistentLayer(Supplier<EntityCreator> supply) {
    supply.get().createFeatureEntity("features_ForegroundBoundingBox", false,
        new AttributeDefinition("frame", AttributeType.LONG),
        new AttributeDefinition("bbox", AttributeType.VECTOR));
  }

  @Override
  public void init(PersistencyWriterSupplier phandlerSupply) {
    super.init(phandlerSupply);
    this.phandler.setFieldNames("id", "frame", "bbox");
  }
}
