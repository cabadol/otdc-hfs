package es.upm.oeg.epnoi.otdchfs.features;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;

import java.io.Serializable;
import java.util.List;


public interface FeatureExtractor extends Serializable{

    JavaRDD<Vector> characterize(JavaRDD<List<String>> terms);
}
