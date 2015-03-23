package es.upm.oeg.epnoi.otdchfs;


import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class CountWords implements Serializable{

    public List<Tuple2<String, Integer>> count(JavaSparkContext sc, String file){
        JavaRDD<String> data = sc.textFile(file);

        // Split up into words.
        JavaRDD<String> words = data.flatMap(
                new FlatMapFunction<String, String>() {
                    public Iterable<String> call(String line) {
                        return Arrays.asList(line.split(" "));
                    }
                });

        // Transform into pairs and count
        JavaPairRDD<String, Integer> counts = words.mapToPair(
                new PairFunction<String, String, Integer>(){
                    public Tuple2<String, Integer> call(String x){
                        return new Tuple2(x, 1);
                    }
                }).reduceByKey(new Function2<Integer, Integer, Integer>(){
            public Integer call(Integer x, Integer y){ return x + y;}
        });


        return counts.collect();
    }
}
