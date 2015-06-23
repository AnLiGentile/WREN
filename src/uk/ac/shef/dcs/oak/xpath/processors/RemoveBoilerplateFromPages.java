package uk.ac.shef.dcs.oak.xpath.processors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

/**
 * @author annalisa
 *
 */
public class RemoveBoilerplateFromPages {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String infile = "./reducedPages";

		removeBoilerplate(infile, "./reducedPagesWithoutBoilerplate/");
	}

	public static void removeBoilerplate(String infolder, String outFolder) {

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
}