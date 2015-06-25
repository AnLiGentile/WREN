package uk.ac.shef.dcs.oak.rexi;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.tuple.Pair;

import uk.ac.shef.dcs.oak.xpath.cotrollers.XPathGenerator;

/**
 * A simple class that makes it easier to run the x path generation inside a
 * single thread.
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class XPathGeneration implements Callable<List<Pair<String, Double>>> {

    private XPathGenerator generator;

    public XPathGeneration(XPathGenerator generator) {
        super();
        this.generator = generator;
    }

    @Override
    public List<Pair<String, Double>> call() throws Exception {
        return generator.getXPaths();
    }

}
