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

public class RDFStreamingOutput implements StreamingOutput {

	private final RepositoryConnection conn;
	private final RDFWriterFactory factory;
	private final Resource[] contexts;

	public RDFStreamingOutput(
			RepositoryConnection conn,
			RDFFormat format,
			Resource... contexts) {
		this.conn     = conn;
		this.contexts = contexts;
		this.factory  = RDFWriterRegistry.getInstance().get(format);
	}

	@Override
	public void write(OutputStream out)
			throws IOException, WebApplicationException {
		try {
			RDFWriter writer = factory.getWriter(out);
			conn.export(writer, contexts);
		} catch (RepositoryException | RDFHandlerException ex) {
			throw new WebApplicationException(ex);
		}
	}
}
