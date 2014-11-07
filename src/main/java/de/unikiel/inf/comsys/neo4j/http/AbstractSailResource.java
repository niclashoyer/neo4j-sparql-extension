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

/**
 * Abstract super class for the implementation of RDF and SPARQL resources.
 */
public abstract class AbstractSailResource {

	protected final SailRepository rep;
	protected final List<Variant> rdfResultVariants;

	/**
	 * Initialize result variants and save reference to repository.
	 * @param rep reference to repository
	 */
	public AbstractSailResource(SailRepository rep) {
		this.rep = rep;
		rdfResultVariants = Variant.mediaTypes(
				MediaType.valueOf(RDFMediaType.RDF_TURTLE),
				MediaType.valueOf(RDFMediaType.RDF_NTRIPLES),
				MediaType.valueOf(RDFMediaType.RDF_XML),
				MediaType.valueOf(RDFMediaType.RDF_JSON)
		).add().build();
	}

	/**
	 * Returns an instance of {@link org.openrdf.rio.RDFFormat} for a
	 * given MIME-Type string.
	 *
	 * @param mimetype the MIME-Type as string
	 * @return the corresponding RDF-Format
	 */
	protected RDFFormat getRDFFormat(String mimetype) {
		switch (mimetype) {
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

	/**
	 * Returns the corresponding RDF parser for a given RDF format.
	 *
	 * @param format the RDF format
	 * @return RDF parser
	 */
	protected RDFParser getRDFParser(RDFFormat format) {
		return RDFParserRegistry.getInstance().get(format).getParser();
	}

	/**
	 * Returns a new connection for the current repository.
	 *
	 * @return a new connection
	 * @throws RepositoryException if there was a problem getting the connection
	 */
	protected SailRepositoryConnection getConnection()
			throws RepositoryException {
		return rep.getConnection();
	}

	/**
	 * Closes a repository connection if it is open. Does nothing if it is
	 * already closed.
	 *
	 * @param conn the connection to close
	 * @throws WebApplicationException if there was a problem while closing the
	 * connection
	 */
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

	/**
	 * Closes a repository connection it it is open. Does nothing if it is
	 * closed. If an exception occurs while closing the connection it will be
	 * added as suppressed exception to the given exception.
	 *
	 * @param conn the connection to close
	 * @param ex an exception that caused the closing of the connection
	 */
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
