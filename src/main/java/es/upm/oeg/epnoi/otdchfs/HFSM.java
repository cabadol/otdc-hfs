package es.upm.oeg.epnoi.otdchfs;


import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.feature.HashingTF;
import org.apache.spark.mllib.feature.IDF;
import org.apache.spark.mllib.feature.IDFModel;
import org.apache.spark.mllib.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

public class HFSM implements Serializable{

    private static Logger LOG = LoggerFactory.getLogger(HFSM.class);
    private final int dimension;

    public HFSM(int dimension){
        // Recommended between 2**18 and 2**20
        this.dimension = dimension;
    }

    public JavaRDD<Vector> featureExtraction(JavaRDD<List<String>> terms){

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
