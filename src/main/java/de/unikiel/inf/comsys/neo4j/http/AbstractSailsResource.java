
package de.unikiel.inf.comsys.neo4j.http;

import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserRegistry;

public abstract class AbstractSailsResource {
	
	protected final SailRepository rep;
	protected final List<Variant> rdfResultVariants;
	
	public AbstractSailsResource(SailRepository rep) {
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
	
	protected RDFParser getRDFParser(RDFFormat format) {
		return RDFParserRegistry.getInstance().get(format).getParser();
	}
	
	protected SailRepositoryConnection getConnection() throws RepositoryException {
		return rep.getConnection();
	}
	
	protected void close(RepositoryConnection conn) {
		if (conn != null) {
			try {
				if (conn.isOpen()) {
					conn.close();
				}
			} catch (RepositoryException ex) {
				throw new WebApplicationException(ex);
			}
		}
	}
	
	protected void close(RepositoryConnection conn, Exception ex) {
		if (conn != null) {
			try {
				if (conn.isOpen()) {
					conn.close();
				}
			} catch (RepositoryException ex2) {
				ex.addSuppressed(ex2);
			}
		}
	}
}
