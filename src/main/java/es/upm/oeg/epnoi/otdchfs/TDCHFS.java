package es.upm.oeg.epnoi.otdchfs;


import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class TDCHFS implements Serializable{

    private static final Integer NUM_FEATURES   = 11;
    private static final Integer MAX_ITERATIONS = 20;
    private static final Integer NUM_CLUSTERS   = 2;

    private final HFSM hfsm;
    private final KMeansWrapper kmeans;

    public TDCHFS(){

        // Hybrid Feature Selection (HFS) Algorithm
        this.hfsm = new HFSM(NUM_FEATURES);

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

    public KMeansModel cluster(JavaPairRDD<String, String> corpus){

        // Get content of each document
        JavaRDD<List<String>> terms = getContent(corpus);

        // Get initial weights by Hybrid Feature Selection (HFS) algorithm
        JavaRDD<Vector> vectors = this.hfsm.featureExtraction(terms);

        // Initial Step
        // 1. Perfom the k-means Algorithm on the dataset to get initial clusters and centroids
        KMeansModel model = this.kmeans.train(vectors,NUM_CLUSTERS);

        // E-step
        // 2. Perfom the HFSM feature selection method on the dataset using the current clusters and centroids.
        // No change is made for the relevant features, however the weight of the unselected terms is reduced by
        // a predetermined factor f in the range of [0,1].

        // 3. Recalculate the k centroids in the new feature space.

        // M-step
        // 4. For each document in the new feature space, calculate its similarity with the k centroids using
        // the cosine function and then assign it to the closest cluster.


        // 5. Repeat steps 2, 3 and 4 until convergence

        return model;
    }

    public HFSM getHFSM(){
        return this.hfsm;
    }

    public KMeansWrapper getKMeans(){
        return this.kmeans;
    }

}
