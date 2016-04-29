package ch.unibas.cs.dbis.cineast.core.features;

import java.util.List;

import ch.unibas.cs.dbis.cineast.core.config.QueryConfig;
import ch.unibas.cs.dbis.cineast.core.data.LongDoublePair;
import ch.unibas.cs.dbis.cineast.core.data.SegmentContainer;
import ch.unibas.cs.dbis.cineast.core.features.abstracts.MotionHistogramCalculator;

public class SubDivMotionSum5 extends MotionHistogramCalculator {

	
	public SubDivMotionSum5() {
		super("features.SubDivMotionHistogram5", 2500);
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
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule();
//		
//		Pair<List<Double>, ArrayList<ArrayList<Float>>> pair = getSubDivHist(5, qc.getPaths());
//
//		FloatVectorImpl fv = new FloatVectorImpl(pair.first);
//		
//		ResultSet rset = this.selector.select("SELECT * FROM features.SubDivMotionHistogram5 USING DISTANCE MINKOWSKI(2)(\'" + fv.toFeatureString() + "\', sums) ORDER USING DISTANCE LIMIT " + limit);
//		return manageResultSet(rset);
//	}
//
//	@Override
//	public List<LongDoublePair> getSimilar(SegmentContainer qc, String resultCacheName) {
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule();
//		
//		Pair<List<Double>, ArrayList<ArrayList<Float>>> pair = getSubDivHist(5, qc.getPaths());
//
//		FloatVectorImpl fv = new FloatVectorImpl(pair.first);
//		
//		ResultSet rset = this.selector.select(getResultCacheLimitSQL(resultCacheName) + " SELECT * FROM features.SubDivMotionHistogram5, c WHERE shotid = c.filter USING DISTANCE MINKOWSKI(2)(\'" + fv.toFeatureString() + "\', sums) ORDER USING DISTANCE LIMIT " + limit);
//		return manageResultSet(rset);
//	}


}
