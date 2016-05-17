package ch.unibas.cs.dbis.cineast.core.features;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.unibas.cs.dbis.cineast.core.color.ColorConverter;
import ch.unibas.cs.dbis.cineast.core.color.FuzzyColorHistogramQuantizer;
import ch.unibas.cs.dbis.cineast.core.color.ReadableLabContainer;
import ch.unibas.cs.dbis.cineast.core.config.QueryConfig;
import ch.unibas.cs.dbis.cineast.core.data.FloatVector;
import ch.unibas.cs.dbis.cineast.core.data.FloatVectorImpl;
import ch.unibas.cs.dbis.cineast.core.data.MultiImage;
import ch.unibas.cs.dbis.cineast.core.data.Pair;
import ch.unibas.cs.dbis.cineast.core.data.SegmentContainer;
import ch.unibas.cs.dbis.cineast.core.data.StringDoublePair;
import ch.unibas.cs.dbis.cineast.core.db.PersistencyWriter;
import ch.unibas.cs.dbis.cineast.core.util.ColorUtils;
import ch.unibas.cs.dbis.cineast.core.util.GridPartitioner;

public class MedianColorRaster extends AverageColorRaster {

	private static final Logger LOGGER = LogManager.getLogger();
	
	@Override
	public void init(PersistencyWriter<?> phandler) {
		this.phandler = phandler;
		this.phandler.open("features_MedianColorRaster");
		this.phandler.setFieldNames("id", "raster", "hist");
	}

	@Override
	public void processShot(SegmentContainer shot) {
		LOGGER.entry();
		if (!phandler.idExists(shot.getId())) {
			MultiImage med = shot.getMedianImg();
			int[] colors = med.getColors();
			ArrayList<Integer> ints = new ArrayList<>(colors.length);
			for(int i : colors){
				ints.add(i);
			}
			ArrayList<LinkedList<Integer>> partitions = GridPartitioner.partition(ints, med.getWidth(), med.getHeight(), 8, 8);
			
			float[] raster = new float[64];
			float[] hist = new float[15];
			
			for(int i = 0; i < 64; ++i){
				LinkedList<Integer> list = partitions.get(i);
				int col = ColorUtils.getAvg(list);
				ReadableLabContainer lab = ColorConverter.cachedRGBtoLab(col);
				raster[i] = get(FuzzyColorHistogramQuantizer.quantize(lab));
				hist[(int)raster[i]]++;
			}
			
			persist(shot.getId(), new FloatVectorImpl(hist), new FloatVectorImpl(raster));
			
		}
		LOGGER.exit();
	}
	
	private Pair<FloatVector, float[]> computeGrid(SegmentContainer qc){
		MultiImage med = qc.getMedianImg();
		int[] colors = med.getColors();
		ArrayList<Integer> ints = new ArrayList<>(colors.length);
		for(int i : colors){
			ints.add(i);
		}
		ArrayList<LinkedList<Integer>> partitions = GridPartitioner.partition(ints, med.getWidth(), med.getHeight(), 8, 8);
		
		float[] queryraster = new float[64];
		float[] hist = new float[15];
		
		for(int i = 0; i < 64; ++i){
			LinkedList<Integer> list = partitions.get(i);
			int col = ColorUtils.getAvg(list);
			ReadableLabContainer lab = ColorConverter.cachedRGBtoLab(col);
			queryraster[i] = get(FuzzyColorHistogramQuantizer.quantize(lab));
			hist[(int)queryraster[i]]++;
		}
		
		FloatVector query = new FloatVectorImpl(hist);
		
		return new Pair<FloatVector, float[]>(query, queryraster);
	}

	@Override
	public List<StringDoublePair> getSimilar(SegmentContainer sc, QueryConfig qc) {
		// TODO Auto-generated method stub
		return super.getSimilar(sc, qc);
	}

