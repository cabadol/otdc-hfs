package es.upm.oeg.epnoi.otdchfs;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchHFSM {

    private static final Logger LOG = LoggerFactory.getLogger(LaunchHFSM.class);
    private JavaSparkContext sc;
    private JavaPairRDD<String, String> corpus;

    @Before
    public void setup(){
        SparkConf conf = new SparkConf().setMaster("local").setAppName("My HFSM Clustering App");
        this.sc = new JavaSparkContext(conf);
        this.corpus = sc.wholeTextFiles("src/test/resources/corpus");
    }

    @Test
    public void classify(){


        int maxIterations   = 10;
        int numClusters     = 2;
        int numFeatures     = 11;
        TDCHFS tdchfs = new TDCHFS(numFeatures,numClusters,maxIterations);

        double accurateThreshold = 5.0;
        KMeansModel model = tdchfs.cluster(corpus, accurateThreshold);

        /**************************************************
         * Show details
         **************************************************/

        // Centroids
        Vector[] centroids = model.clusterCenters();
        for(Vector centroid: centroids){
            LOG.debug("Centroid: " + centroid);
        }

        // Classification
        JavaRDD<Vector> vectors = tdchfs.getTFIDF().characterize(tdchfs.getContent(corpus));
        for (Vector vector: vectors.collect() ){
            LOG.debug("Vector: " + vector + " classified in:  " + model.predict(vector));
        }

    }

}
