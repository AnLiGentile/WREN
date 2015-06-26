package uk.ac.shef.dcs.oak.rexi;

import java.util.SortedMap;
import java.util.concurrent.Callable;

import uk.ac.shef.dcs.oak.xpath.cotrollers.XPathGenerator;

/**
 * A simple class that makes it easier to run the x path generation inside a
 * single thread.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class XPathGeneration implements Callable<SortedMap<String, Double>> {

    private XPathGenerator generator;

    public XPathGeneration(XPathGenerator generator) {
        super();
        this.generator = generator;
    }

    @Override
    public SortedMap<String, Double> call() throws Exception {
        return generator.getXPaths();
    }

}
