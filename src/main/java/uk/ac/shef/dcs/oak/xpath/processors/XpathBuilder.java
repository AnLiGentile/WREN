package uk.ac.shef.dcs.oak.xpath.processors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.SortedMap;

/**
 * @author annalisa
 * this is mainly a demonstration class
 * for setting up experiments refer to uk.ac.shef.dcs.oak.xpath.controllers package
 */
public class XpathBuilder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String experimentName = "testExperiment";
		String containerFolder = "intermediateProcessingResults";
		String domain = "book";
		String website = "book-booksamillion-2000";

		String inFolder = "./temp/pagexpath/testExperiment/" + domain
				+ File.separator + website; // html pages represented as a set
											// of xpath-value

		String gazFolder = "./resources/gazetteers/gazWithCardinality/"
				+ domain;

		String outputFolder = "./temp/" + containerFolder + File.separator
				+ experimentName + File.separator + domain + File.separator
				+ website + File.separator;

		String xpathFolder = outputFolder + "xpath/"; // where candidate xpaths
														// are
														// saved
		new File(xpathFolder).mkdirs();

		File gf = new File(gazFolder);
		for (File g : gf.listFiles()) {
			if (g.getName().endsWith(".txt")) {
				String gaz = g.getAbsolutePath();
				String att = g.getName().substring(0,
						g.getName().lastIndexOf(".txt"));

				CandidateXpathGenerator.generateCandidatedAnnotations(inFolder,
						gaz, outputFolder + "rP" + File.separator + att);
				RemoveBoilerplateFromPages.removeBoilerplate(outputFolder
						+ "rP" + File.separator + att, outputFolder
						+ "rPNoBoilerp" + File.separator + att);
				// TODO this is assuming the attribute always has cardinality >1
				SortedMap<String, Double> x = BuildValuesMap.rankXpath(
						outputFolder + "rPNoBoilerp" + File.separator + att, 10);

				System.out.println("**************************");
				System.out.println("**************************");
				System.out.println("**************************");
				System.out.println("**************************");
				PrintWriter out;

				try {
					out = new PrintWriter(new FileWriter(xpathFolder
							+ File.separator + att + ".txt"));
					for (Entry<String, Double> e : x.entrySet()) {
						System.out.println(e.getKey() + " " + e.getValue());
						out.println(e.getKey() + " " + e.getValue());

					}
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	}

}
