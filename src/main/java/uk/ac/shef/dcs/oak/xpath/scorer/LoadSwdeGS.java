package uk.ac.shef.dcs.oak.xpath.scorer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.shef.dcs.oak.operations.TextOperations;

public class LoadSwdeGS implements LoadGS {

	private static Logger l4j = Logger.getLogger(LoadSwdeGS.class);

	// TODO load counts as well
	private HashMap<String, Set<String>> values;

	public HashMap<String, Set<String>> getValues() {
		return this.values;
	}

	// public StopWord() {
	// super();
	// this.words = new HashSet<String> () ;
	// }
	public LoadSwdeGS(String gsPath) {
		super();
		this.values = new HashMap<String, Set<String>>();
		try {
			this.loadFile(gsPath);
		} catch (IOException e) {
			l4j.error("error loading gs file");
			e.printStackTrace();
		}
	}

	private void loadFile(String gsPath) throws IOException {
		// BufferedReader input=new BufferedReader(new FileReader(gsPath));
		// BufferedReader input = new BufferedReader(new InputStreamReader(is,
		// "UTF-8"));
		BufferedReader input = new BufferedReader(new InputStreamReader(
				new FileInputStream(gsPath), "UTF-8"));

		String line;
		l4j.info("loading file " + gsPath);
		// skip the header
		// TODO do smt with the header later
		line = input.readLine();
		line = input.readLine();
		line = input.readLine();

		while (line != null) {
			line = line.trim();
			if (!line.equals("")) {
				String[] v = line.split("\t");
				int results = 0;
				try {
					results = Integer.parseInt(v[1]);
				} catch (Exception e) {
					l4j.error("parsing issue with ***  " + v.length);
				}

				Set<String> values = new HashSet<String>();

				for (int i = 0; i < results; i++) {
					try {

						values.add(TextOperations.normalizeString(v[i + 2]));

						// values.add(v[i+2].toLowerCase());
					} catch (Exception e) {
						l4j.error("missing value in " + line);
					}
				}
				String id = v[0];
				// TODO this is a quick fix to ignore extensions in file names
				if (id.endsWith(".htm"))
					id = id.substring(0, id.lastIndexOf(".htm"));

				this.values.put(id.toLowerCase(), values);
			}
			// this.values.put(id, values);}

			line = input.readLine();

		}
		// HashMap<String, Set<String>> r = this.values;

	}

	
	/* (non-Javadoc)
	 * @see uk.ac.shef.dcs.oak.xpath.scorer.LoadGS#getAllValuesInGS(java.lang.String)
	 * property can be null, is not used in this loader
	 */
	public Set<String> getAllValuesInGS() {
		Set<String> ann = new HashSet<String>();
		for (Entry<String, Set<String>> s : this.values.entrySet()) {
			for (String a : s.getValue())
				ann.add(a);
		}
		return ann;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LoadSwdeGS fileExt = new LoadSwdeGS(
				"./resources/datasets/swde-17477/groundtruth/nbaplayer/nbaplayer-espn-weight.txt");

		System.out.println(fileExt.getAllValuesInGS(""));
		System.out.println(fileExt.getValues(""));

	}

	@Override
	public HashMap<String, Set<String>> getValues(String property) {
		return getValues();
	}

	@Override
	public Set<String> getAllValuesInGS(String property) {
		return getAllValuesInGS();
	}

}
