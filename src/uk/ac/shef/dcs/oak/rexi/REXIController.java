package uk.ac.shef.dcs.oak.rexi;

/**
 * FIXME PLEASE RENAME ME AND MY PACKAGE!!!
 * 
 * @author Michael R&ouml;der (roeder@informatik.uni-leipzig.de)
 *
 */
public class REXIController {

    public static void main(String[] args) {
        
        /*
         * 1. identify concept A in K (this would be a mock-up, not the focus of
         * the paper)
         */
        
        /*
         * 2. identify a set of properties related to A, P = {p_1 ...p_n} that
         * we want to extract (n can be all of them)
         */
        
        /*
         * 3. collect one gazetteer of each p_i in P by looking at all
         * occurrences of concept A in KB both in subj or obj
         */
        
        /*
         * 4. learning automaton for each p_i at this step we know if: the
         * attribute we want to extract is a “label-like” attribute (mostly 1to1
         * relation attributes, such as titles, names…) the attribute has a
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
        /*
         * 8. @RU @ALG extraction We apply the extractors returned by (7) to all
         * pages in d_i and create a an attribute representation for each w_i in
         * d_i, i.e. a set of triples having as subject the entity contained in
         * w_1 as predicates all p_i in P and has objects all relevant values
         * for the p_i that we can find in page w_i using the provided
         * extractors
         */
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
}
