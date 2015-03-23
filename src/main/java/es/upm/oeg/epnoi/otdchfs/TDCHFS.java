package es.upm.oeg.epnoi.otdchfs;


import es.upm.oeg.epnoi.otdchfs.features.HFSM;
import es.upm.oeg.epnoi.otdchfs.features.TFIDF;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Text Document Clustering with Hybrid Feature Selection algorithm
 */
public class TDCHFS implements Serializable{

    private static final Logger LOG = LoggerFactory.getLogger(TDCHFS.class);

    private static final Integer NUM_FEATURES   = 11;
    private static final Integer MAX_ITERATIONS = 20;
    private static final Integer NUM_CLUSTERS   = 2;

    private final HFSM hfsm;
    private final KMeansWrapper kmeans;
    private final TFIDF tfidf;

    public TDCHFS(){

        // Term-Frequency and Inverse Document-Frequency
        this.tfidf = new TFIDF(NUM_FEATURES);

        // Hybrid Feature Selection (HFS) Algorithm
        this.hfsm = new HFSM();

        // KMeans Algorithm
        this.kmeans = new KMeansWrapper(MAX_ITERATIONS);
    }


    public JavaRDD<List<String>> getContent(JavaPairRDD<String, String> corpus){
        return corpus.map(new Function<Tuple2<String,String>, List<String>>() {
            @Override
            public List<String> call(Tuple2<String, String> tuple) throws Exception {
                return Arrays.asList(tuple._2().split(" "));
            }
        });
    }

    public KMeansModel cluster(JavaPairRDD<String, String> corpus, double minAccurate, int maxIterations){

        // Get content of each document
        JavaRDD<List<String>> terms = getContent(corpus);

        // Initial weights by Term-Frequency and Inverse Document-Frequency
        JavaRDD<Vector> vectors = this.tfidf.characterize(terms);

        // Initial Step
        // 1. Perfom the k-means Algorithm on the dataset to get initial clusters and centroids
        KMeansModel model = this.kmeans.train(vectors,NUM_CLUSTERS);

        // Compute the sum of squared errors
        double cost = model.computeCost(vectors.rdd());

        int iteration = 1;
        while(( cost > minAccurate) && (iteration++ < maxIterations)){
            // E-step
            // 2. Perfom the HFSM feature selection method on the dataset using the current clusters and centroids.
            // No change is made for the relevant features, however the weight of the unselected terms is reduced by
            // a predetermined factor f in the range of [0,1].

            // 3. Recalculate the k centroids in the new feature space.

            // M-step
            // 4. For each document in the new feature space, calculate its similarity with the k centroids using
            // the cosine function and then assign it to the closest cluster.


            // 5. Repeat steps 2, 3 and 4 until convergence
            cost = model.computeCost(vectors.rdd());
        }
        if (iteration >= maxIterations){
            LOG.warn("Max Iterations reached!");
        }


        return model;
    }

    public HFSM getHFSM(){
        return this.hfsm;
    }

    public KMeansWrapper getKMeans(){
        return this.kmeans;
    }

    public TFIDF getTFIDF(){
        return this.tfidf;
    }

}
