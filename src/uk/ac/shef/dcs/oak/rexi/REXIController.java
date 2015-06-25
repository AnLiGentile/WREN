package uk.ac.shef.dcs.oak.rexi;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.shef.dcs.oak.operations.Gazetteer;
import uk.ac.shef.dcs.oak.xpath.cotrollers.XPathGenerator;
import uk.ac.shef.dcs.oak.xpath.cotrollers.XPathGeneratorFactory;

/**
 * FIXME PLEASE RENAME ME AND MY PACKAGE!!!
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class REXIController {

    private static final String GAZETTEERS_FOLDER = "resources/gazetteers/gazWithCardinality/";

    private static final String TEMP_FOLDER = "temp/";
    private static final String INTERMEDIATE_RESULTS_FOLDER = "intermediate/";
    private static final String PREPROCESSED_HTML_FILES_FOLDER_NAME = "html";
    private static final String XPATH_FILES_FOLDER_NAME = "xpath";

    private static final Logger LOGGER = LoggerFactory.getLogger(REXIController.class);

    public static void main(String[] args) {

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
    public void run(File inputFolder, String concept, Set<Property> properties) {
        /*
         * TODO 3. collect one gazetteer of each p_i in P by looking at all
         * occurrences of concept A in KB both in subj or obj
         */
        Map<Property, Gazetteer> gazetteerMapping = loadGazetteers(properties, concept);

        // for every domain d_i inside D
        for (File domainFolder : inputFolder.listFiles()) {
            if (domainFolder.isDirectory()) {
                run(domainFolder, concept, properties, gazetteerMapping);
            }
        }
    }

    private void run(File domainFolder, String concept, Set<Property> properties,
            Map<Property, Gazetteer> gazetteerMapping) {
        /*
         * XXX LATER 4. learning automaton for each p_i at this step we know if:
         * the attribute we want to extract is a “label-like” attribute (mostly
         * 1to1 relation attributes, such as titles, names…) the attribute has a
         * syntactically recognizable form (dates, telephone numbers, isbn,
         * price…) the attribute is of enumeration type (genre, cuisine type…)
         */

        /*
         * 5. @ALG: Each d_i in D is a set of homogeneous webpages d_i = {w_1 …
         * w_n}. For each d_1 we apply LODIE_WI, which returns: a set of
         * extractors for each p_i; the set of extractors is a sorted set of
         * xpath, each of them having a confidence score. LODIE_WI can also
         * return an attribute representation (so basically perform the
         * extraction already) for each w_i in d_i, i.e. a set of triples having
         * as subject the entity contained in w_1 as predicates all p_i in P and
         * has objects all relevant values for the p_i that we can find in page
         * w_i, using naive strategies for choosing the correct xpath from the
         * candidates (i.e. always choosing top-ranking; using a “template
         * methodology” to select multiple xpaths) LODIE_WI can also return a
         * provisional URI for each entity represented by each w_i; for clarity
         * I would put this in the extraction step
         */
        Map<Property, List<Pair<String, Double>>> xpaths = determineXPaths(domainFolder, concept, gazetteerMapping);

        /*
         * 6. xpath re-writing The current implementation uses explicit xpaths
         * expression, without any predicate or special operators; one novelty
         * direction includes work on rewriting the xpath; also a novel
         * contribution could be: depending on the output of step (4) we learn
         * different xpath extractors
         */
        /*
         * 7. @LB @AN: LGG for each d_i the LGG step takes as input the set of
         * scored xpath from (6) and returns the set of correct extractors (set
         * can have cardinality 0, 1 or multiple) we can potentially add
         * heuristics
         */
        callLGG(xpaths);
        /*
         * 8. @RU @ALG extraction We apply the extractors returned by (7) to all
         * pages in d_i and create a an attribute representation for each w_i in
         * d_i, i.e. a set of triples having as subject the entity contained in
         * w_1 as predicates all p_i in P and has objects all relevant values
         * for the p_i that we can find in page w_i using the provided
         * extractors
         */
        applyXPaths(xpaths);
        /*
         * 9. @RU: consistency check (use 4 maybe)
         */
        /*
         * 10. @RU @ALG: triple generation/publishing we generate a URI for each
         * entity (using our own defined namespace) and we perform a linking
         * procedure: we look at the existence of the entity in the original
         * dataset(s) (the reference KB is the same dataset(s) that we used to
         * generate the initial gazetteers); in that case we add a sameAs
         * statement once we performed the extraction on all d_i in D, we look
         * at the existence of same entity in any other w_i in D (i.e. is the
         * same entity represented in more than one website?)
         */
        /*
         * 11. fact checking (optional)
         */
    }

    private Map<Property, Gazetteer> loadGazetteers(Set<Property> properties, String concept) {
        Map<Property, Gazetteer> gazetteerMapping = new HashMap<Property, Gazetteer>();
        for (Property property : properties) {
            Gazetteer gazetteer = new Gazetteer(GAZETTEERS_FOLDER + concept + File.separator + property.getLabel());
            gazetteerMapping.put(property, gazetteer);
        }
        return gazetteerMapping;
    }

    private Map<Property, List<Pair<String, Double>>> determineXPaths(File inputFolder, String concept,
            Map<Property, Gazetteer> gazetteerMapping) {
        XPathGeneratorFactory factory = new XPathGeneratorFactory(TEMP_FOLDER + concept + File.separator
                + INTERMEDIATE_RESULTS_FOLDER);
        Map<Property, Thread> threadMapping = new HashMap<Property, Thread>();
        Thread t;
        for (Property property : gazetteerMapping.keySet()) {
            t = factory.createGeneratorThread(inputFolder, concept, property, gazetteerMapping.get(property));
            t.start();
            threadMapping.put(property, t);
        }
        Map<Property, List<Pair<String, Double>>> xPaths = new HashMap<Property, List<Pair<String, Double>>>();
        for (Property property : threadMapping.keySet()) {
            t = threadMapping.get(property);
            try {
                t.join();
                xPaths.put(property, ((XPathGenerator) t).getXPaths());
            } catch (InterruptedException e) {
                LOGGER.error("Got an exception while waiting for a x path generating thread.", e);
            }
            threadMapping.put(property, t);
        }
        return xPaths;
    }

    private void callLGG(Map<Property, List<Pair<String, Double>>> xpaths) {
        // TODO Auto-generated method stub

    }

    private void applyXPaths(Map<Property, List<Pair<String, Double>>> xpaths) {
        // TODO Auto-generated method stub

    }
}
