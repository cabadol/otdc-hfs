package es.upm.oeg.epnoi.otdchfs.features;

import com.google.common.collect.Range;
import es.upm.oeg.epnoi.otdchfs.KMeansWrapper;
import es.upm.oeg.epnoi.otdchfs.TDCHFS;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CHIRTest implements Serializable{

    private static final Logger LOG = LoggerFactory.getLogger(CHIRTest.class);

    private JavaSparkContext sc;
    private JavaPairRDD<String, String> corpus;

    @Before
    public void setup(){
        SparkConf conf = new SparkConf().setMaster("local").setAppName("My HFSM Clustering App");
        this.sc = new JavaSparkContext(conf);
        this.corpus = sc.wholeTextFiles("src/test/resources/corpus");
    }

    @Test
    public void calculate(){
        int maxIterations   = 10;
        int numClusters     = 2;
        int numFeatures     = 11;
        TDCHFS tdchfs = new TDCHFS(numFeatures,numClusters,maxIterations);
        JavaRDD<List<String>> words = tdchfs.getContent(corpus);

        TFIDF tfidf = new TFIDF(numFeatures);
        JavaRDD<Vector> weightWords = tfidf.characterize(words);

        KMeansWrapper kmeans = new KMeansWrapper(maxIterations);
        KMeansModel model = kmeans.train(weightWords,numClusters);


        CHIR chir = new CHIR();
        chir.calculate(weightWords,model);



    }



}
