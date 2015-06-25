package uk.ac.shef.dcs.oak.xpath.cotrollers;

import java.io.File;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * @author annalisa
 *
 */
public class GenerateXpath extends Thread {

	private static Logger l4j = Logger.getLogger(GenerateXpath.class);

	public static NodeList findXpathNodeOnHtmlPage(Document doc, String xp)
	// public static NodeList findXpathNodeOnHtmlPage(org.jsoup.nodes.Document
	// doc, String xp)

			throws XPathExpressionException {

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile(xp);

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;

		// System.out.println(nodes.item(0));
		return nodes;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String domain = args[0];

		String gazFolder = args[1];

		String resFolder = args[2];

		String attribute = args[1].substring(
				args[1].lastIndexOf(File.separator) + 1,
				args[1].lastIndexOf(".txt"));

		int repetitions = 15;

		File d = new File(domain);

		GenerateXpathSingle.genereteXpath(domain, gazFolder, resFolder
				+ File.separator + "single" + File.separator + d.getName(),
				attribute, repetitions);
		GenerateXpathMultipleValues.genereteXpath(domain, gazFolder, resFolder
				+ File.separator + "multiple" + File.separator + d.getName(),
				attribute, repetitions);

	}

}
