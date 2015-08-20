package uk.ac.shef.dcs.oak.xpath.cotrollers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;
import java.util.SortedMap;

import uk.ac.shef.dcs.oak.operations.Gazetteer;
import uk.ac.shef.dcs.oak.xpath.processors.BuildValuesMap;
import uk.ac.shef.dcs.oak.xpath.processors.CandidateXpathGenerator;
import uk.ac.shef.dcs.oak.xpath.processors.RemoveBoilerplateFromPages;

/**
 * @author annalisa 
 * this class provides facilities to return the complete list
 * of xpaths for an attribute on a website
 */
public class GenerateAllXpath implements XPathGenerator {

	String domain;
	String domain_i;
	String domain_iPagesFolder;
	String domain_iIntermediateResultsFolder;
	Gazetteer gazetteer;
	String attributeName;
	SortedMap<String, Double> rankedXpaths;
	
	
	@Override
	public SortedMap<String, Double> getXPaths() {
		
		if (rankedXpaths ==null)
			buildRankedXpaths();
		return rankedXpaths;
	}
	
	public SortedMap<String, Double> buildRankedXpaths() {
		
		// TODO this is assuming the attribute always has cardinality >1
		rankedXpaths = BuildValuesMap.rankXpath(
				this.domain_iIntermediateResultsFolder + "rPNoBoilerp"
						+ File.separator + this.attributeName, 10);

		PrintWriter out;

		try {
	        new File(this.domain_iIntermediateResultsFolder + "xpath").mkdirs();
			out = new PrintWriter(new FileWriter(
					this.domain_iIntermediateResultsFolder + "xpath"
							+ File.separator + this.attributeName + ".txt"));
			for (Entry<String, Double> e : rankedXpaths.entrySet()) {
//				System.out.println(e.getKey() + " " + e.getValue());
				out.println(e.getKey() + " " + e.getValue());

			}
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return rankedXpaths;
	}

	private void preprocess() {
		
		//TODO if this exists already, load from file
		String rPFolder = this.domain_iIntermediateResultsFolder + "rP"
				+ File.separator + this.attributeName;
		new File(rPFolder).mkdirs();
		CandidateXpathGenerator.generateCandidatedAnnotations(
				this.domain_iPagesFolder, this.gazetteer.getWords(), rPFolder);
		
		
		//TODO if this exists already, load from file
		String rPNoBoilerpFolder = this.domain_iIntermediateResultsFolder
				+ "rPNoBoilerp" + File.separator + this.attributeName;
		new File(rPNoBoilerpFolder).mkdirs();
		RemoveBoilerplateFromPages.removeBoilerplate(rPFolder,
				rPNoBoilerpFolder);

		// **************************
		// rank candidates


	}

	public GenerateAllXpath(String domain, String domain_i,
			String domain_iPagesFolder,
			String domain_iIntermediateResultsFolder, Gazetteer gazetteer,
			String attributeName) {
		super();
		this.domain = domain;
		this.domain_i = domain_i;
		this.domain_iPagesFolder = domain_iPagesFolder;
		this.domain_iIntermediateResultsFolder = domain_iIntermediateResultsFolder;
		this.gazetteer = gazetteer;
		this.attributeName = attributeName;
		preprocess();
	}

}
