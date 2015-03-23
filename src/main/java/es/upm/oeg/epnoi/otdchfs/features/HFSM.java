package es.upm.oeg.epnoi.otdchfs.features;


import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Hybrid Feature Selection Metric
 * - Selects simultaneously frequent and semantically relevant terms through a weighting model
 */
public class HFSM implements FeatureExtractor{

    private static Logger LOG = LoggerFactory.getLogger(HFSM.class);


    @Override
    public JavaRDD<Vector> characterize(JavaRDD<List<String>> terms) {
        return null;
    }
}
