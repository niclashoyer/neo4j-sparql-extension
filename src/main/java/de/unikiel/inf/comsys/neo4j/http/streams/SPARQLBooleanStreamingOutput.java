
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.jetty.io.EofException;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.BooleanQueryResultWriterFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * A {@link StreamingOutput} implementation that streams SPARQL ASK results.
 */
public class SPARQLBooleanStreamingOutput extends AbstractStreamingOutput {

	private final BooleanQuery query;
	private final BooleanQueryResultWriterFactory factory;
	
	/**
	 * Create a new boolean streaming output that executes the given
	 * query and returns the answer.
	 * 
	 * @param query the ASK query
	 * @param writerFactory a writer factory that creates boolean results
	 * @param conn the connection to use for query execution
	 */
	public SPARQLBooleanStreamingOutput(
		BooleanQuery query,
		BooleanQueryResultWriterFactory writerFactory,
		RepositoryConnection conn) {
		super(conn);
		this.query = query;
		this.factory = writerFactory;
	}
	
	/**
	 * Called by JAX-RS upon building a response.
	 * 
	 * @param out the {@link OutputStream} to write the result to
	 * @throws IOException if there was an error during communication
	 * @throws WebApplicationException if there was an error while serialising
	 */
	@Override
	public void write(OutputStream out)
		throws IOException, WebApplicationException {
			try {
				BooleanQueryResultWriter writer = factory.getWriter(out);
				conn.begin();
				boolean result = query.evaluate();
				// handover boolean result to writer
				writer.startDocument();
				writer.handleBoolean(result);
				conn.commit();
				conn.close();
			} catch (QueryInterruptedException ex) {
				// query execution timeout
				close(conn, ex);
				Response res = Response
						.status(Response.Status.SERVICE_UNAVAILABLE)
						.header("X-Max-Response-Time", query.getMaxQueryTime())
						.header("Access-Control-Allow-Origin", "*")
						.build();
				throw new WebApplicationException(ex, res);
			} catch (RepositoryException |
					 QueryEvaluationException |
					 QueryResultHandlerException ex) {
				// server error
				close(conn, ex);
				Throwable cause = ex.getCause();
				// An EofException occurs, if the client closes the connection
				// while we are streaming the response. If the client is not
				// interested in the response, we should just close the
				// connection instead of returning an error.
				if (!(cause instanceof EofException)) {
					throw new WebApplicationException(ex);
				}
			}
	}
	
}
