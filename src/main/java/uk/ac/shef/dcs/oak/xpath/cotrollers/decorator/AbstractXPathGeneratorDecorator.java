package uk.ac.shef.dcs.oak.xpath.cotrollers.decorator;

import uk.ac.shef.dcs.oak.xpath.cotrollers.XPathGenerator;

public abstract class AbstractXPathGeneratorDecorator implements XPathGeneratorDecorator {

    protected XPathGenerator generator;

    public AbstractXPathGeneratorDecorator(XPathGenerator generator) {
        this.generator = generator;
    }

    @Override
    public XPathGenerator getDecorated() {
        return generator;
    }

}
