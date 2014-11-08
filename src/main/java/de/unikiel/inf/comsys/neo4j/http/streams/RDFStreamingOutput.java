package de.unikiel.inf.comsys.neo4j.http.streams;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;

/**
 * A {@link StreamingOutput} implementation that streams RDF triples from a
 * graph.
 */
public class RDFStreamingOutput extends AbstractStreamingOutput {

	private final RDFWriterFactory factory;
	private final Resource[] contexts;

	/**
	 * Create a new RDF streaming output that uses the given connection to
	 * stream RDF triples in the given format from the a graph.
	 *
	 * @param conn the repository connection
	 * @param format the RDF format
	 * @param contexts the graphs to stream from
	 */
	public RDFStreamingOutput(
			RepositoryConnection conn,
			RDFFormat format,
			Resource... contexts) {
		super(conn);
		this.contexts = contexts;
		this.factory = RDFWriterRegistry.getInstance().get(format);
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
			// get an RDF writer to stream the triples
			RDFWriter writer = factory.getWriter(out);
			// export triples from graphs
			conn.export(writer, contexts);
			conn.close();
		} catch (RepositoryException | RDFHandlerException ex) {
			// server error
			close(conn, ex);
			throw new WebApplicationException(ex);
		}
	}
}
