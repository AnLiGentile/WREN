package uk.ac.shef.dcs.oak.xpath.processors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

//import uk.ac.shef.oak.xpath.collectiveExperiment.ValueComparator;


public class XpathBuilder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		String inFolder = "./pagexpath"; //html pages represented as a set of xpath-value
//		String gaz = "/Users/annalisa/Documents/CORPORAandDATASETS/ISWCdataset/gaz/film/title.txt";
//		String domain = "film";
		String gazFolder = "/Users/annalisa/Documents/CORPORAandDATASETS/ISWCdataset/gaz/film";
		String xpathFolder = "./temp/xpath/film/"; //where candidate xpaths are saved
		new File (xpathFolder).mkdirs();

		File gf = new File (gazFolder);
		for(File g: gf.listFiles()){
		if(g.getName().endsWith(".txt")){
			String gaz = g.getAbsolutePath();
			String att = g.getName().substring(0, g.getName().lastIndexOf(".txt"));
			
		CandidateXpathGenerator.generateCandidatedAnnotations(inFolder, gaz, "./temp/rP/film/"+att);
		RemoveBoilerplateFromPages.removeBoilerplate("./temp/rP/film/"+att, "./temp/rPNoBoilerp/film/"+att);
		// TODO this is assuming the attribute always has cardinality 1
		SortedMap<String, Double> x = BuildValuesMap.rankXpath("./temp/rPNoBoilerp/film/"+att, 1);

		System.out.println("**************************");
		System.out.println("**************************");
		System.out.println("**************************");
		System.out.println("**************************");
		PrintWriter out;

			try {
				out = new PrintWriter(new FileWriter(xpathFolder+File.separator+att+".txt"));
				for(Entry<String, Double> e:x.entrySet()){
					System.out.println(e.getKey()+" "+e.getValue());
					out.println(e.getKey()+" "+e.getValue());

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

