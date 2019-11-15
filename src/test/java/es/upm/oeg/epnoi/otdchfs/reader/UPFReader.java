package es.upm.oeg.epnoi.otdchfs.reader;


import edu.upf.taln.dri.common.util.Util;
import edu.upf.taln.dri.lib.Factory;
import edu.upf.taln.dri.lib.model.DocumentImpl;
import edu.upf.taln.dri.lib.model.ext.*;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Extracted from UPF web sample
 */
public class UPFReader {

    static{
      System.setProperty("DRIresources","/Users/cbadenes/Documents/Academic/OEG/DrInventor/upf/DRIresources-0.0.3.5");
    }

    @Test
    public void read(){

        try {
            Factory.setEnableBibEntryParsing(false);

            // Time
            Long start = System.currentTimeMillis();

            // Create a Document by downloadin a PDF file from an URL
            // Document doc = Factory.getPDFimporter().parsePDF(new URL(URL_OF_A_PDF_FILE));

            // Create a Document by downloadin a plain text file (UTF-8) from an URL
            // Document doc = Factory.getPDFimporter().parsePDF(new URL(URL_OF_A_PLAIN_TEXT_FILE_UTF_8));

            // Process a PDF file stored at the path: /my/file/path/PDF_file_name.pdf and get the Dr. Inventor Document instance
//            String PDF_FILE_PATH = "src/test/resources/pdf/document.pdf";
//            Document doc = Factory.getPDFimporter().parsePDF(PDF_FILE_PATH);



            DocumentImpl doc = new DocumentImpl();
            doc.loadXML("src/test/resources/CGCorpus/A01_S01_A_Powell_Optimization_Approach__for_Example-Based_Skinning__CORPUS__v3.xml");


            Long stop = System.currentTimeMillis();
            System.out.println("Time elapsed: " + (stop-start) + " ms");

            // Get document raw text
            String rawText = doc.getRawText();

            // Get document XML (UTF-8)
            String XMLText = doc.getXMLString();

            // Get sentences extracted from the document, in document order - ALL and ONLY_ABSTRACT
            List<Sentence> sentenceListALL = doc.extractSentences(SentenceSelectorENUM.ALL);
            List<Sentence> sentenceListONLY_ABSTRACT = doc.extractSentences(SentenceSelectorENUM.ONLY_ABSTRACT);
            System.out.println("The document has " + ((sentenceListALL != null) ? sentenceListALL.size() : "---") + " sentences.");
            System.out.println("The abstract has " + ((sentenceListONLY_ABSTRACT != null) ? sentenceListONLY_ABSTRACT.size() : "---") + " sentences.");

            // Get document ordered section names
            List<String> sectionNameInDocOrder = doc.extractSectionNames();

            // *** SENTENCE FEATURES:
            // Get sentence number 10 in document order, considering all the sentences, including the abstract ones
            Sentence sentALL10 = sentenceListALL.get(10);

            // Get the title of the section the sentence number 10 belongs to, considering all the sentences, including the abstract ones
            String sectionName = sentALL10.getSectionName();
            System.out.println("SENT> The name of the section of the 10th sentence is " + ((sectionName != null) ? sectionName : "---") + ".");

            // Get the text of the sentence number 10, considering all the sentences, including the abstract ones
            String sentenceText = sentALL10.getText();
            System.out.println("SENT> The text of the 10th sentence is " + ((sentenceText != null) ? sentenceText : "---") + ".");

            // Get the rhetorical class assigned to the sentence number 10, considering all the sentences, including the abstract ones
            RhetoricalClassENUM rhetClass = sentALL10.getRhetoricalClass();
            System.out.println("SENT> The rhetorical class of the 10th sentence is " + ((rhetClass != null) ? rhetClass : "---") + ".");

            // Get the document unambiguous ID that identifies the sentence number 10 in document order, considering all the sentences, including the abstract ones
            Integer sentenceNum10Id = sentALL10.getId();
            System.out.println("SENT> The id of the 10th sentence is " + ((sentenceNum10Id != null) ? sentenceNum10Id : "---") + ".");

            // *** TERMINOLOGY:
            // Get the list of candidate terms extracted from the document and print all the sentences where each candidate term occurs
            List<CandidateTerm> termList = doc.extractTerminology();
            System.out.println("TERM> A total of " + termList.size() + " candidate terms have been extracted from the document.");
            System.out.println("TERM> List of terms:");
            for(CandidateTerm candTerm : termList) {
                System.out.println("TERM>       Term: " + candTerm.getText());
                System.out.println("TERM>       Occurring in : " + candTerm.getSentencesWithTerm().size() + " sentences:");
                for(Map.Entry<Integer, String> sentenceTermOccurrence : candTerm.getSentencesWithTerm().entrySet()) {
                    System.out.println("TERM>       --- the pattern " + sentenceTermOccurrence.getValue() +
                            " has been matched in the sentence: '" + doc.extractSentenceById(sentenceTermOccurrence.getKey()).getText() + "'");
                }
                System.out.println("TERM>");
            }

            // *** GRAPH:
            // Get the COMPLETE and COMPACT graphs generated from the sentence number 10 , considering all the sentences, including the abstract ones
            Graph sentALL10_graphCOMPLETE = doc.extractSentenceGraph(sentALL10.getId(), GraphTypeENUM.DEP);
            Graph sentALL10_graphCOMPACT = doc.extractSentenceGraph(sentALL10.getId(), GraphTypeENUM.COMPACT);

            // Print the COMPLETE and COMPACT graphs generated from the sentence number 10 , considering all the sentences, including the abstract ones
            System.out.print("GRAPH>\n" + sentALL10_graphCOMPLETE.graph_AsString(GraphToStringENUM.TREE));
            System.out.print("GRAPH>\n" + sentALL10_graphCOMPACT.graph_AsString(GraphToStringENUM.TREE));

            // Get and print a CSV string (tab separated) with the following column-tab-separated row format to support ROS population:
            // SENTENCE_ID <tab> START_NODE_ID <tab> START_NODE_WORD <tab> START_NODE_LEMMA <tab> START_NODE_POS <tab> END_NODE_ID <tab> END_NODE_WORD <tab> END_NODE_LEMMA <tab> END_NODE_POS <tab> EDGE_TYPE
            // Refer to the javadoc to explore the meaning of the parameters
            String CSVstring = Util.getROSasCSVstring(doc, SentenceSelectorENUM.ALL, GraphTypeENUM.DEP);
            System.out.print("GRAPH>\n" + CSVstring);

            // Get the extractive summary made of the top 5 TFIDF scored sentences.
            List<Sentence> summarySentences = doc.extractSummary(5, SummaryTypeENUM.TOP_TFIDF);
            for(Sentence sentSumm : summarySentences) {
                System.out.print("SUMMARY> " + sentSumm.getText());
            }

            // Get the citations of the document
            List<Citation> citations = doc.extractCitations();

            // Print info of every citation
            for(Citation cit : citations) {
                System.out.println("---------------------------------------");
                System.out.println("BIBLIOGRAPHIC ENTRY TEXT: " + cit.getText());
                System.out.println("PAPER TITLE: " + ((cit.getTitle() != null) ? cit.getTitle():"-"));
                for(Person pn : cit.getAuthorList()) {
                    System.out.println( "AUTHOR: " + ((pn.getFullName() != null) ? pn.getFullName():"-") +
                            " FIRST NAME: " + ((pn.getFirstName() != null) ? pn.getFirstName():"-") +
                            " SURNAME: " + ((pn.getSurname() != null) ? pn.getSurname():"-") );
                }

                for(Person pn : cit.getEditorList()) {
                    System.out.println( "EDITOR: " + ((pn.getFullName() != null) ? pn.getFullName():"-") +
                            " FIRST NAME: " + ((pn.getFirstName() != null) ? pn.getFirstName():"-") +
                            " SURNAME: " + ((pn.getSurname() != null) ? pn.getSurname():"-") );
                }

                System.out.println("EDITION: " + ((cit.getEdition() != null) ? cit.getEdition():"-"));
                System.out.println("YEAR: " + ((cit.getYear() != null) ? cit.getYear():"-"));
                System.out.println("PAGES: " + ((cit.getPages() != null) ? cit.getPages():"-"));
                System.out.println("DOI: " + ((cit.getDoi() != null) ? cit.getDoi():"-"));
                System.out.println("Open URL: " + ((cit.getOpenURL() != null) ? cit.getOpenURL():"-"));
                System.out.println("BibsonomyURL: " + ((cit.getBibsonomyURL() != null) ? cit.getBibsonomyURL():"-"));
                System.out.println("CHAPTER: " + ((cit.getChapter() != null) ? cit.getChapter():"-"));
                System.out.println("VOLUME: " + ((cit.getVolume() != null) ? cit.getVolume():"-"));
                System.out.println("SERIES: " + ((cit.getSeries() != null) ? cit.getSeries():"-"));
                System.out.println("JOURNAL: " + ((cit.getJournal() != null) ? cit.getJournal():"-"));
                System.out.println("INSTITUTION: " + ((cit.getInstitution() != null) ? cit.getInstitution():"-"));

                System.out.println("> NUMBER OF INLINE CITAITONS: " + cit.getCitaitonMarkers().size());
                for(CitationMarker citMarker : cit.getCitaitonMarkers()) {
                    System.out.println("   > MARKER: " + citMarker.getReferenceText() + " in sentence (" + citMarker.getSentenceId() + "):");
                    System.out.println("           '" + doc.extractSentenceById(citMarker.getSentenceId()).getText() + "'");
                }
            }

            // Get the XML version of document contents, serialized as an XML file (UTF-8) - to store
            String serializedXMLdocuement = doc.getXMLString();

			/* STORE THE SERIALIZED XML DRI DOCUMENT IN THE FILE /my/file/path/dri_serialized_document.xml */

			/* ... */

			/* EXAMPLE 2: LOAD PREVIOUSLY STORED FILE CONTAINING A SERIALIZED XML DRI DOCUMENT
			String serializedDRIdocumentPath = "/my/file/path/dri_serialized_document.xml";
			Document docLoaded;

			docLoaded = Factory.createNewDocument(serializedDRIdocumentPath);
			 */

			/* EXAMPLE 3: LOAD A DOCUMENT BY PARSING THE CONTENTS OF A STRING
			Document textDoc = Factory.getPDFimporter().parseString("Speaking at the UN, he said the rebels were only withdrawing heavy weapons from the front line in selective areas. Meanwhile Russian Foreign Minister Sergei Lavrov said there had been tangible progress with the truce.Both Ukraine and the rebels say they are withdrawing heavy weapons in line with the deal made in Minsk last month. Monitors from the OSCE security group have reported weapons movements on both sides but say it is too early to confirm a full withdrawal.Mr Kerry and Mr Lavrov held talks on Monday as tensions remain high over the conflict. The fragile ceasefire is said to be broadly holding, despite some fighting in recent days.", "");
			 */

        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }

    }

}
