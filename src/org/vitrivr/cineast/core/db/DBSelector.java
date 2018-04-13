package org.vitrivr.cineast.core.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
import org.vitrivr.cineast.core.config.ReadableQueryConfig;
import org.vitrivr.cineast.core.data.distance.DistanceElement;
import org.vitrivr.cineast.core.data.providers.primitive.FloatArrayTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.PrimitiveTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.ProviderDataType;

public interface DBSelector {

    boolean open(String name);

    boolean close();

    /**
     * @deprecated use {@link #getNearestNeighboursGeneric(int, PrimitiveTypeProvider, String, Class, ReadableQueryConfig)} instead. This mode forces the implementing class to handle typeproviders separately and reduces code duplication.
     */
    @Deprecated
    <T extends DistanceElement> List<T> getNearestNeighbours(int k, float[] vector, String column,
                                                             Class<T> distanceElementClass, ReadableQueryConfig config);

    /**
     * * Finds the {@code k}-nearest neighbours of the given {@code queryProvider} in {@code column} using the
     * provided distance function in {@code config}. {@code ScoreElementClass} defines the specific
     * type of {@link DistanceElement} to be created internally and returned by this method.
     *
     * @param k                    maximum number of results
     * @param queryProvider        query vector
     * @param column               feature column to do the search
     * @param distanceElementClass class of the {@link DistanceElement} type
     * @param config               query config
     * @param <E>                  type of the {@link DistanceElement}
     * @return a list of elements with their distance
     */
    default <E extends DistanceElement> List<E> getNearestNeighboursGeneric(int k,
        PrimitiveTypeProvider queryProvider, String column, Class<E> distanceElementClass,
        ReadableQueryConfig config) {
        if (queryProvider.getType().equals(ProviderDataType.FLOAT_ARRAY) || queryProvider.getType()
            .equals(ProviderDataType.INT_ARRAY)) {
            //Default-implementation for backwards compatibility.
            return getNearestNeighbours(k, PrimitiveTypeProvider.getSafeFloatArray(queryProvider), column,
                distanceElementClass, config);
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Performs a batched kNN-search with multiple query vectors. That is, the storage engine is tasked to perform the kNN search for each vector
     * in the provided list and returns the union of the results for every query.
     *
     * @param k                    The number k vectors to return per query.
     * @param vectors              The list of vectors to use.
     * @param column               The column to perform the kNN search on.
     * @param distanceElementClass class of the {@link DistanceElement} type
     * @param configs              The query configuration, which may contain distance definitions or query-hints.
     * @param <T>                  The type T of the resulting <T> type of the {@link DistanceElement}.
     * @return List of results.
     */
    <T extends DistanceElement> List<T> getBatchedNearestNeighbours(int k, List<float[]> vectors, String column, Class<T> distanceElementClass, List<ReadableQueryConfig> configs);

    /**
     * Performs a combined kNN-search with multiple query vectors. That is, the storage engine is tasked to perform the kNN search for each vector and then
     * merge the partial result sets pairwise using the desired MergeOperation.
     *
     * @param k                    The number k vectors to return per query.
     * @param vectors              The list of vectors to use.
     * @param column               The column to perform the kNN search on.
     * @param distanceElementClass class of the {@link DistanceElement} type
     * @param configs              The query configuration, which may contain distance definitions or query-hints.
     * @param merge
     * @param options
     * @param <T>                  The type T of the resulting <T> type of the {@link DistanceElement}.
     * @return List of results.
     */
    <T extends DistanceElement> List<T> getCombinedNearestNeighbours(int k, List<float[]> vectors, String column, Class<T> distanceElementClass, List<ReadableQueryConfig> configs, MergeOperation merge, Map<String, String> options);

    List<Map<String, PrimitiveTypeProvider>> getNearestNeighbourRows(int k, float[] vector, String column, ReadableQueryConfig config);

    List<float[]> getFeatureVectors(String fieldName, String value, String vectorName);

    /**
     * for legacy support, takes the float[] method by default
     */
    default List<PrimitiveTypeProvider> getFeatureVectorsGeneric(String fieldName, String value,
        String vectorName) {
        return getFeatureVectors(fieldName, value, vectorName).stream().map(FloatArrayTypeProvider::new)
            .collect(Collectors.toList());
    }

    List<Map<String, PrimitiveTypeProvider>> getRows(String fieldName, String value);

    List<Map<String, PrimitiveTypeProvider>> getRows(String fieldName, String... values);

    List<Map<String, PrimitiveTypeProvider>> getRows(String fieldName, Iterable<String> values);

    /**
     * Performs a fulltext search with multiple query terms. That is, the storage engine is tasked to lookup for entries in the provided fields that match
     * the provided query terms.
     *
     * TODO: This is a quick & dirty solution so far. Should be re-engineered to fit different use-cases.
     *
     * @param rows The number of rows that should be returned.
     * @param fieldname The field that should be used for lookup.
     * @param terms The query terms. Individual terms will be connected by a logical OR.
     * @return List of rows that math the fulltext search.
     */
    List<Map<String, PrimitiveTypeProvider>> getFulltextRows(int rows, String fieldname, String... terms);

    /**
     * Performs a boolean lookup on a specified field and retrieves the rows that match the specified condition.

     * i.e. SELECT * from WHERE A <Operator> B
     *
     * @param fieldName The name of the database field .
     * @param operator  The {@link RelationalOperator} that should be used for comparison.
     * @param value     The value the field should be compared to.
     * @return List of rows (one row is represented by one Map of the field ames and the data contained in the field).
     */
    List<Map<String, PrimitiveTypeProvider>> getRows(String fieldName, RelationalOperator operator, String value);

    /**
     * Performs a boolean lookup on a specified field  and retrieves the rows that match the specified condition.
     *
     * i.e. SELECT * from WHERE A <Operator> B
     *
     * @param fieldName The name of the database field .
     * @param operator  The {@link RelationalOperator} that should be used for comparison.
     * @param values    The values the field should be compared to.
     * @return List of rows (one row is represented by one Map of the field ames and the data contained in the field).
     */
    List<Map<String, PrimitiveTypeProvider>> getRows(String fieldName, RelationalOperator operator, Iterable<String> values);

    /**
     * SELECT column from the table. Be careful with large entities
     */
    List<PrimitiveTypeProvider> getAll(String column);

    /**
     * SELECT * from
     */
    List<Map<String, PrimitiveTypeProvider>> getAll();

    boolean existsEntity(String name);

    /**
     * Get first k rows
     */
    List<Map<String, PrimitiveTypeProvider>> preview(int k);
}
