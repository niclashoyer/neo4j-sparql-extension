
package de.unikiel.inf.comsys.neo4j.http;

import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

public class AbstractSailsResource {
	
	protected final Repository rep;
	protected final List<Variant> rdfResultVariants;
	
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
		public static final String RDF_JSON = "application/rdf+json";
		public static final String RDF_XML = "application/rdf+xml";
		public static final String RDF_NTRIPLES = "application/n-triples";
	}
	
	public AbstractSailsResource(Repository rep) {
		this.rep = rep;
		rdfResultVariants = Variant.mediaTypes(
			MediaType.valueOf(RDFMediaType.RDF_TURTLE),
			MediaType.valueOf(RDFMediaType.RDF_NTRIPLES),
			MediaType.valueOf(RDFMediaType.RDF_XML),
			MediaType.valueOf(RDFMediaType.RDF_JSON)
		).add().build();
	}
	
	protected RDFFormat getRDFFormat(String mimetype) {
		switch(mimetype) {
			default:
			case RDFMediaType.RDF_TURTLE:
				return RDFFormat.TURTLE;
			case RDFMediaType.RDF_XML:
				return RDFFormat.RDFXML;
			case RDFMediaType.RDF_NTRIPLES:
				return RDFFormat.NTRIPLES;
			case RDFMediaType.RDF_JSON:
				return RDFFormat.RDFJSON;
		}
	}
	
	protected CloseableRepositoryConnection getConnection() throws RepositoryException {
		return new CloseableRepositoryConnection(rep.getConnection());
	}
	
	protected void close(RepositoryConnection conn, Exception ex) {
		if (conn != null) {
			try {
				if (conn.isOpen()) {
					conn.close();
				}
				if (rep.isInitialized()) {
					rep.shutDown();
				}
			} catch (RepositoryException ex2) {
				ex.addSuppressed(ex2);
			}
		}
	}
}
