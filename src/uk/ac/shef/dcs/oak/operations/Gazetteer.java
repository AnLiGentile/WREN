package uk.ac.shef.dcs.oak.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.shef.dcs.oak.xpath.scorer.LoadGS;

/**
 * @author annalisa
 * Class to load a Gazeteer from a text file.
 */
public class Gazetteer implements Serializable {
	private static Logger l4j = Logger.getLogger("Gazeteer");

	private HashSet<String> words;

	public HashSet<String> getWords() {
		return words;
	}

	
	public Gazetteer(String wordFilePath) {
		super();
		this.words = new HashSet<String>();
		try {
			this.loadFile(wordFilePath);
		} catch (IOException e) {
			l4j.error("error loading stopWord file");

			e.printStackTrace();
		}
	}

	private void loadFile(String wordFilePath) throws IOException {
		BufferedReader input = new BufferedReader(new FileReader(wordFilePath));
		String line;
		line = input.readLine();

		while (line != null) {
			line = line.trim();
			if (!line.equals("")) {
				this.words.add(line.toLowerCase());
			}

			line = input.readLine();

		}

	}

	/**
	 * @param args
	 * Example code that prints statistics about gazetteers and gold standard answers
	 */
	public static void main(String[] args) {
		
//		String gazetteer = args[0];
//		String goldStandardFile = args[1]; 
//		String testSetPath = args[2]; 
//		String tempPathForCopyingSubsetOfTest = args[3]; 
		
		String gazetteer = "./resources/gazetteers/book/title.txt";
		String goldStandardFile = "./resources/datasets/swde-17477/groundtruth/book/book-amazon-title.txt";

		
		Gazetteer fileExt = new Gazetteer(gazetteer);
		Set<String> all = new HashSet<String>();
		Set<String> correct = new HashSet<String>();

		for (String s : fileExt.getWords()) {
			all.add(s.toLowerCase());
		}

		LoadGS fileGs = new LoadGS(goldStandardFile);

		for (Set<String> s : fileGs.getValues().values()) {
			for (String t : s) {
				correct.add(t.toLowerCase());
			}
		}

		System.out.println("LOD gathered annotations size " + all.size());
		System.out.println("GS annotations size " + correct.size());

		Set<String> inters = SetOperations.intersection(correct, all);

		System.out.println("intersection size " + inters.size());
		System.out.println(inters);

		
		// the following code is only to demostrate copying facility
		
/*		String testSetPath = "./resources/datasets/swde-17477/testSET/book/book-amazon-2000/";
		String tempPathForCopyingSubsetOfTest = "./resources/temp/datasets/swde-17477/testSET/book/book-amazon(correct)/";

		new File(tempPathForCopyingSubsetOfTest).mkdirs();		
		System.out.println("**** pages to use *****");


		for (Entry<String, Set<String>> s : fileGs.getValues().entrySet()) {
			String firstRes = "";
			if (!s.getValue().isEmpty())
				firstRes = s.getValue().iterator().next();
			if (all.contains(firstRes)) {
				String in = testSetPath + s.getKey() + ".htm";
				String out = tempPathForCopyingSubsetOfTest + s.getKey() + ".htm";

				System.out.println("copying " + in + " to " + out);

				Gazetteer.copyFile(in, out);

			}
		}
		System.out.println("**** end pages to use *****");*/
		
	}

	public static void copyFile(String in, String out) {

		InputStream inStream = null;
		OutputStream outStream = null;

		try {

			File afile = new File(in);
			File bfile = new File(out);

			inStream = new FileInputStream(afile);
			outStream = new FileOutputStream(bfile);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {

				outStream.write(buffer, 0, length);

			}

			inStream.close();
			outStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
