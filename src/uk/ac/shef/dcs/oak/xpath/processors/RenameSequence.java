package uk.ac.shef.dcs.oak.xpath.processors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

public class RenameSequence {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String infile = "/Users/annalisa/Documents/writings/LODIE_local/papers/ISWC2014/CLUSTER/tree.txt";

		String outfile1 = "./cl1.txt";
		String outfile2 = "./cl2.txt";
		String outfile3 = "./cl3.txt";

		renameSequences(infile, outfile1, outfile2, outfile3);
	}

	private static void renameSequences(String infile, String outfile1,
			String outfile2, String outfile3) {

		PrintWriter out1;
		PrintWriter out2;
		PrintWriter out3;

		BufferedReader br;
		try {
			out1 = new PrintWriter(new FileWriter(outfile1));
			out2 = new PrintWriter(new FileWriter(outfile2));
			out3 = new PrintWriter(new FileWriter(outfile3));

			br = new BufferedReader(new FileReader(infile));

			int lines = 0;

			String nextLine;
			new File("./clusters/c1").mkdirs();
			new File("./clusters/c2").mkdirs();
			new File("./clusters/c3").mkdirs();

			while ((nextLine = br.readLine()) != null) {
				lines++;
				String t[] = nextLine.split("\t");
				File source = new File(
						"/Users/annalisa/Documents/CORPORAandDATASETS/ISWCdataset/TESTSET/film_all/film-imdb-5747"
								+ File.separator + t[0]);
				if (t[1].startsWith("doc-HTML[1]-HEAD[1]-SCRIPT[11]")) {
					out1.println(t[0] + "\t" + t[1]);
					File desc = new File("./clusters/c1" + File.separator
							+ t[0]);
					try {
						FileUtils.copyFile(source, desc);
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else if (t[1].startsWith("doc-HTML[1]-HEAD[1]-SCRIPT[12]")) {
					out2.println(t[0] + "\t" + t[1]);
					File desc = new File("./clusters/c2" + File.separator
							+ t[0]);
					try {
						FileUtils.copyFile(source, desc);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (t[1].startsWith("doc-HTML[1]-HEAD[1]-SCRIPT[6]")) {
					out3.println(t[0] + "\t" + t[1]);
					File desc = new File("./clusters/c3" + File.separator
							+ t[0]);
					try {
						FileUtils.copyFile(source, desc);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println(t[0] + " " + t[1]);
				}

			}
			out1.close();
			out2.close();
			out3.close();

			System.out.println(lines);
		} catch (Exception e) {

		}
	}
}
