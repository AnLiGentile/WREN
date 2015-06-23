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

import uk.ac.shef.dcs.oak.operations.SetOperations;
import uk.ac.shef.dcs.oak.operations.TextOperations;
import uk.ac.shef.dcs.oak.operations.ValueComparator;
import uk.ac.shef.dcs.oak.util.DOMUtil;
import uk.ac.shef.dcs.oak.util.HtmlDocument;

/**
 * @author annalisa
 *
 */
public class GenerateXpathMultipleValues extends Thread {

	private static Logger l4j = Logger
			.getLogger(GenerateXpathMultipleValues.class);

	private static Set<String> gaz;
	PrintWriter out;
	String outF;
	File folder;
	Map<String, Set<String>> xpathDensity;
	Map<String, Set<String>> relaxedXpathMap;
	int pagesUsedForTraining;
	Pair<String, Double> winnerR;

	boolean finished = false;

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public Pair<String, Double> getWinnerR() {
		return winnerR;
	}

	public void setWinnerR(Pair<String, Double> winnerR) {
		this.winnerR = winnerR;
	}

	public GenerateXpathMultipleValues(File folder, String resFolder) {

		try {

			this.outF = resFolder + File.separator + folder.getName() + ".txt";
			this.folder = folder;
			this.out = new PrintWriter(new FileWriter(this.outF));
			this.xpathDensity = new HashMap<String, Set<String>>();
			this.pagesUsedForTraining = 0;
			this.relaxedXpathMap = new HashMap<String, Set<String>>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void close() {
		this.out.close();
	}

	/**
	 * This function extract all nodes in a cached html file which match with
	 * the class gazetter
	 * 
	 * @param f
	 *            the cached html page
	 * @return a map where the key is the xpath and the value is the value of
	 *         the node identified from the xpath
	 */
	public static Map<String, String> getXpathForTextNodesFromPage(File f) {
		Map<String, String> xp = new HashMap<String, String>();

		long START = System.currentTimeMillis();

		HtmlDocument h = new HtmlDocument(f);

		// String s = FileUtils.readFileContent(f);
		// l4j.info(s);
		// h.setPageHtml(s);
		Document doc = h.getPageDom();
		// org.jsoup.nodes.Document doc = h.getPageDom();

		// DOMReader dr = new DOMReader();
		// org.dom4j.Document read = dr.read(doc);

		// System.out.println("**getAnnotationsFromPage**fetching candidate Xpaths from page "
		// + f.getAbsolutePath());

		try {
			NodeList nodesORG = GenerateXpath.findXpathNodeOnHtmlPage(doc,
					"//text()");

			for (int i = 0; i < nodesORG.getLength(); i++) {
				Node n = nodesORG.item(i);
				String v = n.getNodeValue();
				// I added this line to normalize the text, remove if causes
				// issues
				v = TextOperations.normalizeString(v);
				if (v != null) {
					if (v.length() > 1) {

						String xpn = DOMUtil.getXPath(nodesORG.item(i));

						// System.out.println("**getAnnotationsFromPage***matching node "
						// + v.trim() + " " + xpn);
						xp.put(xpn, v.trim());
					}
				}
				// }
			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		return xp;

	}

	public Map<String, Set<String>> xpathDensity(String folder) {
		Map<String, Set<String>> xpathDensity = new HashMap<String, Set<String>>();
		// Set<String> boilerplate = new HashSet<String> ();

		int traningSize = 15;

		File f = new File(folder);

		if (f.isDirectory()) {
			System.out.print("generating xpath for " + folder);

			File[] pages = f.listFiles();
			List<File> listPages = Arrays.asList(pages);

			Collections.shuffle(listPages);

			int initialTraining = 0;
			boolean changeSize = true;
			// int previousSize = 10;
			// System.out.println("size = "+size);
			// System.out.println("previous size = "+previousSize);

			int maxTrainingToUse = listPages.size() / 2;

			while (changeSize) {
				int size = xpathDensity.size();
				int nextLimit = initialTraining + traningSize;
				if (nextLimit > maxTrainingToUse) {
					nextLimit = maxTrainingToUse;
				}
				for (int i = initialTraining; i < nextLimit; i++) {

					// if (i % 100 == 0)
					// System.out.println();
					// System.out.print(".");

					Map<String, String> m = getXpathForTextNodesFromPage(listPages
							.get(i));
					// this.out.println(m);
					for (String x : m.keySet()) {
						String xp = x;
						if (!xpathDensity.containsKey(xp))
							xpathDensity.put(xp, new HashSet<String>());
						xpathDensity.get(xp).add(m.get(x).toLowerCase());

						// if (!xpathDensity.get(xp).isEmpty()){
						// System.out.println(xp +
						// " --> "+xpathDensity.get(xp));
						// }

						// if (xpathDensity.get(xp).size()==1){
						// String v = xpathDensity.get(xp).iterator().next();
						// if(v.equals(m.get(xp).toLowerCase())){
						// boilerplate.add(xp);
						// }else{
						// xpathDensity.get(xp).add(m.get(x).toLowerCase());
						// if (boilerplate.contains(xp))
						// boilerplate.remove(xp);
						// }
						// }else{
						// xpathDensity.get(xp).add(m.get(x).toLowerCase());
						// if (boilerplate.contains(xp))
						// boilerplate.remove(xp);}
					}

				}
				if (size == xpathDensity.size()) {
					changeSize = false;
					this.pagesUsedForTraining = nextLimit;
				}

				initialTraining = nextLimit;
				nextLimit = initialTraining + traningSize;
				System.out.print("." + nextLimit);

				if (nextLimit > maxTrainingToUse) {
					nextLimit = maxTrainingToUse;
				}
				// System.out.println("size = "+size);
				// System.out.println("previous size = "+previousSize);
			}

			System.out.println();
		}
		// System.out.println("xpathDensity = "+ xpathDensity.size());
		// System.out.println("boilerplate = "+ boilerplate.size());
		// for(String b :boilerplate){
		// xpathDensity.remove(b);
		// }
		// System.out.println("xpathDensity = "+ xpathDensity.size());

		return xpathDensity;

	}

	public static Map<String, Set<String>> extractValues(String folder,
			String xpath) throws XPathExpressionException {
		Map<String, Set<String>> xpathDensity = new HashMap<String, Set<String>>();
		File f = new File(folder);
		if (f.isDirectory()) {
			int c = 0;
			System.out.print("extracting results");

			for (File file : f.listFiles()) {
				String name = file.getName();
				if (name.contains(".htm")) {
					name = name.substring(0, name.lastIndexOf(".htm"));
				}

				c++;
				// if (c % 100 == 0)
				// System.out.println();
				// System.out.print(".");

				HtmlDocument h = new HtmlDocument(file);
				Document doc = h.getPageDom();
				// org.jsoup.nodes.Document doc = h.getPageDom();

				NodeList nodes = GenerateXpath.findXpathNodeOnHtmlPage(doc,
						xpath);

				for (int i = 0; i < nodes.getLength(); i++) {
					String v = nodes.item(i).getNodeValue();
					// I added this line to normalize the text, remove if causes
					// issues
					v = TextOperations.normalizeString(v);

					if (xpathDensity.get(name) == null) {
						xpathDensity.put(name, new HashSet<String>());
					}

					if (v != null) {
						xpathDensity.get(name).add(v);
					}

				}
				//
				// Map<String, String> m = getXpathForTextNodesFromPage(file);
				// for (String x : m.keySet()){
				// String xp =x;
				// if (relax){
				// xp = DOMUtil.removeXPathPositionFilters(x);}
				// if (!xpathDensity.containsKey(xp))
				// xpathDensity.put(xp, new HashSet<String> ());
				// xpathDensity.get(xp).add(m.get(x).toLowerCase());
				// }
			}
			System.out.println();
		}
		return xpathDensity;

	}

	public static Map<String, Set<String>> xpathFeatureCollector(String folder) {
		Map<String, Set<String>> xpathDensity = new HashMap<String, Set<String>>();
		File f = new File(folder);
		if (f.isDirectory()) {
			for (File file : f.listFiles()) {
				System.out.println("processing " + file.getName());
				Map<String, String> m = getXpathForTextNodesFromPage(file);
				for (String xp : m.keySet()) {
					if (!xpathDensity.containsKey(xp))
						xpathDensity.put(xp, new HashSet<String>());
					xpathDensity.get(xp).add(m.get(xp).toLowerCase());
				}
			}
		}
		return xpathDensity;

	}

	public Set<String> relaxXpath(Set<String> xpaths) {
		Set<String> relaxXpath = new HashSet<String>();

		for (String xp : xpaths) {

			String relaxedXp = DOMUtil.removeXPathPositionFilters(xp);
			relaxXpath.add(relaxedXp);
			// l4j.info(xp +" "+ relaxedXp);

		}

		return relaxXpath;

	}

	/**
	 * 
	 * @param strategyName
	 * @param strategyXpaths
	 * @param htmlFolder
	 * @param experimentFolder
	 * @param domain
	 * @param website
	 * @param attribute
	 */
	public static void printAnnotations(String strategyName,
			Map<String, Set<String>> res, String experimentFolder,
			String domain, String website, String attribute) {

		String resFolder = "";

		resFolder = experimentFolder + File.separator + "annotation_results_"
				+ strategyName + File.separator + domain;
		File resultsFolder = new File(resFolder);

		if (!resultsFolder.exists())
			resultsFolder.mkdirs();

		if (res.isEmpty()) {
			System.out
					.println("no results for " + domain + website + attribute);
		} else {
			printResutls(res, resultsFolder + File.separator + domain + "-"
					+ website + "-" + attribute + ".txt");
		}

	}

	// public static void printXpath(String strategyName,
	// SortedMap<String, Double> res, String experimentFolder,
	// String domain, String website, String attribute, int size) {
	//
	// String resFolder = "";
	//
	// resFolder = experimentFolder + File.separator + "xpath_results_"
	// + strategyName + File.separator + domain;
	// File resultsFolder = new File(resFolder);
	//
	// if (!resultsFolder.exists())
	// resultsFolder.mkdirs();
	//
	// if (res.isEmpty()) {
	// System.out
	// .println("no results for " + domain + website + attribute);
	// } else {
	// printResXpath(res, resultsFolder + File.separator + domain + "-"
	// + website + "-" + attribute + ".txt", size);
	// }
	//
	// }

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

	/**
	 * private method to print a single file
	 * 
	 * @param resPath
	 * @param filename
	 */
	// private static void printResXpath(SortedMap<String, Double> resPath,
	// String filename, int size) {
	// PrintWriter out;
	//
	// try {
	// out = new PrintWriter(new FileWriter(filename));
	// out.println("Xpath calculated over " + size + " pages");
	// out.println();
	//
	// for (Entry<String, Double> s : resPath.entrySet()) {
	// out.println(s.getKey() + "\t"
	// + String.format("%.10f", s.getValue()));
	// }
	//
	// out.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }

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
		return words;
	}

	public Pair<String, Double> generateWinner() {

		// KeyValuePair<String, Double> winner = new KeyValuePair<String,
		// Double> ();

		Map<String, Set<String>> m = this.xpathDensity(this.folder
				.getAbsolutePath());

		Map<String, Set<String>> mf = filterXpath(m);

		System.out.println("original xpath " + m.size()
				+ ", after removing boilerplate " + mf.size());
		Map<String, Set<String>> mfmatch = filterXpath(mf, gaz);

		// TODO switch line use use relax
		Map<String, Set<String>> mr = this.relaxMap(mfmatch);
		// Map<String, Set<String>> mr = mfmatch;

		l4j.info(mr);

		// SortedMap<String, Double> candidatesR = pickCandidates(mr, gaz);

		Pair<String, Double> winnerR = null;

		if (!mfmatch.isEmpty()) {
			SortedMap<String, Double> candidatesR = pickCandidates(mr, gaz);

			System.out.println("training size: " + this.pagesUsedForTraining);
			l4j.info("candidates " + candidatesR.size() + " out of "
					+ mr.size());

			// System.out.println("candidates "+candidatesR.size());

			// double margin =Integer.MAX_VALUE;
			double winner = 0;
			String winnerXR = "";

			System.out.println("CANDIDATES");

			for (Entry<String, Double> e : candidatesR.entrySet()) {
				System.out.println(e.getKey() + e.getValue());
			}

			// score
			for (Entry<String, Double> candid : candidatesR.entrySet()) {
				double currentMargin;

				if (this.pagesUsedForTraining < mr.get(candid.getKey()).size()) {
					currentMargin = (double) this.pagesUsedForTraining
							/ (double) mr.get(candid.getKey()).size();
				} else {
					currentMargin = (double) mr.get(candid.getKey()).size()
							/ (double) this.pagesUsedForTraining;
				}
				l4j.info("pagesUsedForTraining = " + this.pagesUsedForTraining);
				l4j.info("values for current XPR = "
						+ mr.get(candid.getKey()).size());

				if (((double) candid.getValue() / (double) mr.get(
						candid.getKey()).size())
						+ currentMargin > winner) {
					winner = ((double) candid.getValue() / (double) mr.get(
							candid.getKey()).size());

					l4j.info("winner = " + winner + " " + currentMargin);
					winner = winner + currentMargin;
					winnerXR = candid.getKey();
				}
				l4j.info(candid.getKey() + "\t(" + candid.getValue() + "/"
						+ mr.get(candid.getKey()).size() + ")\t" + "\t("
						+ this.pagesUsedForTraining + "-->" + currentMargin
						+ ")\t" + mr.get(candid.getKey()));
			}

			System.out.println("Winner XPR \t" + winnerXR + "\t" + winner);
			System.out.println("Winner XP \t"
					+ this.relaxedXpathMap.get(winnerXR));

			winnerR = new ImmutablePair(winnerXR, winner);
		}

		return winnerR;
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

		GenerateXpathMultipleValues.genereteXpath(domain, gazFolder, resFolder,
				attribute, repetitions);

	}

	public static void genereteXpath(String domain, String gazFolder,
			String resFolder, String attribute, int repetitions) {

		// Set<String> gaz = new HashSet<String> ();
		gaz = new HashSet<String>();
		try {
			gaz = loadFile(gazFolder);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		resFolder = resFolder + File.separator + attribute;
		File f = new File(domain);
		new File(resFolder).mkdirs();
		for (File folder : f.listFiles()) {
			if (folder.isDirectory()) {
				boolean reliable = true;

				// GenerateXpath firstGx = new GenerateXpath(folder, resFolder);
				// Thread thread1 = new Thread(firstGx);
				// thread1.start();

				// if (firstGx.pagesUsedForTraining<folder.listFiles().length)
				// {
				GenerateXpathMultipleValues gxlist[] = new GenerateXpathMultipleValues[repetitions];
				for (int i = 0; i < repetitions; i++) {

					GenerateXpathMultipleValues gx = new GenerateXpathMultipleValues(
							folder, resFolder);
					Thread thread = new Thread(gx);
					thread.setName(folder.getName());
					gxlist[i] = gx;
					// gxlist.add(gx);
					thread.start();

					// Pair<String, Double> currentWinnerR =
					// gx.generateWinner();
					// gx.run();
				}

				//

				for (int i = 0; i < repetitions; i++) {

					try {
						gxlist[i].join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

				// Iterator<GenerateXpath> it1 = gxlist.iterator();
				// while(it1.hasNext()){
				// Thread thread = new Thread(it1.next());
				// thread.start();
				// }

				boolean allFinished = false;

				while (!allFinished) {
					allFinished = true;
					// if (!firstGx.isFinished()){
					// allFinished=false;
					// System.out.println(firstGx +" is alive");
					// }

					for (int i = 0; i < gxlist.length; i++) {

						if (!gxlist[i].isFinished()) {
							allFinished = false;
							break;
						}
					}
					// System.out.println("Threads still running");

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}

				// Pair<String, Double> winnerR = firstGx.getWinnerR();

				List<Double> scores = new ArrayList<Double>();
				// scores[0] = winnerR.getValue();

				Map<String, Double> win = new HashMap<String, Double>();
				Map<String, Set<String>> winXP = new HashMap<String, Set<String>>();

				for (int i = 0; i < gxlist.length; i++) {

					Pair<String, Double> currentWinnerR = gxlist[i]
							.getWinnerR();
					if (currentWinnerR != null) {
						if (win.get(currentWinnerR.getKey()) == null) {
							win.put(currentWinnerR.getKey(),
									currentWinnerR.getValue());
							winXP.put(currentWinnerR.getKey(),
									gxlist[i].relaxedXpathMap
											.get(currentWinnerR.getKey()));

						} else {
							if (win.get(currentWinnerR.getKey()) < currentWinnerR
									.getValue()) {
								win.put(currentWinnerR.getKey(),
										currentWinnerR.getValue());
								winXP.put(currentWinnerR.getKey(),
										gxlist[i].relaxedXpathMap
												.get(currentWinnerR.getKey()));
							}
						}
						scores.add(currentWinnerR.getValue());
					}
				}
				Entry<String, Double> winnerR = null;
				if (!win.isEmpty()) {
					if (win.size() > 1) {
						reliable = false;
					} else {
						winnerR = win.entrySet().iterator().next();
					}
				}
				// }
				if (reliable) {
					if (winnerR != null) {
						DescriptiveStatistics d = new DescriptiveStatistics();
						for (double s : scores)
							d.addValue(s);

						System.out
								.println("***************RELIABLE**************"
										+ folder);
						for (double ss : scores) {
							System.out.print(ss + " ");
						}
						System.out.println();
						System.out.println("mean: " + d.getMean() + " stDev: "
								+ d.getStandardDeviation());
						System.out
								.println("*************************************"
										+ winnerR);

						System.out.println("Winner XPR \t" + winnerR);
						System.out.println("Winner XP \t"
								+ gxlist[0].relaxedXpathMap.get(winnerR
										.getKey()));
						for (double ss : scores) {
							gxlist[0].out.print(ss + " ");
						}
						gxlist[0].out.println();
						try {
							gxlist[0].out.print("mean: " + d.getMean());
							gxlist[0].out.println(" stDev: "
									+ d.getStandardDeviation());
						} catch (Exception e) {
							System.err.println("cannot get mean and stdev for "
									+ folder.getName());
						}
						// TODO relaxed solution

						for (String s : gxlist[0].relaxedXpathMap.get(winnerR
								.getKey())) {
							// gx.out.println("xpath.get(file).add(\"" + s +
							// "\");");
							gxlist[0].out.println(s);
						}

						// TODO just a quick fix to try non-relaxed solution
						// gxlist[0].out.println(winnerR.getKey());

						gxlist[0].close();
					} else {
						gxlist[0].close();
						new File(gxlist[0].outF).delete();

					}
				} else {
					SortedMap<String, Double> sortwin = sortMap(win);

					System.out
							.println("***************UNRELIABLE************** "
									+ folder);
					gxlist[0].out
							.println("***************UNRELIABLE**************");
					System.out.println("******************");

					for (Entry<String, Double> e : sortwin.entrySet()) {

						System.out.println(e.getKey() + "\t" + e.getValue());
						System.out.println(winXP.get(e.getKey()));

						gxlist[0].out.print(e.getKey() + "\t" + e.getValue()
								+ "\t");
						gxlist[0].out.println(winXP.get(e.getKey()));

					}
					gxlist[0].out.close();
					System.out.println("******************");

					// for (int i=0; i<gxlist.length; i++){
					// System.out.println(gxlist[i].getWinnerR());
					// System.out.println(gxlist[i].relaxedXpathMap.get(gxlist[i].getWinnerR().getKey()));
					//
					// }
				}
			}
		}
	}

	private static SortedMap<String, Double> pickCandidates(
			Map<String, Set<String>> m, Set<String> words) {
		Map<String, Double> cand = new HashMap<String, Double>();

		for (String k : m.keySet()) {

			Set<String> inters = SetOperations.intersection(m.get(k), words);

			if (inters.size() > 0) {
				// TODO changed previous line
				cand.put(k, (double) inters.size());
				System.out.println(k + "------->" + inters);
				// cand.put(k, (double) inters.size()/m.get(k).size());
			}
		}

		ValueComparator bvc = new ValueComparator(cand);
		SortedMap<String, Double> sortedNumberOfWords = new TreeMap<String, Double>(
				bvc);
		sortedNumberOfWords.putAll(cand);

		// System.out.println(sortedNumberOfWords);
		return sortedNumberOfWords;
	}

	private static Map<String, Set<String>> reduceToCandidates(
			Map<String, Set<String>> m, Set<String> words) {
		Map<String, Set<String>> cand = new HashMap<String, Set<String>>();

		for (String k : m.keySet()) {

			Set<String> inters = SetOperations.intersection(m.get(k), words);

			if (inters.size() > 0)
				cand.put(k, inters);
		}

		return cand;
	}

	private Map<String, Set<String>> relaxMap(Map<String, Set<String>> m) {

		// generate map with relaxed xpath
		Map<String, Set<String>> mr = new HashMap<String, Set<String>>();
		// for each realaxed xpath maintans the set af all the ones with
		// posizion fillers
		Map<String, Set<String>> rxpath = new HashMap<String, Set<String>>();

		for (String x : m.keySet()) {
			String xpr = DOMUtil.removeXPathPositionFilters(x);

			if (!mr.containsKey(xpr)) {
				mr.put(xpr, new HashSet<String>());
				rxpath.put(xpr, new HashSet<String>());
			}
			mr.get(xpr).addAll(m.get(x));
			rxpath.get(xpr).add(x);

		}

		this.relaxedXpathMap = rxpath;
		return mr;

	}

	private static Map<String, Set<String>> mapRelacedToExplicit(
			Map<String, Set<String>> m) {

		// generate map with relaxed xpath
		Map<String, Set<String>> mr = new HashMap<String, Set<String>>();
		Map<String, Set<String>> rxpath = new HashMap<String, Set<String>>();// for
																				// each
																				// realaxed
																				// xpath
																				// maintans
																				// the
																				// set
																				// af
																				// all
																				// the
																				// ones
																				// with
																				// posizion
																				// fillers

		for (String x : m.keySet()) {
			String xpr = DOMUtil.removeXPathPositionFilters(x);

			if (!mr.containsKey(xpr)) {
				mr.put(xpr, new HashSet<String>());
				rxpath.put(xpr, new HashSet<String>());
			}
			mr.get(xpr).addAll(m.get(x));
			rxpath.get(xpr).add(x);

		}

		return rxpath;

	}

	/**
	 * filter the xpath map using basic website stats
	 * 
	 * @param m
	 * @return
	 */
	private static Map<String, Set<String>> filterXpath(
			Map<String, Set<String>> m) {

		Map<String, Set<String>> filtered = new HashMap<String, Set<String>>();

		for (String x : m.keySet()) {
			if (m.get(x).size() > 1) {
				filtered.put(x, m.get(x));
				// System.out.println(x + " --> "+m.get(x));
			}
		}

		return filtered;
	}

	private static Map<String, Set<String>> filterXpath(
			Map<String, Set<String>> m, Set<String> gaz) {

		Map<String, Set<String>> filtered = new HashMap<String, Set<String>>();

		for (String x : m.keySet()) {
			Set<String> inters = SetOperations.intersection(m.get(x), gaz);

			if (inters.size() > 0)
				filtered.put(x, m.get(x));

		}

		return filtered;
	}

	public void run() {
		Pair<String, Double> winnerR = this.generateWinner();
		this.setWinnerR(winnerR);
		this.finished = true;

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
}
