package es.upm.oeg.epnoi.otdchfs.features;


import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.feature.IDF;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.Vector;

import java.io.Serializable;
import java.util.List;

/**
 * Term-Frequency and Inverse Document-Frequency
 */
public class TFIDF implements FeatureExtractor{

    private final int dimension;

    public TFIDF(int dimension){
        this.dimension = dimension;
    }

    @Override
    public JavaRDD<Vector> characterize(JavaRDD<List<String>> terms){

        // Term-Frequency Feature Extraction
        HashingTF tf = new HashingTF(dimension);

        // Read a set of text files as TF Vectors
        JavaRDD<Vector> tfVectors = tf.transform(terms).cache();

        // Inverse Document-Frequency Feature Extraction
        IDF idf = new IDF();

        // Compute the IDF, then the TF-IDF vectors
        IDFModel idfModel = idf.fit(tfVectors);
        JavaRDD<Vector> tfIdfVectors = idfModel.transform(tfVectors);

        return tfIdfVectors;
    }

}
