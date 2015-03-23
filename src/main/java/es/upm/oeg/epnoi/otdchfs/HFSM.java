package es.upm.oeg.epnoi.otdchfs;


import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.feature.IDF;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class HFSM implements Serializable{

    private static Logger LOG = LoggerFactory.getLogger(HFSM.class);

    private JavaRDD<Vector> featureExtraction(JavaRDD<List<String>> terms){

        // Recommended between 2**18 and 2**20
        int num_features= 11;

        // Term-Frequency Feature Extraction
        HashingTF tf = new HashingTF(num_features);

        // Read a set of text files as TF Vectors
        JavaRDD<Vector> tfVectors = tf.transform(terms).cache();

        // Inverse Document-Frequency Feature Extraction
        IDF idf = new IDF();

        // Compute the IDF, then the TF-IDF vectors
        IDFModel idfModel = idf.fit(tfVectors);
        JavaRDD<Vector> tfIdfVectors = idfModel.transform(tfVectors);

        return tfIdfVectors;
    }

    private KMeansModel kmeans(JavaRDD<Vector> vectors){
        // Cluster the data into two classes using KMeans
        int numClusters = 2;
        int numIterations = 20;
        KMeansModel clusterModel = KMeans.train(vectors.rdd(), numClusters, numIterations);

        // Evaluate clustering by computing Within Set Sum of Squared Errors
        LOG.info("Within Set Sum of Squared Errors = " + clusterModel.computeCost(vectors.rdd()));

        return clusterModel;
    }


    public void cluster(JavaPairRDD<String, String> corpus){

        JavaRDD<List<String>> terms = corpus.map(new Function<Tuple2<String,String>, List<String>>() {
            @Override
            public List<String> call(Tuple2<String, String> tuple) throws Exception {
                return Arrays.asList(tuple._2().split(" "));
            }
        });


        // Representation as vector space model
        JavaRDD<Vector> vectors = featureExtraction(terms);

        // Perform the k-means algorithm
        KMeansModel model = kmeans(vectors);

        /**************************************************
         * Show details
         **************************************************/

        // Centroids
        Vector[] centroids = model.clusterCenters();
        for(Vector centroid: centroids){
            LOG.debug("Centroid: "+ centroid);
        }

        for (Vector vector: vectors.collect() ){
            LOG.debug("Vector: " + vector + " classified in:  " + model.predict(vector));
        }


    }
}
