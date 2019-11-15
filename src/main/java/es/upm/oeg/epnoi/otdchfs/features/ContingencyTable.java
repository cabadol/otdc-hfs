package es.upm.oeg.epnoi.otdchfs.features;


import lombok.Data;

import java.io.Serializable;

@Data
public class ContingencyTable implements Serializable{

    String word;

    int docsInCategoryAndWord;

    int docsInCategoryAndNoWord;

    int docsNotInCategoryAndWord;

    int docsNotInCategoryAndNoWord;




}
