package ch.unibas.cs.dbis.cineast.core.features;

import java.util.List;

import ch.unibas.cs.dbis.cineast.core.config.QueryConfig;
import ch.unibas.cs.dbis.cineast.core.data.FloatVector;
import ch.unibas.cs.dbis.cineast.core.data.LongDoublePair;
import ch.unibas.cs.dbis.cineast.core.data.Pair;
import ch.unibas.cs.dbis.cineast.core.data.SegmentContainer;
import ch.unibas.cs.dbis.cineast.core.features.abstracts.AbstractFeatureModule;
import ch.unibas.cs.dbis.cineast.core.util.ARPartioner;
import ch.unibas.cs.dbis.cineast.core.util.ImageHistogramEqualizer;

public class AverageColorARP44Normalized extends AbstractFeatureModule {

	public AverageColorARP44Normalized() {
		super("features.AverageColorARP44Normalized", "arp", 115854f / 4f);
	}

	@Override
	public void processShot(SegmentContainer shot) {
		if(!phandler.idExists(shot.getId())){
			Pair<FloatVector, float[]> p = ARPartioner.partitionImage(ImageHistogramEqualizer.getEqualized(shot.getAvgImg()), 4, 4);
			persist(shot.getId(), p.first);
		}
	}

	@Override
	public List<LongDoublePair> getSimilar(SegmentContainer sc, QueryConfig qc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LongDoublePair> getSimilar(long shotId, QueryConfig qc) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public List<LongDoublePair> getSimilar(SegmentContainer qc) {
//		Pair<FloatVector, float[]> p = ARPartioner.partitionImage(ImageHistogramEqualizer.getEqualized(qc.getAvgImg()), 4, 4);
//		FloatVector query = p.first;
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule();
//		
//		ResultSet rset = this.selector.select("SELECT * FROM features.AverageColorARP44Normalized USING DISTANCE MINKOWSKI(1, " + formatQueryWeights(p.second) + ")(\'" + query.toFeatureString() + "\', arp) ORDER USING DISTANCE LIMIT " + limit);
//		return manageResultSet(rset);
//	}
//
//	@Override
//	public List<LongDoublePair> getSimilar(SegmentContainer qc, String resultCacheName) {
//		Pair<FloatVector, float[]> p = ARPartioner.partitionImage(ImageHistogramEqualizer.getEqualized(qc.getAvgImg()), 4, 4);
//		FloatVector query = p.first;
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule();
//		
//		ResultSet rset = this.selector.select(getResultCacheLimitSQL(resultCacheName) + "SELECT * FROM features.AverageColorARP44Normalized, c WHERE shotid = c.filter USING DISTANCE MINKOWSKI(1, " + formatQueryWeights(p.second) + ")(\'" + query.toFeatureString() + "\', arp) ORDER USING DISTANCE LIMIT " + limit);
//		return manageResultSet(rset);
//	}

}
