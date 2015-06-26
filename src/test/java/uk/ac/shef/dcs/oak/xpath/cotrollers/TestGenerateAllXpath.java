package uk.ac.shef.dcs.oak.xpath.cotrollers;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

import uk.ac.shef.dcs.oak.operations.Gazetteer;
import uk.ac.shef.dcs.oak.rexi.Property;

public class TestGenerateAllXpath extends JUnitCore {
	GenerateAllXpath gax;

    @Before
    public void initialize() throws Exception {	
	Set<Property> properties = new HashSet<Property>();
    properties.add(new Property("http://example.org/author", "author"));
    
    String concept = "book";
    
    

    Map<Property, Gazetteer> gazetteerMapping = new HashMap<Property, Gazetteer>();
    for (Property property : properties) {
        Gazetteer gazetteer = new Gazetteer("./resources/gazetteers/gazWithCardinality/" + concept + File.separator + property.getLabel()
                + ".txt");
        gazetteerMapping.put(property, gazetteer);
    }

    gax = new GenerateAllXpath("book", "book-booksamillion-2000", "./temp/pagexpath/testExperiment/book/book-booksamillion-2000", "./temp/intermediateProcessingResults/testExperiment2/", gazetteerMapping.get(new Property("http://example.org/author", "author")), "author");


    }

    @Test
    public void testXpathGeneration() throws Exception {

    	//TODO I am not gonna test it properly (for now....)
	Assert.assertEquals(0, gax.getXPaths().size());
    }

}
