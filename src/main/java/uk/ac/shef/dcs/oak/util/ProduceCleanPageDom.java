package uk.ac.shef.dcs.oak.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.any23.extractor.html.TagSoupParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author annalisa
 * this class was used to fix some ill html pages
 */
@Deprecated
public class ProduceCleanPageDom {

	private static Document extractDomForHtmlPage(File localHtml2) {
		Document doc = null;
		InputStream inputStream = null;
		// org.jsoup.nodes.Document d=null;

		try {
			inputStream = new FileInputStream(localHtml2.getAbsolutePath());

			// d = Jsoup.parse(localHtml2, "utf-8");
			TagSoupParser tsp = new TagSoupParser(inputStream, "utf-8");
			doc = tsp.getDOM();
			inputStream.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
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

	/**
	 * @param args
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static void main(String[] args)
			throws TransformerFactoryConfigurationError, TransformerException {

		// File res = new File ("./parsedPages/book/");
		File res = new File("./parsedPages/music/");
		if (!res.exists())
			res.mkdirs();

		File folder = new File(
				"/Users/annalisa/Documents/LODIEws/LODIE_data/ISWCdataset/RAW/priority_2/music/music-reverbnation-1216");

		int c = 0;
		int i = 0;
		int size = folder.listFiles().length;
		for (File f : folder.listFiles()) {

			i++;
			Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			Node doc = extractDomForHtmlPage(f);
			// System.out.println("root node has childern: "
			// +doc.getChildNodes().getLength());
			// for (int i=0; i< doc.getChildNodes().getLength(); i++){
			// Node next = doc.getChildNodes().item(i);
			// System.out.println("*****"+next
			// +" --> "+next.getChildNodes().getLength());
			// for (int j=0; j< next.getChildNodes().getLength(); j++){
			// System.out.println(next.getChildNodes().item(j));
			// }
			// }

			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
			String xmlString = result.getWriter().toString();

			PrintWriter out;

			try {
				out = new PrintWriter(new FileWriter(res + File.separator
						+ f.getName()));
				out.println(xmlString);

				c++;
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out
					.println("parsed " + i + " saved " + c + " total " + size);
		}
	}
}
