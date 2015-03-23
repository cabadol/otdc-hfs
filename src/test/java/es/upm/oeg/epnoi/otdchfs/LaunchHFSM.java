package es.upm.oeg.epnoi.otdchfs;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchHFSM {

    private static final Logger LOG = LoggerFactory.getLogger(LaunchHFSM.class);

    @Test
    public void classify(){

        SparkConf conf = new SparkConf().setMaster("local").setAppName("My HFSM Clustering App");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaPairRDD<String, String> input = sc.wholeTextFiles("src/test/resources/corpus");


        double accurateThreshold = 5.0;
        int maxIterations = 10;
        TDCHFS tdchfs = new TDCHFS();
        KMeansModel model = tdchfs.cluster(input, accurateThreshold, maxIterations);

        /**************************************************
         * Show details
         **************************************************/

        // Centroids
        Vector[] centroids = model.clusterCenters();
        for(Vector centroid: centroids){
            LOG.debug("Centroid: " + centroid);
        }

        // Classification
        JavaRDD<Vector> vectors = tdchfs.getTFIDF().characterize(tdchfs.getContent(input));
        for (Vector vector: vectors.collect() ){
            LOG.debug("Vector: " + vector + " classified in:  " + model.predict(vector));
        }

    }

}
