package uk.ac.shef.dcs.oak.xpath.processors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import model.Page;

import org.aksw.rex.crawler.CrawlIndex;
import org.apache.any23.extractor.html.TagSoupParser;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.shef.dcs.oak.operations.TextOperations;
import uk.ac.shef.dcs.oak.util.CleanHtmlPAge;
import uk.ac.shef.dcs.oak.util.DOMUtil;
import uk.ac.shef.dcs.oak.util.HtmlDocument;

/**
 * @author annalisa
 * This class provides method to represent HTML pages as a set of xpath-value pairs
 * In the current implementation only text nodes are retained
 */
public class ReducePagesToXpath {

	private static Logger l4j = Logger.getLogger(ReducePagesToXpath.class);

	private static Set<String> gaz;
	PrintWriter out;

	PrintWriter allXpathOutput;
	PrintWriter matchingXpathOutput;

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

	public ReducePagesToXpath(File folder) {

		this.folder = folder;

		this.xpathDensity = new HashMap<String, Set<String>>();

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
			NodeList nodesORG = findXpathNodeOnHtmlPage(doc, "//text()");

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
	
	
	/**
	 * This function extract all nodes in a cached html document which match with
	 * the class gazetter
	 * 
	 * @param doc
	 *            the cached html page as Document
	 * @return a map where the key is the xpath and the value is the value of
	 *         the node identified from the xpath
	 */
	public static Map<String, String> getXpathForTextNodesFromPage(Document doc) {
		Map<String, String> xp = new HashMap<String, String>();

		long START = System.currentTimeMillis();

		try {
			NodeList nodesORG = findXpathNodeOnHtmlPage(doc, "//text()");

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

	public static void generateStructure(String folder, String outFolder) {

		File f = new File(folder);

		if (f.isDirectory()) {
			System.out.print("generating xpath for " + folder);

			File[] pages = f.listFiles();
			List<File> listPages = Arrays.asList(pages);

			for (int i = 0; i < listPages.size(); i++) {

				if (listPages.get(i).getName().endsWith(".htm")) {
					// if (i % 100 == 0)
					// System.out.println();
					// System.out.print(".");

					Map<String, String> m = getXpathForTextNodesFromPage(listPages
							.get(i));

					PrintWriter page = null;
					new File(outFolder).mkdirs();

					try {
						page = new PrintWriter(new FileWriter(outFolder
								+ listPages.get(i).getName()));
						SortedMap<String, String> sm = new TreeMap<String, String>();
						sm.putAll(m);

						for (Entry<String, String> e : sm.entrySet()) {
							page.write(e.getKey() + "\t" + e.getValue() + "\n");
						}
						page.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

			System.out.println();

			System.out.println("page size = " + listPages.size());

			// }

		}

	}
	
	
	/**
	 * reads pages from Lucene index
	 * @param index
	 * @param outFolder
	 */
	public static void generateStructure(CrawlIndex index, String outFolder) {


		
		 File outFold = new File(outFolder);
		 Set<String> processed = new HashSet<String>();
		 if(outFold.exists()){
			 File[] files = outFold.listFiles();
			 for (File f:files){
				 processed.add(f.getName());
			 }
		 }else{
			 outFold.mkdirs();
		 }
 
		Set<Page> pages = index.getAllPages();
			HashMap<String, Integer> ids = null;
			try {
				ids = index.getAllDocumentsIds();
				System.out.println(ids.size()+" "+index.maxId() + " "+index.size());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
		System.out.print("generating xpath for " + index.getName());
		System.out.println("already processed: "+processed.size() +processed);

			for (Page p: pages) {
				String pid = ids.get(p.getTitle()).toString();
				if(!processed.contains(pid))
				{
				InputStream streamDoc = CleanHtmlPAge.cleanHtmlDocument(p.getDocument());
				Document d = extractDomForHtmlPage(streamDoc);
				try {
					streamDoc.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
					Map<String, String> m = getXpathForTextNodesFromPage(d);

					PrintWriter page = null;

					try {
//						String pageName = p.getTitle().endsWith("/")? p.getTitle().substring(0, p.getTitle().length()-1) :p.getTitle();
//						String id = outFolder +  pageName.substring(pageName.lastIndexOf("/"));
						String id = outFolder + ids.get(p.getTitle());
						page = new PrintWriter(new FileWriter(id));
						SortedMap<String, String> sm = new TreeMap<String, String>();
						sm.putAll(m);

						for (Entry<String, String> e : sm.entrySet()) {
							page.write(e.getKey() + "\t" + e.getValue() + "\n");
						}
						page.close();
					} catch (IOException e) {
						e.printStackTrace();
						System.err.println(p.getTitle());
					}

			}else{
				System.err.println(" skipping "+ ids.get(p.getTitle()));
			}
			}

			System.out.println();

			System.out.println("page size = " + pages.size());

			// }

		

	}
	
	 
	
	  
		private static Document extractDomForHtmlPage(InputStream inputStream) {
			Document doc = null;
			try {

				TagSoupParser tsp = new TagSoupParser(inputStream, "utf-8");
				doc = tsp.getDOM();
				inputStream.close();

				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if (inputStream!=null){
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			return doc;

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

				NodeList nodes = findXpathNodeOnHtmlPage(doc, xpath);

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

	public static NodeList findXpathNodeOnHtmlPage(Document doc, String xp)

	throws XPathExpressionException {

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		XPathExpression expr = xpath.compile(xp);

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;

		return nodes;
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

	/**
	 * @param args
	 * 
	 * This main demonstrates the conversion of HTML pages to a set of <xpath-value> pairs for all text nodes in the page
	 */
	public static void main(String[] args) {

		
		//read dataset from file system
//		String dom = "auto";
//		String dom = "book";
//		String dom = "camera";
//		String dom = "job";
//		String dom = "movie";
//		String dom = "nbaplayer";
//		String dom = "restaurant";
		String dom = "university";

		String domain = "/Users/annalisa/Documents/CORPORAandDATASETS/swde-17477/testSET/"+dom+"/";
		String resFolder = "./resources/datasetsWithInternalXPathRepresentation/swde-17477/"+dom+"/";

		File d = new File(domain);
		for (File f : d.listFiles()) {
			if (f.isDirectory()) {
				ReducePagesToXpath.generateStructure(f.getAbsolutePath(),
						resFolder + f.getName() + File.separator);

			}
		}

		
		//read dataset from lucene index
		/*
//		String index = "./resources/datasets/REX/testset/espnfc-player-index";
//		String index = "./resources/datasets/REX/testset/espnfc-team-index-new";
//		String index = "./resources/datasets/REX/testset/goodreads-author-index";
//		String index = "./resources/datasets/REX/testset/goodreads-book-index";
//		String index = "./resources/datasets/REX/testset/imdb-name-index";
		String index = "./resources/datasets/REX/testset/imdb-title-index";

		
		CrawlIndex ci = new CrawlIndex(index);
		String indexName = ci.getName().substring(ci.getName().lastIndexOf(File.separator));
		String resFolderFromIndex = "./resources/datasetsWithInternalXPathRepresentation/REX/"+ indexName+File.separator;

		

				ReducePagesToXpath.generateStructure(ci,
						resFolderFromIndex + File.separator);

*/
	}

	// public static void genereteXpath(String domain, String gazFolder, String
	// resFolder, String attribute, int repetitions) {
	public static void genereteXpath(String domain, String outFolder) {

		// Set<String> gaz = new HashSet<String> ();
		// gaz = new HashSet<String>();
		// try {
		// gaz = loadFile(gazFolder);
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }

		// resFolder = resFolder+File.separator+attribute;
		File f = new File(domain);
		for (File folder : f.listFiles()) {
			if (folder.isDirectory()) {
				ReducePagesToXpath.generateStructure(folder.getAbsolutePath(),
						outFolder);

			}
		}
	}

}
