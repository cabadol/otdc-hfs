package es.upm.oeg.epnoi.otdchfs.reader;


import gate.*;
import gate.util.GateException;
import gate.util.InvalidOffsetException;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GateReader {

    @Test
    public void readFromXml() throws GateException, MalformedURLException {

        Gate.setGateHome(new File("/Users/cbadenes/Documents/Academic/OEG/DrInventor/upf/DRIresources-0.0.3.5/gate_home"));
        Gate.init();

        String xml = "file:src/test/resources/CGCorpus/A01_S01_A_Powell_Optimization_Approach__for_Example-Based_Skinning__CORPUS__v3.xml";
        System.out.println("Reading xml file...");
        Document doc = Factory.newDocument(new URL(xml));

//        System.out.println(doc);

        System.out.println("Reading content..");
        DocumentContent content = doc.getContent();

        System.out.println("Reading annotations..");
        Map<String, AnnotationSet> annotations = doc.getNamedAnnotationSets();
//        System.out.println( annotations.size() + " annotations read");

        Map<String,List<String>> sections = new HashMap<>();

        for(String annotation: annotations.keySet()){
//            System.out.println("=====================================");
//            System.out.println("Annotation: " + annotation);

            AnnotationSet value = annotations.get(annotation);
            for(Annotation innerAnnotation: value.inDocumentOrder()){
//                System.out.println("----------------------------------");
//                System.out.println("Annotation id: " + innerAnnotation.getId());
//                System.out.println("Annotation type: " + innerAnnotation.getType());
//                System.out.println("Annotation start-node: " + innerAnnotation.getStartNode());
//                System.out.println("Annotation end-node: " + innerAnnotation.getEndNode());

                addNodes(content, sections, innerAnnotation.getType(), innerAnnotation);

                FeatureMap features = innerAnnotation.getFeatures();
                for (Object feature: features.keySet()){
                    String element = (String) features.get(feature);
//                    System.out.println("Feature '" + feature + "' = " + element);
                    if (!feature.equals(innerAnnotation.getType()) && (((String)feature).startsWith("DRI_"))){
                        addNodes(content, sections, (String) feature, innerAnnotation);
                    }
                }
//                System.out.println("Content: " + content.getContent(innerAnnotation.getStartNode().getOffset(), innerAnnotation.getEndNode().getOffset()));


            }
        }

       for(String section: sections.keySet()){
           System.out.println("-------------------------------");
           System.out.println("-------------------------------");
           System.out.println("SECTION: " + section);
           System.out.println("-------------------------------");
           List<String> sectionContent = sections.get(section);
           for(String sentence : sectionContent){
               System.out.println(sentence);
           }
       }

    }

    private void addNodes(DocumentContent content, Map<String,List<String>> sections, String section, Annotation annotation) throws InvalidOffsetException {
        if (!section.startsWith("DRI_")) return;
        List<String> sentences = sections.get(section);
        if (sentences == null){
            sentences = new ArrayList<>();
        }
        sentences.add(content.getContent(annotation.getStartNode().getOffset(), annotation.getEndNode().getOffset()).toString());
        sections.put(section, sentences);
    }


    @Test
    public void readFromPDF() throws GateException, MalformedURLException {
        Gate.setGateHome(new File("/Users/cbadenes/Documents/Academic/OEG/DrInventor/upf/DRIresources-0.0.3.5/gate_home"));
        Gate.init();

        String pdf = "file:src/test/resources/pdf/document.pdf";
        System.out.println("Reading pdf file...");
        Document doc = Factory.newDocument(new URL(pdf));
        System.out.println(doc);
    }

}
