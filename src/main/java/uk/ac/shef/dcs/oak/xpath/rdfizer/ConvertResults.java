package uk.ac.shef.dcs.oak.xpath.rdfizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * @author annalisa
 *
 */
public class ConvertResults {

	public Model toRDF(CSVReader csvReader, String wsName) {
		Model model = ModelFactory.createDefaultModel();

		String split[] = wsName.split("-");

		String domain = split[0];
		String website = split[1];
		String attribute = split[2];

		// TODO change to relevant namespaces

		try {

			// line[0] is the page id
			// line[1] is the number of results n
			// line[2]...line[n+1] contain the value(s) of the attribute

			String[] line = null;
			while ((line = csvReader.readNext()) != null) {
				if (line.length > 1) {
					String id = line[0].trim();
					System.out.println(attribute + ": "
							+ OfficialNameSpace.lodieOntology + domain + "/"
							+ website + "/" + id);
					Resource domainItem = model.createResource(
							OfficialNameSpace.lodieOntology + domain + "/"
									+ website + "/" + id,
							OfficialNameSpace.types.get(domain));
					domainItem
							.addLiteral(RDFS.label, model.createTypedLiteral(
									id, XSDDatatype.XSDstring));
					domainItem
							.addLiteral(OfficialNameSpace.website, model
									.createTypedLiteral(website,
											XSDDatatype.XSDstring));

					String attValue = "";
					int n = Integer.parseInt(line[1]);
					for (int i = 2; i <= n + 1; i++) {
						try {
							attValue = line[i].trim();
						} catch (Exception e) {
							e.printStackTrace();
							System.err.println(" ERROR with " + wsName
									+ " --> " + line);
						}

						// TODO save to model
						domainItem.addLiteral(OfficialNameSpace.properties
								.get(domain + "-" + attribute), model
								.createTypedLiteral(attValue,
										XSDDatatype.XSDstring));

					}
				} else {
					csvReader.readNext();
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;

	}

	public Model toRDF(CSVReader csvReader) {
		return null;

	}

	public Model buildRDF(File directory) {
		Model model = null;

		String sparql = "PREFIX afn: <http://jena.hpl.hp.com/ARQ/function#> "
				+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
				+ "PREFIX co: <http://purl.org/co/> "
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX dcterms: <http://purl.org/dc/terms/> "
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "PREFIX swrc: <http://swrc.ontoware.org/ontology#> "
				+ "PREFIX lodie: <"
				+ OfficialNameSpace.lodieOntology
				+ "> "
				+ "PREFIX rdfs: <"
				+ RDFS.getURI()
				+ "> "
				+ "CONSTRUCT {"
				+ "    ?book a  <http://schema.org/Book> . "
				+ "    ?book dc:title ?title . "
				+ "    ?book lodie:author ?author . "
				+ "    ?book lodie:website ?website . "
				+ "    ?book rdfs:label ?label . "
				+ "    ?book <"
				+ OfficialNameSpace.isbnProperty.getURI()
				+ "> ?isbn . "
				+ "    ?book <"
				+ OfficialNameSpace.publicationdateProperty.getURI()
				+ "> ?pub_date . "
				+ "    ?book <"
				+ OfficialNameSpace.publisherProperty.getURI()
				+ "> ?publisher . "
				+
				"}"
				+ "WHERE{"
				+ "    ?book a <http://schema.org/Book> ."
				+ "    OPTIONAL { ?book lodie:website ?website . }"
				+ "    OPTIONAL { ?book dc:title ?title . }"
				+ "    OPTIONAL { ?book <http://lodie.co.uk/ontology/bookAuthor> ?author . }"
				+ "    OPTIONAL { ?book <"
				+ OfficialNameSpace.isbnProperty.getURI()
				+ "> ?isbn . }"
				+ "    OPTIONAL { ?book <"
				+ OfficialNameSpace.publisherProperty.getURI()
				+ "> ?publisher . }"
				+ "    OPTIONAL { ?book <"
				+ OfficialNameSpace.publicationdateProperty.getURI()
				+ "> ?pub_date . }"
				+ "    OPTIONAL { ?book rdfs:label ?label . }" +
				"}";
		if (directory.isDirectory()) {

			model = ModelFactory.createDefaultModel();

			File[] files = directory.listFiles();

			int i = 0;

			for (File file : files) {

				if (file.getName().endsWith(".rdf")) {

					if (i == 0) {
						i += 1;
						// model = ModelFactory.createDefaultModel();
					}

					System.out.println("Loading " + file.getAbsolutePath());
					Model localRDF = FileManager.get().loadModel(
							file.getAbsolutePath());
					
					Query query = QueryFactory.create(sparql, Syntax.syntaxARQ);
					QueryExecution queryExecution = QueryExecutionFactory
							.create(query, localRDF);
					Model tmpModel = queryExecution.execConstruct();
					model.add(tmpModel);

				}

			}
		}

		model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		model.setNsPrefix("dc-terms", "http://purl.org/dc/terms/");
		model.setNsPrefix("lodie", OfficialNameSpace.lodieOntology);
		model.setNsPrefix("dbp", "http://dbpedia.org/ontology/");
		model.setNsPrefix("schema", "http://schema.org/");

		return model;
	}

	public static void main(String[] args) {
		ConvertResults formData = new ConvertResults();

		String resFolder = "./extractionResults/testExperiment/WI/book";
		String tempFolder = "./extractionResults/rdf/";
		String resultFileName = "./extractionResults/rdf/testExperiment.rdf";

		File extractionFolder = new File(resFolder);
		File rdfResultFolder = new File(tempFolder);
		rdfResultFolder.mkdirs();

		if (extractionFolder.isDirectory()) {
			for (File f : extractionFolder.listFiles()) {
				if (f.getName().endsWith(".txt")) {
					String wsName = f.getName().substring(0,
							f.getName().lastIndexOf(".txt"));

					try {
						CSVReader csvReader = new CSVReader(
								new InputStreamReader(new FileInputStream(f),
										"UTF-8"), '\t');
						Model model = formData.toRDF(csvReader, wsName);
						csvReader.close();
						OutputStream out = new FileOutputStream(new File(
								rdfResultFolder + File.separator + wsName
										+ ".rdf"));
						model.write(out);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			System.out
					.println("Expecting a folder "+extractionFolder.getAbsolutePath());
		}

		Model mod = formData.buildRDF(new File(tempFolder));
		OutputStream out = null;
		try {
			out = new FileOutputStream(resultFileName);
			mod.write(out);

			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
