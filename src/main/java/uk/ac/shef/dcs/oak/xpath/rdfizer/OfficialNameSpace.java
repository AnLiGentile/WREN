package uk.ac.shef.dcs.oak.xpath.rdfizer;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author annalisa
 *
 */
public class OfficialNameSpace {
	// TODO change to relevant namespaces, these are just demonstrational
	public static String lodieOntology = "http://lodie.co.uk/ontology/";

	static Model model = ModelFactory.createDefaultModel();

	static Resource book = model.createResource("http://schema.org/Book");
	static Resource film = model.createResource("http://schema.org/Movie");
	static Resource musicAlbum = model
			.createResource("http://schema.org/MusicRecording");

	static String dboedia_name = "http://dbpedia.org/property/name";
	static String bookTitle = "http://dbpedia.org/property/title";// or directly
																	// dboedia_name
	static String bookAuthor = "http://dbpedia.org/property/author";// then
																	// dboedia_name
	static String bookIsbn = "http://dbpedia.org/property/isbn";
	static String bookPublisher = "http://dbpedia.org/ontology/publisher";// then
																			// dboedia_name
	static String bookDate = "http://dbpedia.org/property/releaseDate";// then
																		// dboedia_name

	static Property authorProperty = model.createProperty(lodieOntology
			+ "bookAuthor");
	static Property titleProperty = model
			.createProperty("http://purl.org/dc/elements/1.1/title");
	static Property isbnProperty = model.createProperty(bookIsbn);
	static Property publisherProperty = model.createProperty(bookPublisher);
	static Property publicationdateProperty = model.createProperty(bookDate);

	public static Property website = model.createProperty(lodieOntology
			+ "website");

	public static Map<String, Resource> types = new HashMap<String, Resource>();

	static {
		types.put("book", book);
		types.put("film", film);
		types.put("music", musicAlbum);
	}

	public static Map<String, Property> properties = new HashMap<String, Property>();

	static {
		properties.put("book-author", authorProperty);
		properties.put("book-title", titleProperty);
		properties.put("book-isbn", isbnProperty);
		properties.put("book-publisher", publisherProperty);
		properties.put("book-publication_date", publicationdateProperty);

		// TODO add other
	}

}
