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

import uk.ac.shef.dcs.oak.operations.ValueComparator;

//import uk.ac.shef.oak.xpath.collectiveExperiment.ValueComparator;

/**
 * @author annalisa
 *
 */
public class BuildValuesMap {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		String infile = "/Users/annalisa/Documents/LODIEws/LODIE_data/ISWCdataset/DATASET/ISWCpaperTEMPxpath/tempSIGIR8/rPNoBoilerp/film/movie-amctv-2000/title";
		SortedMap<String, Double> x = rankXpath(infile, 5);

		System.out.println("**************************");
		System.out.println("**************************");
		System.out.println("**************************");
		System.out.println("**************************");

		for (Entry<String, Double> e : x.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue());
		}
	}

	/**
	 * @param map
	 * @return sorted map
	 */
	private static SortedMap<String, Double> sortMap(Map<String, Double> map) {
		ValueComparator bvc = new ValueComparator(map);
		SortedMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
		sorted_map.putAll(map);
		return sorted_map;
	}

	public static SortedMap<String, Double> rankXpath(String infolder,
			int cardinality) {

		// xpath contains the chose xpath and their score
		Map<String, Double> xpath = new HashMap<String, Double>();

		File folder = new File(infolder);

		for (File f : folder.listFiles()) {
			// xp contains as keys the possible values extracted, as values, al
			// different xpath that extract that value in the page
			SortedMap<String, Set<String>> xp = new TreeMap<String, Set<String>>();

			BufferedReader br;

			try {

				br = new BufferedReader(new FileReader(f));

				int lines = 0;

				String nextLine;

				// if (folder.getName().equals("genre")){
				// System.out.println(folder.getAbsolutePath()+"*********genre*********");
				// System.out.println(f.getName());
				//
				// }
				while ((nextLine = br.readLine()) != null) {
					// if (folder.getName().equals("genre"))
					// System.out.println(nextLine);
					if (!nextLine.equals("")) {
						lines++;
						String t[] = nextLine.split("\t");
						if (!xp.containsKey(t[1])) {
							xp.put(t[1], new HashSet<String>());
							// out1.println(t[0] + "\t"+t[1]);
						}
						xp.get(t[1]).add(t[0]);

					}
				}

				SortedMap<String, Double> xps = new TreeMap<String, Double>();
				for (Entry<String, Set<String>> e : xp.entrySet()) {
					xps.put(e.getKey(), (double) e.getValue().size());

				}
				xps = sortMap(xps);


				// if there is a predoninant value in the page, keep the xpath,
				// otherwise do not consider results from the page
				boolean add = true;

				if (cardinality == 1) {
					add = false;

					double m = 0;
					for (double d : xps.values()) {
						// add = true;
						if (m == 0) {
							m = d;
						} else {
							if (d != m) {
								add = true;
							} else {
								add = false;
								break;
							}
						}

					}
				}

				// System.out.println(f.getName()+xp+add);

				if (!xps.isEmpty() & add) {
					String k = xps.firstKey();
					// System.out.println(k +
					// "--> "+xp.get(k).size()+"--> "+xp.get(k));
					for (String x : xp.get(k)) {
						// System.out.println("x = "+x);

						if (xpath.get(x) == null) {
							xpath.put(x, 1.0);
						} else {
							double val = xpath.get(x) + 1;
							xpath.put(x, val);

						}
					}
				}
				br.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		SortedMap<String, Double> sortx = sortMap(xpath);

		return sortx;

		// for(File f : folder.listFiles()){
		//
		// BufferedReader br;
		//
		// try {
		// out1 = new PrintWriter(new
		// FileWriter("./reducedPagesWithoutBoilerplate/"+f.getName()));
		//
		//
		// br = new BufferedReader(new FileReader(f));
		//
		//
		// String nextLine;
		//
		//
		// while ((nextLine = br.readLine()) != null) {
		// if (!nextLine.equals(""))
		// {
		// String t [] =nextLine.split("\t");
		// if (xp.get(t[0]).size()>1){
		// out1.println(t[0] + "\t"+t[1]);
		// }
		//
		//
		// }
		// }
		// out1.close();
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
	}

}
