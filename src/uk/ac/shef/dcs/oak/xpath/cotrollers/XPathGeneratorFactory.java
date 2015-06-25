package uk.ac.shef.dcs.oak.xpath.cotrollers;

import java.io.File;

import uk.ac.shef.dcs.oak.operations.Gazetteer;
import uk.ac.shef.dcs.oak.rexi.Property;


@Deprecated
public class XPathGeneratorFactory {

    private String intermediateResultFolder;

    public XPathGeneratorFactory(String intermediateResultFolder) {
        this.intermediateResultFolder = intermediateResultFolder;
    }

    public Thread createGeneratorThread(File inputFolder, String concept, Property property, Gazetteer gazetteer) {
    	//TODO this is to check, leave as is for now
        if (property.hasMultipleValues()) {
            return new GenerateXpathMultipleValues(inputFolder, concept, gazetteer, intermediateResultFolder);
        } else {
            return new GenerateXpathSingle(inputFolder, concept, gazetteer, intermediateResultFolder);
        }
    }
}
