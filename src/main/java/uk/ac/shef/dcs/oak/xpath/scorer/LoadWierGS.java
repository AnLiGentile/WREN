package uk.ac.shef.dcs.oak.xpath.scorer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

public class LoadWierGS implements LoadGS {

	@Override
	public HashMap<String, Set<String>> getValues(String property) {
		return this.values.get(property);
	}

	private static Logger l4j = Logger.getLogger(LoadSwdeGS.class);

	// TODO load counts as well
	private HashMap<String,HashMap<String, Set<String>>> values;

	private HashMap<String,HashMap<String, Set<String>>> getValuesAllPRoperties() {
		return this.values;
	}


	public LoadWierGS(String gsPath) {
		super();
		this.values = new HashMap<String,HashMap<String, Set<String>>>();
		try {
			this.loadFile(gsPath);
		} catch (IOException e) {
			l4j.error("error loading gs file");
			e.printStackTrace();
		}
	}

	private void loadFile(String gsPath) throws IOException {
		
		CSVParser sent;

			File f = new File(gsPath);
			sent = CSVParser
					.parse(f, Charset.forName("UTF-8"), CSVFormat.RFC4180);
			
//		String line;
		l4j.info("loading file " + gsPath);
		// read the header line
		
		
		CSVRecord line = null;
		Iterator<CSVRecord> it = sent.iterator();
		CSVRecord header = it.next();
		
		while (it.hasNext()) {
			CSVRecord current = it.next();


			
				for (int i = 1; i < current.size(); i++) {
					try {
						if (this.values.get(header.get(i))==null)
							this.values.put(header.get(i), new HashMap<String, Set<String>>());
						
						Set<String> val = new HashSet<String>();
						val.add(current.get(i));
						this.values.get(header.get(i)).put(current.get(0), val);
											} catch (Exception e) {
						l4j.error("problems on line " + line);
					}
				}

			
			
		}



	}

	public Set<String> getAllValuesInGS(String property) {
		Set<String> ann = new HashSet<String>();
		for (Entry<String, Set<String>> s : this.values.get(property).entrySet()) {
			for (String a : s.getValue())
				ann.add(a);
		}
		return ann;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadWierGS fileExt = new LoadWierGS(
				"./resources/datasets/WEIR/weir-dataset-csv-golden/book/book-amazon.csv");
		System.out.println(fileExt.getAllValuesInGS("STRING : TITLE"));
		System.out.println(fileExt.getAllValuesInGS("STRING : PUBLISHER"));
		System.out.println(fileExt.getValues("STRING : TITLE").size());
		System.out.println(fileExt.getValues("STRING : TITLE").keySet());

		
	}


}
