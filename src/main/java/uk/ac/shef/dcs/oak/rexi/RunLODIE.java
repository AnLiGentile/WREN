package uk.ac.shef.dcs.oak.rexi;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.xpath.XPathExpressionException;

import uk.ac.shef.dcs.oak.operations.Gazetteer;
import uk.ac.shef.dcs.oak.xpath.cotrollers.GenerateAllXpath;
import uk.ac.shef.dcs.oak.xpath.cotrollers.GenerateXpathMultipleValues;
import uk.ac.shef.dcs.oak.xpath.cotrollers.XPathGenerator;
import uk.ac.shef.dcs.oak.xpath.cotrollers.decorator.LODIEXPathGeneratorDecorator;
import uk.ac.shef.dcs.oak.xpath.cotrollers.decorator.LggXPathGeneratorDecorator;
import uk.ac.shef.dcs.oak.xpath.processors.ExtractValues;
import uk.ac.shef.dcs.oak.xpath.processors.XpathOverlapCalculator;

public class RunLODIE extends REXIController {
	
	String currentDomain;
	

    public RunLODIE(ExecutorService executor, String string) {
		super(executor);
		this.currentDomain = string;
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
        ExecutorService executor_book = Executors.newFixedThreadPool(5);
        try {
        	RunLODIE book = new RunLODIE(executor_book, "book");

            Set<Property> properties = new HashSet<Property>();
            properties.add(new Property("http://example.org/title", "title"));
            properties.add(new Property("http://example.org/author", "author"));
            properties.add(new Property("http://example.org/isbn_13", "isbn"));
            properties.add(new Property("http://example.org/publication_date", "publication_date"));
            properties.add(new Property("http://example.org/publisher", "publisher"));

            book.run(new File("./resources/datasetsWithInternalXPathRepresentation/swde-17477/book"), "book", properties);
            
            
        } finally {
            executor_book.shutdown();
        }
		
		
        ExecutorService executor_restaurant = Executors.newFixedThreadPool(5);
        try {
        	RunLODIE restaurant = new RunLODIE(executor_restaurant, "restaurant");

            Set<Property> properties = new HashSet<Property>();
            properties.add(new Property("http://example.org/address", "address"));
            properties.add(new Property("http://example.org/cuisine", "cuisine"));
            properties.add(new Property("http://example.org/name", "name"));
//            properties.add(new Property("http://example.org/phone", "phone"));

            restaurant.run(new File("./resources/datasetsWithInternalXPathRepresentation/swde-17477/restaurant"), "restaurant", properties);
            
            
        } finally {
            executor_restaurant.shutdown();
        }
    

	
    ExecutorService executor_movie = Executors.newFixedThreadPool(5);
    try {
    	RunLODIE movie = new RunLODIE(executor_movie, "movie");

        Set<Property> properties = new HashSet<Property>();
        properties.add(new Property("http://example.org/title", "title"));
        properties.add(new Property("http://example.org/genre", "genre"));
        properties.add(new Property("http://example.org/mpaa_rating", "mpaa"));
        properties.add(new Property("http://example.org/director", "director"));

        movie.run(new File("./resources/datasetsWithInternalXPathRepresentation/swde-17477/movie"), "movie", properties);
        
        
    } finally {
        executor_movie.shutdown();
    }
		
	    
	    
	    ExecutorService executor_nbaplayer = Executors.newFixedThreadPool(5);
	    try {
	    	RunLODIE nbaplayer = new RunLODIE(executor_nbaplayer, "nbaplayer");

	        Set<Property> properties = new HashSet<Property>();
	        properties.add(new Property("http://example.org/name", "name"));
	        properties.add(new Property("http://example.org/team", "team"));

	        nbaplayer.run(new File("./resources/datasetsWithInternalXPathRepresentation/swde-17477/nbaplayer"), "nbaplayer", properties);
	        
	        
	    } finally {
	        executor_nbaplayer.shutdown();
	    }
	    
      ExecutorService executor_university = Executors.newFixedThreadPool(5);
      try {
      	RunLODIE university = new RunLODIE(executor_university, "university");

          Set<Property> properties = new HashSet<Property>();
          properties.add(new Property("http://example.org/phone", "phone"));
          properties.add(new Property("http://example.org/website", "website"));
          properties.add(new Property("http://example.org/name", "name"));
//          properties.add(new Property("http://example.org/phone", "phone"));

          university.run(new File("./resources/datasetsWithInternalXPathRepresentation/swde-17477/university"), "university", properties);
          
          
      } finally {
          executor_university.shutdown();
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

					 for (Entry<String, Map<String, Set<String>>> e : attribute.getValue().entrySet()){
							System.out.println(attribute.getKey());

					System.out.println(e);
					ExtractValues.printAnnotationsWREN(RESULTS_FOLDER, e.getValue(),
							this.currentDomain, domain_iFolder.getName(), attribute.getKey());
					 }
					 //TODO write a class to show-case this better, with multiple candidates
						XpathOverlapCalculator c = new XpathOverlapCalculator(attribute.getValue());
						for (int i =0; i <c.getOverlapMatrix().length; i++){
							for (int j =0; j <c.getOverlapMatrix()[i].length; j++){
							System.out.print(c.getOverlapMatrix()[i][j]+"\t");}
							System.out.println();
						}

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
        
    	//coollect xpath
        Map<Property, SortedMap<String, Double>> xpaths = determineXPaths(domain_iFolder, concept,
                domain_iFolder.getName(), gazetteerMapping);

        applyXPaths(xpaths, domain_iFolder);

    }
    
   
    @Override
    protected Map<Property, SortedMap<String, Double>> determineXPaths(File inputFolder, String concept, String domain_i,
            Map<Property, Gazetteer> gazetteerMapping) {

    	Map<Property, Future<SortedMap<String, Double>>> threadMapping = new HashMap<Property, Future<SortedMap<String, Double>>>();
    	XPathGenerator generator;
        for (Property property : gazetteerMapping.keySet()) {
            generator = new GenerateAllXpath(concept, domain_i, inputFolder.getAbsolutePath(),
                    INTERMEDIATE_RESULTS_FOLDER + concept + File.separator + domain_i + File.separator,
                    gazetteerMapping.get(property), property.getLabel());
            generator = new LODIEXPathGeneratorDecorator(generator);
            threadMapping.put(property, executor.submit(new XPathGeneration(generator)));
        }
        Map<Property, SortedMap<String, Double>> xPaths = new HashMap<Property, SortedMap<String, Double>>();
        for (Property property : threadMapping.keySet()) {
            try {
                xPaths.put(property, threadMapping.get(property).get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("Got an exception while trying to execute the x path generation.", e);
            }
        }

        return xPaths;
    }

}
