package es.upm.oeg.epnoi.otdchfs;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Test;

public class LaunchHFSM {

    @Test
    public void hfsmCluster(){

        SparkConf conf = new SparkConf().setMaster("local").setAppName("My HFSM Clustering App");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaPairRDD<String, String> input = sc.wholeTextFiles("src/test/resources/corpus");

        HFSM hfsm = new HFSM();
        hfsm.cluster(input);

    }

}
