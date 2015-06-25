package uk.ac.shef.dcs.oak.xpath.processors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * @author annalisa This class reads pages represented as a set of <xpath-value>
 *         pairs (as produced by ReducePagesToXpath.generateStructure method)
 *         and select the subset of <xpath-value> pairs where the value matches
 *         a given gazetteer
 */
public class CandidateXpathGenerator {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String infile = "./pagexpath/testExperiment/book/book-booksamillion-2000";
        String gaz = "./resources/gazetteers/gazWithCardinality/book/title.txt";

        generateCandidatedAnnotations(infile, gaz, "./reducedPages");
    }

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

    public static void generateCandidatedAnnotations(String infolder, String gazFile, String outFolder) {
        // TODO load gaz
        Set<String> gaz = new HashSet<String>();
        try {
            gaz = loadFile(gazFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        generateCandidatedAnnotations(infolder, gaz, outFolder);
    }

    public static void generateCandidatedAnnotations(String infolder, Set<String> gaz, String outFolder) {
        PrintWriter out1;
        File folder = new File(infolder);
        new File(outFolder).mkdirs();
        for (File f : folder.listFiles()) {
            BufferedReader br;
            try {
                out1 = new PrintWriter(new FileWriter(outFolder + File.separator + f.getName()));
                br = new BufferedReader(new FileReader(f));
                String nextLine;
                while ((nextLine = br.readLine()) != null) {
                    String t[] = nextLine.split("\t");
                    if ((t.length > 1) && (gaz.contains(t[1]))) {
                        out1.println(t[0] + "\t" + t[1]);
                    }
                }
                br.close();
                out1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}