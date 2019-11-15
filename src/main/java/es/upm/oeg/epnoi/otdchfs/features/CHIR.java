package es.upm.oeg.epnoi.otdchfs.features;


import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CHIR implements Serializable{

    private static final Logger LOG = LoggerFactory.getLogger(CHIR.class);

    public void calculate(JavaRDD<Vector> words, KMeansModel model){

        int numClusters = model.clusterCenters().length;

        JavaRDD<WordCategory> termCategories = words.flatMap(new FlatMapFunction<Vector, WordCategory>() {
            @Override
            public Iterable<WordCategory> call(Vector vector) throws Exception {

                // Predicted category
                Integer predicted = model.predict(vector);

                // Initialize list of terms and categories
                List<WordCategory> termCategories = new ArrayList<WordCategory>();

                // Load entries for each category
                for (int category = 0; category < numClusters; category++) {

                    // TODO optimize this loop (only use active ones)
                    for (int i = 0; i < vector.size(); i++) {
                        WordCategory wordCategory = new WordCategory();
                        wordCategory.setCategory(category);
                        wordCategory.setCategoryLogic(predicted.equals(category));
                        wordCategory.setWord(i);
                        wordCategory.setWordLogic(vector.apply(i) > 0.0);
                        termCategories.add(wordCategory);
                    }
                }

                return termCategories;
            }
        });

        JavaPairRDD<WordCategory, Integer> pairTermCategories = termCategories.mapToPair(new PairFunction<WordCategory, WordCategory, Integer>() {
            @Override
            public Tuple2<WordCategory, Integer> call(WordCategory wordCategory) throws Exception {
                return new Tuple2<WordCategory, Integer>(wordCategory, 1);
            }
        });

        JavaPairRDD<WordCategory, Integer> counts = pairTermCategories.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer x, Integer y) throws Exception {
                return x + y;
            }
        });

        // Show details
        for(Tuple2<WordCategory, Integer> wordCategoryCount  : counts.collect()){
            LOG.info("TermCategory: "+ wordCategoryCount._1() + " => " + wordCategoryCount._2());
        }
    }
}
