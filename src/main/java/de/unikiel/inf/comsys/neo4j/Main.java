
package de.unikiel.inf.comsys.neo4j;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;
import info.aduna.iteration.CloseableIteration;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

public class Main {

	public static void main(String[] args) throws URISyntaxException, SailException, IOException, RDFParseException, RDFHandlerException, MalformedQueryException, QueryEvaluationException {
		Graph graph = new Neo4j2Graph("test");
		Sail sail = new GraphSail((KeyIndexableGraph) graph);
		sail.initialize();
		SailConnection sc = sail.getConnection();
		ValueFactory vf = sail.getValueFactory();
		
		Resource dctx = vf.createURI("http://kai.uni-kiel.de/13c1de33-3183-d1b2-1025-4d39c46bdbf0");
		
		InputStream in = Main.class.getResourceAsStream("/scheel.ttl");
		
		RDFParser p = Rio.createParser(RDFFormat.TURTLE);
		p.setRDFHandler(new SailsRDFHandler(sc, dctx));
		p.parse(in, dctx.stringValue());
		
		SPARQLParser parser = new SPARQLParser();
		CloseableIteration<? extends BindingSet, QueryEvaluationException> sparqlResults;
		String queryString = "PREFIX kai: <http://kai.uni-kiel.de/>\n" +
"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
"\n" +
"SELECT ?p ?puri ?birth WHERE {\n" +
"	{ ?puri a kai:Person } UNION\n" +
"	{ ?puri a kai:AcademicPerson }\n" +
"	{ ?puri rdfs:label ?p\n" +
"		OPTIONAL { ?puri kai:familyNameAtBirth ?birth }\n" +
"		FILTER(contains(lcase(?p), \"scheel\")) } UNION\n" +
"	{ ?puri kai:familyNameAtBirth ?birth\n" +
"		OPTIONAL { ?puri rdfs:label ?p }\n" +
"		FILTER(contains(lcase(?birth), \"scheel\")) }\n" +
"} ORDER BY ASC(?p)";
		ParsedQuery query = parser.parseQuery(queryString, "http://example.com/");

		System.out.println("\nSPARQL: " + queryString);
		sparqlResults = sc.evaluate(query.getTupleExpr(), query.getDataset(), new EmptyBindingSet(), false);
		while (sparqlResults.hasNext()) {
			System.out.println(sparqlResults.next());
		}
		
		sc.close();
		graph.shutdown();
		sail.shutDown();
	}
	
}
