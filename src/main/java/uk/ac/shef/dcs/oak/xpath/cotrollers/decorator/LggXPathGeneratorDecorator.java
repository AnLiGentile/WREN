package uk.ac.shef.dcs.oak.xpath.cotrollers.decorator;

import java.util.SortedMap;

import uk.ac.shef.dcs.oak.xpath.cotrollers.XPathGenerator;

public class LggXPathGeneratorDecorator extends AbstractXPathGeneratorDecorator {

    public LggXPathGeneratorDecorator(XPathGenerator generator) {
        super(generator);
    }

    @Override
    public SortedMap<String, Double> getXPaths() {
        SortedMap<String, Double> xpaths = this.generator.getXPaths();
        // TODO LGG Magic
        return xpaths;
    }

}
