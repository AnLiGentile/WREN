package uk.ac.shef.dcs.oak.xpath.cotrollers.decorator;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import uk.ac.shef.dcs.oak.xpath.cotrollers.XPathGenerator;

public class LggXPathGeneratorDecorator extends AbstractXPathGeneratorDecorator {

    public LggXPathGeneratorDecorator(XPathGenerator generator) {
        super(generator);
    }

    @Override
    public List<Pair<String, Double>> getXPaths() {
        List<Pair<String, Double>> xpaths = this.generator.getXPaths();
        // TODO LGG Magic
        return xpaths;
    }

}
