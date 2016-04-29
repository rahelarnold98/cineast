package ch.unibas.cs.dbis.cineast.core.features;

import java.util.ArrayList;
import java.util.List;

import ch.unibas.cs.dbis.cineast.core.config.QueryConfig;
import ch.unibas.cs.dbis.cineast.core.data.FloatVector;
import ch.unibas.cs.dbis.cineast.core.data.FloatVectorImpl;
import ch.unibas.cs.dbis.cineast.core.data.LongDoublePair;
import ch.unibas.cs.dbis.cineast.core.data.Pair;
import ch.unibas.cs.dbis.cineast.core.data.SegmentContainer;
import ch.unibas.cs.dbis.cineast.core.features.abstracts.SubDivMotionHistogram;
import ch.unibas.cs.dbis.cineast.core.util.MathHelper;

public class SubDivMotionHistogram3 extends SubDivMotionHistogram {

	public SubDivMotionHistogram3() {
		super("features.SubDivMotionHistogram3", "hists", MathHelper.SQRT2 * 9);
	}

	@Override
	public void processShot(SegmentContainer shot) {
		if(!phandler.idExists(shot.getId())){
		
			Pair<List<Double>, ArrayList<ArrayList<Float>>> pair = getSubDivHist(3, shot.getPaths());
			
			FloatVector sum = new FloatVectorImpl(pair.first);
			ArrayList<Float> tmp = new ArrayList<Float>(3 * 3 * 8);
			for(List<Float> l : pair.second){
				for(float f : l){
					tmp.add(f);
				}
			}
			FloatVectorImpl fv = new FloatVectorImpl(tmp);

			persist(shot.getId(), sum, fv);
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
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule();
//		
//		Pair<List<Double>, ArrayList<ArrayList<Float>>> pair = getSubDivHist(3, qc.getPaths());
//
//		ArrayList<Float> tmp = new ArrayList<Float>(3 * 3 * 8);
//		for(List<Float> l : pair.second){
//			for(float f : l){
//				tmp.add(f);
//			}
//		}
//		FloatVectorImpl fv = new FloatVectorImpl(tmp);
//		
//		ResultSet rset = this.selector.select("SELECT * FROM features.SubDivMotionHistogram3 USING DISTANCE MINKOWSKI(2)(\'" + fv.toFeatureString() + "\', hists) ORDER USING DISTANCE LIMIT " + limit);
//		return manageResultSet(rset);
//	}
//
//	@Override
//	public List<LongDoublePair> getSimilar(SegmentContainer qc, String resultCacheName) {
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule();
//		
//		Pair<List<Double>, ArrayList<ArrayList<Float>>> pair = getSubDivHist(3, qc.getPaths());
//
//		ArrayList<Float> tmp = new ArrayList<Float>(3 * 3 * 8);
//		for(List<Float> l : pair.second){
//			for(float f : l){
//				tmp.add(f);
//			}
//		}
//		FloatVectorImpl fv = new FloatVectorImpl(tmp);
//		
//		ResultSet rset = this.selector.select(getResultCacheLimitSQL(resultCacheName) + " SELECT * FROM features.SubDivMotionHistogram3, c WHERE shotid = c.filter USING DISTANCE MINKOWSKI(2)(\'" + fv.toFeatureString() + "\', hists) ORDER USING DISTANCE LIMIT " + limit);
//		return manageResultSet(rset);
//	}
}
