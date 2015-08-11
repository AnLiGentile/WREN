package uk.ac.shef.dcs.oak.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

/**
 * @author annalisa
 */
public class CleanHtmlPAge {
	
	// convert InputStream to String and remove the DOCTYPE declaration
	public static String getCleanHtmlString(InputStream is) {
 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()&!line.toUpperCase().startsWith("<!DOCTYPE"))
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
 
		return sb.toString();
 
	}
 
	// convert InputStream to String and remove the DOCTYPE declaration
	private static String getCleanHtmlDocument(Document doc) {
 
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(doc);
		Result outputTarget = new StreamResult(outputStream);
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
			InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
			 
			String line;
			try {
	 
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					if (!line.isEmpty()&!line.toUpperCase().startsWith("<!DOCTYPE"))
					sb.append(line);
				}
	 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (TransformerConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (TransformerFactoryConfigurationError e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return sb.toString();
 	}
	
	private static String getHtmlString(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
 
		return sb.toString();
 
	}
	

	private static InputStream getInputStream(String s) {
		InputStream cleanStream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
		return cleanStream;

	}
	

	public static InputStream cleanHtmlInputStream (InputStream inputStream){
		String s = getCleanHtmlString(inputStream);
		InputStream is = getInputStream(s);
		return is;

	}

	
	public static InputStream cleanHtmlDocument (Document doc){
		String s = getCleanHtmlDocument(doc);
		InputStream is = getInputStream(s);
		return is;

	}
	
	public static void main (String[] args){
    	
    	
    	
		InputStream inputStreamRead = null;
		InputStream inputStream = null;

			try {
				inputStreamRead = new FileInputStream("./resources/datasets/WEIR/test/uk.eurosport.yahoo.com/aaron-hunt.html");		        
		        
				inputStream = CleanHtmlPAge.cleanHtmlInputStream(inputStreamRead);

			    System.out.println(CleanHtmlPAge.getCleanHtmlString(inputStream));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		

    }
}