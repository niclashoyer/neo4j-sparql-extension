
package de.unikiel.inf.comsys.neo4j.http.streams;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.jetty.io.EofException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * A {@link StreamingOutput} implementation that streams the results from
 * a SPARQL SELECT query.
 */
public class SPARQLTupleStreamingOutput extends AbstractStreamingOutput {

	private final TupleQuery query;
	private final TupleQueryResultWriterFactory factory;
	
	/**
	 * Creates a new sparql tuple streaming output that executes a SPARQL
	 * SELECT query and streams the result.
	 * @param query the SELECT query to execute
	 * @param writerFactory a result writer factory used to serialise the
	 * results
	 * @param conn the connection to use for query execution
	 */
	public SPARQLTupleStreamingOutput(
		TupleQuery query,
		TupleQueryResultWriterFactory writerFactory,
		RepositoryConnection conn) {
		super(conn);
		this.query = query;
		this.factory = writerFactory;
	}
	
	/**
	 * Called by JAX-RS upon building a response.
	 *
	 * @param out the {@link OutputStream} to write the results to
	 * @throws IOException if there was an error during communication
	 * @throws WebApplicationException if there was an error while serialising
	 */
	@Override
	public void write(OutputStream out)
		throws IOException, WebApplicationException {
			try {
				TupleQueryResultWriter writer = factory.getWriter(out);
				conn.begin();
				// run query and stream results
				query.evaluate(writer);
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
				// connection.
				if (!(cause instanceof EofException)) {
					throw new WebApplicationException(ex);
				}
			}
	}
	
}