	@Override
	public List<StringDoublePair> getSimilar(String shotId, QueryConfig qc) {
		// TODO Auto-generated method stub
		return super.getSimilar(shotId, qc);
	}
	
//	@Override
//	public List<LongDoublePair> getSimilar(SegmentContainer qc) {
//		Pair<FloatVector, float[]> pair = computeGrid(qc);
//		
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule() * 5;
//
//		ResultSet rset = this.selector.select("SELECT * FROM features.MedianColorRaster USING DISTANCE MINKOWSKI(2)(\'" + pair.first.toFeatureString() + "\', hist) ORDER USING DISTANCE LIMIT " + limit);
//		ArrayList<LongDoublePair> result = new ArrayList<>(limit);
//		if(rset != null){
//			try {
//				while(rset.next()){
//					long shotId = rset.getLong(2);
//					String rasterFromDB = rset.getString(4);
//
//					float[] raster = stringToFloatArray(rasterFromDB);
//					
//					result.add(new LongDoublePair(shotId, register(pair.second, raster)));
//				}
//			} catch (SQLException e) {
//				LOGGER.fatal(LogHelper.SQL_MARKER, LogHelper.getStackTrace(e));
//			}
//		}
//		return result;
//
//	}
//	
//	@Override
//	public List<LongDoublePair> getSimilar(SegmentContainer qc, String resultCacheName) {
//		Pair<FloatVector, float[]> pair = computeGrid(qc);
//		
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule() * 5;
//
//		ResultSet rset = this.selector.select(getResultCacheLimitSQL(resultCacheName) + " SELECT * FROM features.MedianColorRaster, c WHERE shotid = c.filter USING DISTANCE MINKOWSKI(2)(\'" + pair.first.toFeatureString() + "\', hist) ORDER USING DISTANCE LIMIT " + limit);
//		ArrayList<LongDoublePair> result = new ArrayList<>(limit);
//		if(rset != null){
//			try {
//				while(rset.next()){
//					long shotId = rset.getLong(2);
//					String rasterFromDB = rset.getString(4);
//
//					float[] raster = stringToFloatArray(rasterFromDB);
//					
//					result.add(new LongDoublePair(shotId, register(pair.second, raster)));
//				}
//			} catch (SQLException e) {
//				LOGGER.fatal(LogHelper.SQL_MARKER, LogHelper.getStackTrace(e));
//			}
//		}
//		return result;
//	}
//
//	public List<LongDoublePair> getSimilar(long shotId) {
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule() * 5;
//		
//		ResultSet rset = this.selector.select("WITH q AS (SELECT hist, raster FROM features.MedianColorRaster WHERE shotid = " + shotId + ") SELECT shotid, MedianColorRaster.raster, q.raster as queryraster FROM features.MedianColorRaster, q USING DISTANCE MINKOWSKI(2)(q.hist, MedianColorRaster.hist) ORDER USING DISTANCE LIMIT " + limit);
//		ArrayList<LongDoublePair> result = new ArrayList<>(limit);
//		if(rset != null){
//			try {
//				while(rset.next()){
//					long id = rset.getLong(2);
//					String rasterFromDB = rset.getString(3);
//					float[] raster = stringToFloatArray(rasterFromDB);
//					
//					rasterFromDB = rset.getString(4);
//					float[] queryraster = stringToFloatArray(rasterFromDB);
//					
//					result.add(new LongDoublePair(id, register(queryraster, raster)));
//				}
//			} catch (SQLException e) {
//				LOGGER.fatal(LogHelper.SQL_MARKER, LogHelper.getStackTrace(e));
//			}
//		}
//		return result;
//	}
//	
//	public List<LongDoublePair> getSimilar(long shotId, String resultCacheName) {
//		int limit = Config.getRetrieverConfig().getMaxResultsPerModule() * 5;
//		
//		ResultSet rset = this.selector.select(getResultCacheLimitSQL(resultCacheName) + " , q AS (SELECT hist, raster FROM features.MedianColorRaster WHERE shotid = " + shotId + ") SELECT shotid, MedianColorRaster.raster, q.raster as queryraster FROM features.MedianColorRaster, q , c WHERE shotid = c.filter USING DISTANCE MINKOWSKI(2)(q.hist, MedianColorRaster.hist) ORDER USING DISTANCE LIMIT " + limit);
//		ArrayList<LongDoublePair> result = new ArrayList<>(limit);
//		if(rset != null){
//			try {
//				while(rset.next()){
//					long id = rset.getLong(2);
//					String rasterFromDB = rset.getString(3);
//					float[] raster = stringToFloatArray(rasterFromDB);
//					
//					rasterFromDB = rset.getString(4);
//					float[] queryraster = stringToFloatArray(rasterFromDB);
//					
//					result.add(new LongDoublePair(id, register(queryraster, raster)));
//				}
//			} catch (SQLException e) {
//				LOGGER.fatal(LogHelper.SQL_MARKER, LogHelper.getStackTrace(e));
//			}
//		}
//		return result;
//	}
}
