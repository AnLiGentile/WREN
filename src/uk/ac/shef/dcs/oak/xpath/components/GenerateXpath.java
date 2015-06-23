package uk.ac.shef.dcs.oak.xpath.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.shef.oak.any23.xpath.HtmlDocument;
import uk.ac.shef.dcs.oak.operations.SetOperations;
import uk.ac.shef.dcs.oak.operations.TextOperations;
import uk.ac.shef.dcs.oak.operations.ValueComparator;
import uk.ac.shef.wit.ie.wrapper.html.xpath.DOMUtil;

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
