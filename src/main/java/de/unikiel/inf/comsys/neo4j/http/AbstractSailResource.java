package de.unikiel.inf.comsys.neo4j.http;

/*
 * #%L
 * neo4j-sparql-extension
 * %%
 * Copyright (C) 2014 Niclas Hoyer
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
