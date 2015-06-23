package uk.ac.shef.dcs.oak.xpath.processors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import uk.ac.shef.dcs.oak.lodie.table.interpreter.misc.DataTypeClassifier;
import uk.ac.shef.dcs.oak.lodie.table.interpreter.misc.DataTypeClassifier.DataType;
import uk.ac.shef.dcs.oak.operations.SetOperations;
import uk.ac.shef.dcs.oak.operations.TextOperations;
import uk.ac.shef.dcs.oak.operations.ValueComparator;
import uk.ac.shef.dcs.oak.xpath.components.GenerateXpath;
import uk.ac.shef.oak.any23.xpath.HtmlDocument;
import uk.ac.shef.wit.ie.wrapper.html.xpath.DOMUtil;

//import uk.ac.shef.oak.xpath.collectiveExperiment.ValueComparator;

/**
 * @author annalisa
 *
 */
public class ExtractValues {

	public static Map<String, Integer> loadCardinalityInfo(String gazFolder) {

		Map<String, Integer> referenceGazettersCardinality = new HashMap<String, Integer>();

		// load gazetter per attribute
		// load cardinality info for each gazetter, assume 1 if not available
		File g = new File(gazFolder);
		for (File f : g.listFiles()) {
			if (f.getName().endsWith(".txt")) {
				Set<String> gaz = new HashSet<String>();

				String name = f.getName().substring(0,
						f.getName().lastIndexOf(".txt"));
				int i = 1;
				if (name.contains("-")) {
					String[] n = name.split("-");
					i = Integer.parseInt(n[1]);
					name = n[0];
				}
				referenceGazettersCardinality.put(name, i);

			}
		}
		return referenceGazettersCardinality;
	}

	/**
	 * @param inFolder
	 * @param gf
	 * @return
	 */
	static Map<String, Set<String>> extractWith2templates(String inFolder,
			File gf, String gazFolder) {
		// TODO this bit keeps xpath with same template as the first two ones

		Map<String, Set<String>> xpath = new HashMap<String, Set<String>>();
		Map<String, Integer> cardinalityInfo = loadCardinalityInfo(gazFolder);

		for (File g : gf.listFiles()) {
			if (g.isDirectory()) {
				SortedMap<String, Double> x = BuildValuesMap.rankXpath(inFolder
						+ File.separator + g.getName(),
						cardinalityInfo.get(g.getName()));
				if (!x.isEmpty()) {

					Set<String> xp = new HashSet<String>();
					xp.add(x.firstKey());
					String template1 = DOMUtil.removeXPathPositionFilters(x
							.firstKey());
					String template2 = "";

					// only keep xpath with same template as the first one
					for (String xps : x.keySet()) {
						String next = DOMUtil.removeXPathPositionFilters(xps);
						if (next.equals(template1)) {
							xp.add(xps);
						} else {
							if (template2.equals("")) {
								template2 = DOMUtil
										.removeXPathPositionFilters(xps);
							}
							if (next.equals(template2)) {
								xp.add(xps);
							}
						}
					}
					xpath.put(g.getName(), xp);
				}
			}
		}
		return xpath;

	}

