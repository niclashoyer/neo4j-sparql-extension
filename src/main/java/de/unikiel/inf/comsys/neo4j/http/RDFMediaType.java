package de.unikiel.inf.comsys.neo4j.http;

public abstract class RDFMediaType {
	public static final String SPARQL_RESULTS_JSON = "application/sparql-results+json";
	public static final String SPARQL_RESULTS_XML = "application/sparql-results+xml";
	public static final String SPARQL_RESULTS_CSV = "text/csv";
	public static final String SPARQL_RESULTS_TSV = "text/tab-separated-values";
	public static final String SPARQL_QUERY = "application/sparql-query";
	public static final String SPARQL_UPDATE = "application/sparql-update";
	public static final String RDF_TURTLE = "text/turtle";
	public static final String RDF_JSON = "application/rdf+json";
	public static final String RDF_XML = "application/rdf+xml";
	public static final String RDF_NTRIPLES = "application/n-triples";
}
