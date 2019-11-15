package es.upm.oeg.epnoi.otdchfs.features;

import lombok.Data;

import java.io.Serializable;

@Data
public class WordCategory implements Serializable{
    Integer category;
    Boolean categoryLogic;

    Integer word;
    Boolean wordLogic;

    public String getId(){
        StringBuilder id = new StringBuilder();

        if (!categoryLogic){
            id.append("!");
        }
        id.append(category).append("c");

        if (!wordLogic){
            id.append("!");
        }
        id.append(word).append("w");


        return id.toString();
    }
}
