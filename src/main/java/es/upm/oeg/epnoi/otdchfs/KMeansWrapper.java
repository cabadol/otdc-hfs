package es.upm.oeg.epnoi.otdchfs;


import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class KMeansWrapper implements Serializable{

    private static final Logger LOG = LoggerFactory.getLogger(KMeansWrapper.class);

    private final int iterations;

    public KMeansWrapper(int maxIterations){
        this.iterations     = maxIterations;
    }

    public KMeansModel train(JavaRDD<Vector> vectors, int numClusters){
        // Cluster the data into two classes using KMeans
        KMeansModel clusterModel = KMeans.train(vectors.rdd(), numClusters, iterations);

        // Evaluate clustering by computing Within Set Sum of Squared Errors
        LOG.info("Within Set Sum of Squared Errors = " + clusterModel.computeCost(vectors.rdd()));

        return clusterModel;
    }

}
