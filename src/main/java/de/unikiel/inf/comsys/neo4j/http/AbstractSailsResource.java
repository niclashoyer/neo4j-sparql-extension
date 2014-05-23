
package de.unikiel.inf.comsys.neo4j.http;

import org.openrdf.model.ValueFactory;
import org.openrdf.sail.SailConnection;

public class AbstractSailsResource {
	protected final SailConnection sc;
	protected final ValueFactory vf;
	
	protected static class Status {
		public static final int NOT_IMPLEMENTED = 501;
	}
	
	protected static class RDFMediaType {
		public static final String SPARQL_RESULTS_JSON = "application/sparql-results+json";
		public static final String SPARQL_RESULTS_XML = "application/sparql-results+xml";
		public static final String SPARQL_RESULTS_CSV = "text/csv";
		public static final String SPARQL_RESULTS_TSV = "text/tab-separated-values";
		public static final String SPARQL_QUERY = "application/sparql-query";
		public static final String SPARQL_UPDATE = "application/sparql-update";
		public static final String RDF_TURTLE = "text/turtle";
		public static final String RDF_JSONLD = "application/ld+json";
		public static final String RDF_XML = "application/rdf+xml";
	}
	
	public AbstractSailsResource(SailConnection sc, ValueFactory vf) {
		this.sc = sc;
		this.vf = vf;
	}
}
