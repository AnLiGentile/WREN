package uk.ac.shef.dcs.oak.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.apache.any23.extractor.html.TagSoupParser;
import org.w3c.dom.Document;

/**
 * @author annalisa
 * 
 */
public class HtmlDocument {

	public HtmlDocument(String pageUrl) {
		super();
		this.pageUrl = pageUrl;
		//TODO CHECK valid url
	}
	

	public HtmlDocument(File f) {
		super();
		this.localHtml=f;
	}

	private File localHtml; //local copy of the page
	private String pageUrl; // url of the page
	private String pageHtml; // html content of page
	private Document pageDom; // dom Document from the html page

	public Document getPageDom() {
		
		if (pageDom==null){
			if (this.localHtml!=null){
				
				this.pageDom = extractDomForHtmlPage(this.localHtml);

			}else{
			this.pageDom = extractDomForHtmlPage(this.pageUrl);}
		}
		return pageDom;
	}

	private Document extractDomForHtmlPage(File localHtml2) {
		Document doc = null;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(localHtml2.getAbsolutePath());

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


	public void setPageDom(Document pageDom) {
		this.pageDom = pageDom;
	}

	private Set<String> xpath; // weight for each xpath in the page

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getPageHtml() {
		return pageHtml;
	}

	public void setPageHtml(String pageHtml) {
		this.pageHtml = pageHtml;
	}

	public Set<String> getXpath() {
		return xpath;
	}

	public void setXpath(Set<String> xpath) {
		this.xpath = xpath;
	}
	
	//TODO create similar methods for extractin html content, clean text from html, triples from html, xpath ...

	private Document extractDomForHtmlPage(String pageUrl) {
		Document doc = null;
		InputStream input;
		try {
			input = new URL(pageUrl).openStream();
			TagSoupParser tsp = new TagSoupParser(input, "utf-8");
			doc = tsp.getDOM();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;

	}


	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
