package de.unikiel.inf.comsys.neo4j.http.streams;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;

public class SPARQLGraphStreamingOutput extends AbstractStreamingOutput {

	private final GraphQuery query;
	private final RDFWriterFactory factory;

	public SPARQLGraphStreamingOutput(
			GraphQuery query,
			RDFWriterFactory writerFactory,
			RepositoryConnection conn) {
		super(conn);
		this.query = query;
		this.factory = writerFactory;
	}

	@Override
	public void write(OutputStream out)
			throws IOException, WebApplicationException {
		try {
			RDFWriter writer = factory.getWriter(out);
			query.evaluate(writer);
			conn.close();
		} catch (RepositoryException |
				 QueryEvaluationException |
				 RDFHandlerException ex) {
			close(conn, ex);
			throw new WebApplicationException(ex);
		}
	}
}
