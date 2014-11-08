package de.unikiel.inf.comsys.neo4j.http.streams;

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

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;

/**
 * A {@link StreamingOutput} implementation that streams RDF triples as a
 * result from a SPARQL CONSTRUCT query.
 */
public class SPARQLGraphStreamingOutput extends AbstractStreamingOutput {

	private final GraphQuery query;
	private final RDFWriterFactory factory;

	/**
	 * Creates a new graph streaming output that executes a CONSTRUCT query
	 * and streams the result.
	 * 
	 * @param query the CONSTRUCT query to execute
	 * @param writerFactory a RDF writer factory to use for serialisation
	 * @param conn the connection to use for query execution
	 */
	public SPARQLGraphStreamingOutput(
			GraphQuery query,
			RDFWriterFactory writerFactory,
			RepositoryConnection conn) {
		super(conn);
		this.query = query;
		this.factory = writerFactory;
	}

	/**
	 * Called by JAX-RS upon building a response.
	 *
	 * @param out the {@link OutputStream} to write the triples to
	 * @throws IOException if there was an error during communication
	 * @throws WebApplicationException if there was an error while serialising
	 */
	@Override
	public void write(OutputStream out)
			throws IOException, WebApplicationException {
		try {
			RDFWriter writer = factory.getWriter(out);
			// evaluate query and stream result
			query.evaluate(writer);
			conn.close();
		} catch (RepositoryException |
				 QueryEvaluationException |
				 RDFHandlerException ex) {
			// server error
			close(conn, ex);
			throw new WebApplicationException(ex);
		}
	}
}
