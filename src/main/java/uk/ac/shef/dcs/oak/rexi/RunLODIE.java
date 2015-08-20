package uk.ac.shef.dcs.oak.rexi;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.xpath.XPathExpressionException;

import model.ExtractedValue;
import uk.ac.shef.dcs.oak.operations.Gazetteer;
import uk.ac.shef.dcs.oak.xpath.processors.ExtractValues;
import uk.ac.shef.dcs.oak.xpath.processors.XpathOverlapCalculator;

public class RunLODIE extends REXIController {

    public RunLODIE(ExecutorService executor) {
		super(executor);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        try {
        	RunLODIE rexi = new RunLODIE(executor);

            Set<Property> properties = new HashSet<Property>();
            properties.add(new Property("http://example.org/title", "title"));
            rexi.run(new File("./pagexpath/testExperiment/book"), "book", properties);
            
            
        } finally {
            executor.shutdown();
        }
    }

	
    protected void applyXPaths(Map<Property, SortedMap<String, Double>> xpaths, File domain_iFolder) {
        // TODO Auto-generated method stub

		// in xpath the key is the property, values are all possible xpath
		Map<String, Set<String>> xpath = new HashMap<String, Set<String>>();
		
        // Let's just print them...
        SortedMap<String, Double> paths;
        for (Property property : xpaths.keySet()) {
            System.out.println("***** Property: " + property.getLabel());
            paths = xpaths.get(property);
            xpath.put(property.getLabel(), new HashSet<String>());
            for (Map.Entry<String, Double> e : paths.entrySet()) {
//                System.out.println(e.getKey() + " " + e.getValue());
                xpath.get(property.getLabel()).add(e.getKey());
            }
        }
        
        try {
			Map<String, Map<String, Map<String, Set<String>>>> res = ExtractValues.buildXpathValueMapromCachedPages(domain_iFolder.getAbsolutePath(), xpath);
			
			// print results
			if (res != null) {
				for (Entry<String, Map<String, Map<String, Set<String>>>> attribute : res.entrySet()) {
//					printAnnotations("./test/test/test", res.get(attribute),
//							domain, website, attribute);
					 for (Entry<String, Map<String, Set<String>>> e : attribute.getValue().entrySet()){
					System.out.println(e);
					
					 }
						XpathOverlapCalculator c = new XpathOverlapCalculator(attribute.getValue());

				}
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    /**
     * 
     * @param inputFolder
     *            The folder containing the HTML files.
     * @param concept
     *            INPUT 1. identify concept A in K (this would be a mock-up, not
     *            the focus of the paper)
     * @param properties
     *            INPUT 2. identify a set of properties related to A, P = {p_1
     *            ...p_n} that we want to extract (n can be all of them)
     */
    @Override
    public void run(File inputFolder, String concept, Set<Property> properties) {
        /*
         * 3. collect one gazetteer of each p_i in P by looking at all
         * occurrences of concept A in KB both in subj or obj
         */
        Map<Property, Gazetteer> gazetteerMapping = loadGazetteers(properties, concept);

        // for every domain d_i inside D
        for (File domainFolder : inputFolder.listFiles()) {
            if (domainFolder.isDirectory()) {
                runLODIE(domainFolder, concept, properties, gazetteerMapping);
            }
        }
    }

    private void runLODIE(File domain_iFolder, String concept, Set<Property> properties,
            Map<Property, Gazetteer> gazetteerMapping) {
        
        Map<Property, SortedMap<String, Double>> xpaths = determineXPaths(domain_iFolder, concept,
                domain_iFolder.getName(), gazetteerMapping);

        applyXPaths(xpaths, domain_iFolder);

    }

}
