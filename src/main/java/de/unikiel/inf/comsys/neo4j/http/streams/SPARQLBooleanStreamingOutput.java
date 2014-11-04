
package de.unikiel.inf.comsys.neo4j.http.streams;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.eclipse.jetty.io.EofException;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryInterruptedException;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.resultio.BooleanQueryResultWriter;
import org.openrdf.query.resultio.BooleanQueryResultWriterFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class SPARQLBooleanStreamingOutput extends AbstractStreamingOutput {

	private final BooleanQuery query;
	private final BooleanQueryResultWriterFactory factory;
	
	public SPARQLBooleanStreamingOutput(
		BooleanQuery query,
		BooleanQueryResultWriterFactory writerFactory,
		RepositoryConnection conn) {
		super(conn);
		this.query = query;
		this.factory = writerFactory;
	}
	
	@Override
	public void write(OutputStream out)
		throws IOException, WebApplicationException {
			try {
				BooleanQueryResultWriter writer = factory.getWriter(out);
				conn.begin();
				boolean result = query.evaluate();
				writer.startDocument();
				writer.handleBoolean(result);
				conn.commit();
				conn.close();
			} catch (QueryInterruptedException ex) {
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
