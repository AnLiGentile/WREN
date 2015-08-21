package uk.ac.shef.dcs.oak.xpath.cotrollers.decorator;

import java.util.SortedMap;
import java.util.TreeMap;

import uk.ac.shef.dcs.oak.xpath.cotrollers.GenerateXpathMultipleValues;
import uk.ac.shef.dcs.oak.xpath.cotrollers.XPathGenerator;

public class LODIEXPathGeneratorDecorator extends AbstractXPathGeneratorDecorator {

    public LODIEXPathGeneratorDecorator(XPathGenerator generator) {
        super(generator);
    }

    @Override
    public SortedMap<String, Double> getXPaths() {
        SortedMap<String, Double> xpaths = this.generator.getXPaths();
        // TODO run LODIE multi-template strategy
        
        SortedMap<String, Double> winners = new TreeMap<String, Double>();
                winners.put(xpaths.firstKey(), xpaths.get(xpaths.firstKey()));
                // TODO having refactoring issues using old code. I need to re-implement the winners logic here
                // for now I am only getting the first candidate
         return winners;
    }

}