	static void extractFromCache(String infolder, String outFolder) {

		PrintWriter out1;

		File folder = new File(infolder);
		new File(outFolder).mkdirs();

		Map<String, Set<String>> xp = new HashMap<String, Set<String>>();

		for (File f : folder.listFiles()) {

			BufferedReader br;

			try {

				br = new BufferedReader(new FileReader(f));

				int lines = 0;

				String nextLine;

				while ((nextLine = br.readLine()) != null) {
					if (!nextLine.equals("")) {
						lines++;
						String t[] = nextLine.split("\t");
						if (!xp.containsKey(t[0])) {
							xp.put(t[0], new HashSet<String>());
							// out1.println(t[0] + "\t"+t[1]);
						}
						xp.get(t[0]).add(t[1]);

					}
				}
				System.out.println(f.getName() + " " + lines);

				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		for (File f : folder.listFiles()) {

			BufferedReader br;

			try {
				out1 = new PrintWriter(new FileWriter(outFolder
						+ File.separator + f.getName()));

				br = new BufferedReader(new FileReader(f));

				String nextLine;

				while ((nextLine = br.readLine()) != null) {
					if (!nextLine.equals("")) {
						String t[] = nextLine.split("\t");
						if (xp.get(t[0]).size() > 1) {
							out1.println(t[0] + "\t" + t[1]);
						}

					}
				}
				br.close();
				out1.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// String testSET =
		// "/Users/annalisa/Documents/CORPORAandDATASETS/ISWCdataset/TESTSET/film_all/film-imdb-5747";
		// String testSET =
		// "/Users/annalisa/Documents/LODIEws/LODIE_data/ISWCdataset/RAW/priority_1/film-imdb-5755";

		// String testSET = args[0];//"./film-imdb-15";
		// String domain = args[1];//"film";
		// String website = args[2];// "imdb";
		// String inFolder = args[3];// "./temp/rPNoBoilerp/film/";
		// String outFolder = args[4];// "./newScoreMultiple/film/";
		// String gazFolder = args[5];//
		// "/Users/annalisa/Documents/CORPORAandDATASETS/ISWCdataset/gaz_cardinality/film/";

		// String testSET = "./film-imdb-15";
		// String domain = "film";
		// String website = "imdb";
		// String inFolder = "./temp/rPNoBoilerp/film/";
		// String outFolder = "./newScoreMultiple/film/";
		// String gazFolder =
		// "/Users/annalisa/Documents/CORPORAandDATASETS/ISWCdataset/gaz_cardinality/film/";

		// String testSET =
		// "/Users/annalisa/Documents/CORPORAandDATASETS/swde-17477/testSET/book/book-abebooks-2000";
		// String domain = "book";
		// String website = "abebooks";
		// String inFolder =
		// "./tempSIGIR5/rPNoBoilerp/book/book-abebooks-2000/";
		// String outFolder = "./SIGIR7results/book/book-abebooks-2000/";
		// String gazFolder =
		// "/Users/annalisa/Documents/CORPORAandDATASETS/ISWCdataset/ISWCgazDBPEDIAonly_cardinality/book/";

		// String testS =
		// "/Users/annalisa/Documents/CORPORAandDATASETS/swde-17477/testSET/book/";

		// TODO SIGIR dataset
		// String domain = "book";
		// String testS = "./pagexpath/SIGIR/"+domain+File.separator;
		//
		// String inF = "./tempSIGIR8/rPNoBoilerp/"+domain+File.separator;
		// String outFolder = "./SIGIR10/WI/"+domain+File.separator;
		// String gazFolder =
		// "/Users/annalisa/Documents/CORPORAandDATASETS/ISWCdataset/ISWCgazDBPEDIAonly_cardinality/"+domain+File.separator;

		// String domain = "book";
		// String testS = "./pagexpath/"+domain+File.separator;
		//
		// String inF = "./temp/rPNoBoilerp/"+domain+File.separator;
		// String outFolder = "./ISWC/WI/"+domain+File.separator;
		// String gazFolder =
		// "/Users/annalisa/Documents/CORPORAandDATASETS/ISWCdataset/ISWCgazDBPEDIAonly_cardinality/"+domain+File.separator;
		//
		String domain = "film";
		String testS = "/Users/annalisa/Documents/LODIEws/LODIE_data/ISWCdataset/DATASET/ISWCpaperXPATHpages/ISWC/"
				+ domain + File.separator;

		String inF = "/Users/annalisa/Documents/LODIEws/LODIE_data/ISWCdataset/DATASET/ISWCpaperTEMPxpath/ISWC/rPNoBoilerp/"
				+ domain + File.separator;
		String outFolder = "/Users/annalisa/Documents/LODIEws/LODIE_data/ISWCdataset/COMPUTEDfinal/ISWC/WI/"
				+ domain + File.separator;
		// this is only needed to check the cardinality of the attributes
		String gazFolder = "/Users/annalisa/Documents/CORPORAandDATASETS/ISWCdataset/ISWCgazDBPEDIAonly_cardinality/"
				+ domain + File.separator;

		File inf = new File(inF);

		for (File f : inf.listFiles()) {
			if (f.isDirectory()) {
				String testSET = testS + f.getName();
				String website = f.getName().split("-")[1];
				String inFolder = inF + f.getName();
				// String outFolder = outF+f.getName();

				// in xpath the key is the attribute, values are all possible
				// xpaths
				Map<String, Set<String>> xpath = new HashMap<String, Set<String>>();

				File gf = new File(inFolder);

				xpath = extractWith2templates(inFolder, gf, gazFolder);

				// *****
				// pretty print, nothing else
				for (Entry<String, Set<String>> x : xpath.entrySet()) {
					System.out.println(x.getKey() + " " + x.getValue().size());
					for (String s : x.getValue()) {
						System.out.println(s);

					}
				}
				// *****

				try {
					// Map<String, Map<String, Set<String>>> res =
					// extractValues(testSET, xpath, gazFolder);
					Map<String, Map<String, Set<String>>> res = extractValuesFromCache(
							testSET, xpath, gazFolder);

					// print results
					if (res != null) {
						for (String attribute : res.keySet()) {
							printAnnotations(outFolder, res.get(attribute),
									domain, website, attribute);
						}
					}
				} catch (XPathExpressionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * @param result
	 *            folder
	 * @param results
	 *            to print
	 * @param domain
	 *            name
	 * @param website
	 *            name
	 * @param attribute
	 *            name
	 */
	public static void printAnnotations(String resFolder,
			Map<String, Set<String>> res, String domain, String website,
			String attribute) {
		System.out.println("printing results in " + resFolder);

		File resultsFolder = new File(resFolder);

		if (!resultsFolder.exists())
			resultsFolder.mkdirs();

		if (res.isEmpty()) {
			System.out.println("no results for " + resFolder);
		} else {
			printResutls(res, resultsFolder + File.separator + domain + "-"
					+ website + "-" + attribute + ".txt");
		}

	}

	/**
	 * private method to print a single file
	 * 
	 * @param resPath
	 * @param filename
	 */
	private static void printResutls(Map<String, Set<String>> resPath,
			String filename) {
		PrintWriter out;

		try {
			out = new PrintWriter(new FileWriter(filename));
			// TODO write header lines
			out.println();
			out.println();

			for (Entry<String, Set<String>> a : resPath.entrySet()) {

				out.print(a.getKey() + "\t" + a.getValue().size());
				if (a.getValue().size() == 0) {
					out.print("\t <NULL>\n");

				} else {
					for (String s : a.getValue()) {
						out.print("\t" + s);

					}
					out.print("\n");
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Map<String, Map<String, Set<String>>> extractValues(
			String folder, Map<String, Set<String>> xpath, String gazFolder)
			throws XPathExpressionException {

		Map<String, Map<String, Map<String, Set<String>>>> attribute_xpath_results = new HashMap<String, Map<String, Map<String, Set<String>>>>();
		Map<String, Set<String>> referenceGazetters = new HashMap<String, Set<String>>();
		Map<String, Integer> referenceGazettersCardinality = new HashMap<String, Integer>();

		// load gazetter per attribute
		// load cardinality info for each gazetter, assume 1 if not available
		File g = new File(gazFolder);
		for (File f : g.listFiles()) {
			if (f.getName().endsWith(".txt")) {
				Set<String> gaz = new HashSet<String>();

				String name = f.getName().substring(0,
						f.getName().lastIndexOf(".txt"));
				int i = 1;
				if (name.contains("-")) {
					String[] n = name.split("-");
					i = Integer.parseInt(n[1]);
					name = n[0];
				}
				referenceGazettersCardinality.put(name, i);

				try {
					gaz = loadFile(f.getAbsolutePath());

					referenceGazetters.put(name, gaz);

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		Map<String, Map<String, Set<String>>> results = new HashMap<String, Map<String, Set<String>>>();
		File f = new File(folder);

		for (String att : xpath.keySet()) {
			// results.put(k, new HashMap<String, Set<String>>());
			attribute_xpath_results.put(att,
					new HashMap<String, Map<String, Set<String>>>());

		}

		if (f.isDirectory()) {
			int c = 0;
			System.out.print("extracting results for " + folder);

			for (File file : f.listFiles()) {
				String name = file.getName();
				// if (name.contains(".htm")) {
				// name = name.substring(0, name.lastIndexOf(".htm"));
				// }

				c++;
				if (c % 100 == 0)
					System.out.println();
				System.out.print(".");
				// System.out.println(file.getAbsolutePath());

				HtmlDocument h = new HtmlDocument(file);
				Document doc = h.getPageDom();

				for (String k : xpath.keySet()) {
					for (String specific_xp : xpath.get(k)) {

						// TODO search in local cache

						// System.out.println(specific_xp);

						NodeList nodes = null;
						try {
							nodes = GenerateXpath.findXpathNodeOnHtmlPage(doc,
									specific_xp);
						} catch (Exception ex) {
							System.err.println(file + " " + specific_xp);
						}

						for (int i = 0; i < nodes.getLength(); i++) {
							String v = nodes.item(i).getNodeValue();
							// I added this line to normalize the text, remove
							// if causes
							// issues
							v = TextOperations.normalizeString(v);

							if (attribute_xpath_results.get(k).get(specific_xp) == null) {
								attribute_xpath_results.get(k).put(specific_xp,
										new HashMap<String, Set<String>>());
							}

							if (v != null) {
								if (attribute_xpath_results.get(k)
										.get(specific_xp).get(name) == null) {
									attribute_xpath_results.get(k)
											.get(specific_xp)
											.put(name, new HashSet<String>());
								}
								attribute_xpath_results.get(k).get(specific_xp)
										.get(name).add(v);

								// if (results.get(k)==null){
								// results.put(k, new HashMap<String,
								// Set<String>>());
								// }
								// if (results.get(k).get(name)==null){
								// results.get(k).put(name, new
								// HashSet<String>());
								// }
								//
								// results.get(k).get(name).add(v);
							}
						}

						// if
						// (nodes.getLength()>0)System.out.println(k+"****"+name+"*******"+attribute_xpath_results.get(k).get(specific_xp).get(name));

					}
				}
			}

			// System.out.println();
		}

		System.out.println("****finished extracting results*******");

		// based on extracted values, decide xpath to keep
		for (Entry<String, Map<String, Map<String, Set<String>>>> e : attribute_xpath_results
				.entrySet()) {
			System.out.println("generating xpath for " + e.getKey());
			System.out.println("xpaths: " + e.getValue().keySet());

			// TODO use a confidence level and do no use any if score is below
			// that
			SortedMap<String, Double> wxp = decideWinningXpath(e.getValue(),
					referenceGazetters.get(e.getKey()),
					referenceGazettersCardinality.get(e.getKey()),
					f.listFiles().length);

			if (wxp != null) {
				try {
					// add first xpath
					results.put(
							e.getKey(),
							attribute_xpath_results.get(e.getKey()).get(
									wxp.firstKey()));

					// if multiple values are expected
					if (referenceGazettersCardinality.get(e.getKey()) > 1) {

						Iterator<String> it = wxp.keySet().iterator();

						while (it.hasNext()) {
							Map<String, Set<String>> xpr = attribute_xpath_results
									.get(e.getKey()).get(it.next());

							if (results.get(e.getKey()) == null) {
								results.put(e.getKey(),
										new HashMap<String, Set<String>>());
							}
							for (String name : xpr.keySet()) {
								if (results.get(e.getKey()).get(name) == null) {
									results.get(e.getKey()).put(name,
											new HashSet<String>());
								}

								results.get(e.getKey()).get(name)
										.addAll(xpr.get(name));
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();

					System.err.println(f + " " + e.getKey() + " "
							+ e.getValue());

					System.err.println(attribute_xpath_results.get(e.getKey()));

				}
			}
			// decide winning xpath

			// SAVE RESULTS IN THE RESULTS VARIABLE
			// if (results.get(e.getKey())==null){
			// results.put(e.getKey(), new HashMap<String, Set<String>>());
			// }
			// results.get(e.getKey()),
			// attribute_xpath_results.get(e.getKey()).get(wxp.firstKey()));
		}

		return results;

	}

	// TODO folder is the page as xpath-value
	public static Map<String, Map<String, Set<String>>> extractValuesFromCache(
			String folder, Map<String, Set<String>> xpath, String gazFolder)
			throws XPathExpressionException {

		Map<String, Map<String, Map<String, Set<String>>>> attribute_xpath_results = new HashMap<String, Map<String, Map<String, Set<String>>>>();
		Map<String, Set<String>> referenceGazetters = new HashMap<String, Set<String>>();
		Map<String, Integer> referenceGazettersCardinality = new HashMap<String, Integer>();

		// load gazetter per attribute
		// load cardinality info for each gazetter, assume 1 if not available
		File g = new File(gazFolder);
		for (File f : g.listFiles()) {
			if (f.getName().endsWith(".txt")) {
				Set<String> gaz = new HashSet<String>();

				String name = f.getName().substring(0,
						f.getName().lastIndexOf(".txt"));
				int i = 1;
				if (name.contains("-")) {
					String[] n = name.split("-");
					i = Integer.parseInt(n[1]);
					name = n[0];
				}
				referenceGazettersCardinality.put(name, i);

				try {
					gaz = loadFile(f.getAbsolutePath());

					referenceGazetters.put(name, gaz);

				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		Map<String, Map<String, Set<String>>> results = new HashMap<String, Map<String, Set<String>>>();
		File f = new File(folder);

		for (String att : xpath.keySet()) {
			// results.put(k, new HashMap<String, Set<String>>());
			attribute_xpath_results.put(att,
					new HashMap<String, Map<String, Set<String>>>());

		}

		if (f.isDirectory()) {
			int c = 0;
			System.out.print("extracting results for " + folder);

			for (File file : f.listFiles()) {
				String name = file.getName();
				// if (name.contains(".htm")) {
				// name = name.substring(0, name.lastIndexOf(".htm"));
				// }

				c++;
				if (c % 100 == 0)
					System.out.println();
				System.out.print(".");
				// System.out.println(file.getAbsolutePath());

				for (String k : xpath.keySet()) {
					// for(String specific_xp : xpath.get(k)){

					BufferedReader br;

					try {

						br = new BufferedReader(new FileReader(file));
						String nextLine;

						while ((nextLine = br.readLine()) != null) {
							if (!nextLine.equals("")) {
								String t[] = nextLine.split("\t");
								if (xpath.get(k).contains(t[0])) {

									// TODO save value
									String v = TextOperations
											.normalizeString(t[1]);
									if (attribute_xpath_results.get(k)
											.get(t[0]) == null) {
										attribute_xpath_results
												.get(k)
												.put(t[0],
														new HashMap<String, Set<String>>());
									}

									if (v != null) {
										if (attribute_xpath_results.get(k)
												.get(t[0]).get(name) == null) {
											attribute_xpath_results
													.get(k)
													.get(t[0])
													.put(name,
															new HashSet<String>());
										}
										attribute_xpath_results.get(k)
												.get(t[0]).get(name).add(v);

									}

								}

							}
						}

						br.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}

		System.out.println("****finished extracting results*******");

		// based on extracted values, decide xpath to keep
		for (Entry<String, Map<String, Map<String, Set<String>>>> e : attribute_xpath_results
				.entrySet()) {
			System.out.println("generating xpath for " + e.getKey());
			System.out.println("xpaths: " + e.getValue().keySet());

			// TODO use a confidence level and do no use any if score is below
			// that
			SortedMap<String, Double> wxp = null;
			try {
				wxp = decideWinningXpath(e.getValue(),
						referenceGazetters.get(e.getKey()),
						referenceGazettersCardinality.get(e.getKey()),
						f.listFiles().length);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.err.println(" key = " + e.getKey());

				System.err.println(" value = " + e.getValue());
				System.err.println(referenceGazetters.get(e.getKey()));
				System.err
						.println(referenceGazettersCardinality.get(e.getKey()));
				System.err.println(f.listFiles().length);
			}

			if (wxp != null) {
				try {
					// add first xpath
					results.put(
							e.getKey(),
							attribute_xpath_results.get(e.getKey()).get(
									wxp.firstKey()));

					// if multiple values are expected
					if (referenceGazettersCardinality.get(e.getKey()) > 1) {

						Iterator<String> it = wxp.keySet().iterator();

						while (it.hasNext()) {
							Map<String, Set<String>> xpr = attribute_xpath_results
									.get(e.getKey()).get(it.next());

							if (results.get(e.getKey()) == null) {
								results.put(e.getKey(),
										new HashMap<String, Set<String>>());
							}
							for (String name : xpr.keySet()) {
								if (results.get(e.getKey()).get(name) == null) {
									results.get(e.getKey()).put(name,
											new HashSet<String>());
								}

								results.get(e.getKey()).get(name)
										.addAll(xpr.get(name));
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();

					System.err.println(f + " " + e.getKey() + " "
							+ e.getValue());

					System.err.println(attribute_xpath_results.get(e.getKey()));

				}
			}
			// decide winning xpath

			// SAVE RESULTS IN THE RESULTS VARIABLE
			// if (results.get(e.getKey())==null){
			// results.put(e.getKey(), new HashMap<String, Set<String>>());
			// }
			// results.get(e.getKey()),
			// attribute_xpath_results.get(e.getKey()).get(wxp.firstKey()));
		}

		return results;

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

	public static DataType inferMajorityType(Set<String> items) {
		Map<DataType, Integer> typeCountings = new HashMap<DataType, Integer>();
		DataType maxT = null;

		for (String s : items) {
			DataType t = DataTypeClassifier.classify(s);
			if (typeCountings.containsKey(t)) {
				int c = typeCountings.get(t);
				typeCountings.put(t, c + 1);
			} else {
				typeCountings.put(t, 1);
			}
		}
		// System.out.println(items);
		System.out.println(typeCountings);
		if (typeCountings.size() == 1) {
			maxT = typeCountings.keySet().iterator().next();
		} else if (typeCountings.size() > 1) {
			double max = 0;
			for (Entry<DataType, Integer> e : typeCountings.entrySet()) {
				double current = (double) e.getValue() / items.size();
				if (current > max) {
					maxT = e.getKey();
					max = current;
				}
			}
		}

		return maxT;
	}

	public static double calculateContainment(Set<String> values,
			Set<String> gaz, int totalPages) {

		// int contained = 0;

		// for (String k:values ){
		//
		// if (gaz.contains(k))
		// contained++;
		// }
		// double perc = (double)contained/values.size();

		// Set<String> total =SetOperations.union(values, gaz);

		Set<String> inter = SetOperations.intersection(values, gaz);

		double acc = (double) inter.size() / values.size()
				* (double) values.size() / totalPages;

		// System.out.println(contained+" / "+p1.size()+" = "+perc
		// +" Gaz size =  "+p2.size());

		return acc;

	}

	public static SortedMap<String, Double> decideWinningXpath(
			Map<String, Map<String, Set<String>>> candidates, Set<String> gaz,
			int referenceGazettersCardinality, int totalPages) {

		SortedMap<String, Double> xpathScore = new TreeMap<String, Double>();
		// Map<String,Set<String>> relaxed = new HashMap<String,Set<String>>();

		System.out.println("datatype for gazetter");
		DataType gazType = inferMajorityType(gaz);

		for (Entry<String, Map<String, Set<String>>> ea : candidates.entrySet()) {

			// System.out.println(ea.getKey());
			// System.out.println(ea.getValue());
			Set<String> v = new HashSet<String>();
			for (Set<String> ss : ea.getValue().values()) {
				v.addAll(ss);
			}
			// TODO change v, v
			System.out.println("datatype for " + ea.getKey());
			DataType resultType = inferMajorityType(v);

			if (resultType == gazType) {
				xpathScore.put(ea.getKey(),
						calculateContainment(v, gaz, totalPages));
				System.out.println("CORRECT TYPE for " + ea.getKey() + " "
						+ xpathScore.get(ea.getKey()) + " " + resultType + " "
						+ gazType);
			} else {
				System.out.println("WRONG TYPE for " + ea.getKey() + " "
						+ gazType + " <> " + resultType);
			}
		}

		// if multiple values are expected
		// if(referenceGazettersCardinality.get(e.getKey())>1){
		//
		// Iterator<String> it = wxp.keySet().iterator();
		//
		// for (int i=0; i<referenceGazettersCardinality.get(e.getKey()); i++){
		// Map<String, Set<String>> xpr =
		// attribute_xpath_results.get(e.getKey()).get(it.next());
		//
		// if (results.get(e.getKey())==null){
		// results.put(e.getKey(), new HashMap<String, Set<String>>());
		// }
		// for (String name : xpr.keySet()){
		// if (results.get(e.getKey()).get(name)==null){
		// results.get(e.getKey()).put(name, new HashSet<String>());
		// }
		//
		// results.get(e.getKey()).get(name).addAll(xpr.get(name));
		// }
		// }
		// }

		// TODO from here - relaxed strategy
		// String relaxedXp = DOMUtil.removeXPathPositionFilters(ea.getKey());
		// if (!relaxed.containsKey(relaxedXp))
		// relaxed.put(relaxedXp, new HashSet<String>());
		// relaxed.get(relaxedXp).add(ea.getKey());

		// for (Entry<String, Set<String>> r : relaxed.entrySet()){
		//
		// Set<String> pages = new HashSet<String> ();
		//
		//
		// for (String x : r.getValue()){
		//
		// pages.addAll(candidates.get(x).keySet());
		// System.out.println(x+" "+candidates.get(x).keySet().size());
		// double score = xpathScore.get(x)+(double)
		// candidates.get(x).keySet().size()/totalPages;
		// System.out.println(x + " " + score);
		// xpathScore.put(x, score);
		// }
		//
		// System.out.println( r.getKey()+ " = "+pages.size());
		//
		// }

		// TODO up to here - relaxed strategy

		// decide winning xpath
		xpathScore = sortMap(xpathScore);
		System.out.println("xpath of compatible types: " + xpathScore);

		if (xpathScore.isEmpty())
			return null;
		return xpathScore;

	}

	public static Set<String> loadFile(String wordFilePath) throws IOException {

		Set<String> words = new HashSet<String>();

		BufferedReader input = new BufferedReader(new FileReader(wordFilePath));
		String line;
		line = input.readLine();

		while (line != null) {
			line = line.trim();
			if (!line.equals("")) {
				words.add(line.toLowerCase());
			}

			line = input.readLine();

		}
		input.close();
		return words;
	}

}
