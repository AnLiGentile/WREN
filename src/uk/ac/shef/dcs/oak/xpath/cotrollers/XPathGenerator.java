package uk.ac.shef.dcs.oak.xpath.cotrollers;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

public interface XPathGenerator {

    public List<Pair<String, Double>> getXPaths();
}
