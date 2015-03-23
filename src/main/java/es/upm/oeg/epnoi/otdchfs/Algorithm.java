package es.upm.oeg.epnoi.otdchfs;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;

public class Algorithm implements Serializable{

    public void cluster(){
        SparkConf conf = new SparkConf().setMaster("local").setAppName("My OTDCHFS App");
        JavaSparkContext sc = new JavaSparkContext(conf);

        List<String> corpus = Arrays.asList(
                "document1.txt",
                "document2.txt",
                "document3.txt",
                "document4.txt"
                );


        // Documents which contain the term
        Map<String, List<String>> termMatrix        = new HashMap<>();

        // Frequency of terms by document
        Map<String,Map<String, Integer>> frqMatrix  = new HashMap<>();

        // Task to count words
        CountWords countWords = new CountWords();


        // Count words for each document
        for(String document: corpus){
            // Frequency of words for 'document'
            List<Tuple2<String, Integer>> counts = countWords.count(sc, "src/main/resources/" + document);

            // Convert to Map
            for (Tuple2<String, Integer> termFrequency: counts){

                // Term
                String term = termFrequency._1();

                // Frequency
                Integer frequency = termFrequency._2();

                // Add to Frequency Matrix
                Map<String, Integer> frqMap = frqMatrix.get(document);
                if (frqMap == null){
                    frqMap = new HashMap<>();
                }
                frqMap.put(term, frequency);
                frqMatrix.put(document,frqMap);

                // Add to Term Matrix
                List<String> documents = termMatrix.get(term);
                if (documents == null){
                    documents = new ArrayList<>();
                }
                documents.add(document);
                termMatrix.put(term,documents);
            }

        }

        List<Vector> vectors = new ArrayList<>();

        // List of vector space models
        for(String document: corpus){
            Map<String, Integer> terms = frqMatrix.get(document);

            // Vector
            double[] values = new double[termMatrix.size()];

            int j = 0;
            for(String term: termMatrix.keySet()){
                // Term Frequency
                Integer tf = terms.containsKey(term)? terms.get(term):0;
                // Number of documents
                Integer n  = corpus.size();
                // Number of documents that contains this term
                Integer df = termMatrix.get(term).size();

                // tf*log(n/df)
                Double tdidf = tf*Math.log(n/df);
                values[j++] = tdidf;
            }
            System.out.println("Document added: "+ document);
            vectors.add(Vectors.dense(values));
        }



        // PreProcessed Data
        JavaRDD<Vector> parsedData = sc.parallelize(vectors);



//        JavaRDD<Vector> parsedData = data.map(
//            new Function<String, Vector>() {
//                public Vector call(String s) {
//                    String[] sarray = s.split(" ");
//
//                    // Count occurrences
//
//
//
//                    double[] values = new double[sarray.length];
//                    for (int i = 0; i < sarray.length; i++)
//                        values[i] = Double.parseDouble(sarray[i]);
//                    return Vectors.dense(values);
//                }
//            }
//        );
//
        // Cluster the data into two classes using KMeans
        int numClusters = 2;
        int numIterations = 20;
        KMeansModel clusters = KMeans.train(parsedData.rdd(), numClusters, numIterations);


        System.out.println("Terms order: "+ termMatrix.keySet());
        System.out.println("Vectors: "+ vectors);


        // Evaluate clustering by computing Within Set Sum of Squared Errors
        double WSSSE = clusters.computeCost(parsedData.rdd());
        System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

        // Centroids
        Vector[] centroids = clusters.clusterCenters();
        for(Vector centroid: centroids){
            System.out.println("Centroid: "+ centroid);
        }

        for (Vector vector: vectors ){
            System.out.println("Cluster of "+ vector+" is " + clusters.predict(vector));
        }


    }

    public static void main(String[] args){
        Algorithm algorithm = new Algorithm();
        algorithm.cluster();
    }

}
