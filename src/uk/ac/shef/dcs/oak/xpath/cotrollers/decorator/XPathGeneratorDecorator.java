package uk.ac.shef.dcs.oak.xpath.cotrollers.decorator;

import uk.ac.shef.dcs.oak.xpath.cotrollers.XPathGenerator;

public interface XPathGeneratorDecorator extends XPathGenerator {

    public XPathGenerator getDecorated();
}
