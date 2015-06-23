package uk.ac.shef.dcs.oak.operations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.any23.util.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author annalisa
 * Class which provides basic text normalization operators.
 * 
 */

public class TextOperations {

	private String matchesPattern(Pattern p, String sentence) {
		Matcher m = p.matcher(sentence);

		if (m.find()) {
			return m.group();
		}
		return null;
	}


	public static String normalizeString(String r) {
		r = StringEscapeUtils.unescapeHtml(r);
		// r = r.trim().toLowerCase().replaceAll("( )+", " ");
		r = r.replaceAll("\\u00A0", " ");
		r = r.trim().toLowerCase().replaceAll("\\s+", " ");
		return r;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("unescape Weight: 245&nbsp; ["
				+ StringEscapeUtils.unescapeHtml("Weight: 245&nbsp;") + "]");
		System.out.println("normalize Weight: 245&nbsp; ["
				+ normalizeString("Weight: 245&nbsp;") + "]");
		System.out.println("posted:&nbsp; november 28, 2010 "
				+ normalizeString("posted:&nbsp; november 28, 2010"));

		try {

			File folder = new File(
					"/Users/annalisa/Desktop/sindiceEvalResults/a/");
			File[] files = folder.listFiles();

			File outfolder = new File(
					"/Users/annalisa/Desktop/sindiceEvalResultsUnescaped/a/");
			outfolder.mkdirs();

			for (File f : files) {
				PrintWriter out = new PrintWriter(new FileWriter(outfolder
						+ File.separator + f.getName()));
				System.out
						.println(f.getName() + "****************************");

				String[] lines = FileUtils.readFileLines(f);
				int lc = 1;
				for (String l : lines) {
					String[] cell = l.split(",");
					for (String c : cell) {

						out.print(URLDecoder.decode(c) + ",");
					}
					System.out.print(lc + "\t");
					System.out.print(URLDecoder.decode(cell[1]) + "\t");
					System.out.print(URLDecoder.decode(cell[2]) + "\t");

					System.out.println();
					out.println();

					lc++;
				}
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
