package de.unikiel.inf.comsys.neo4j.http.streams;

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
