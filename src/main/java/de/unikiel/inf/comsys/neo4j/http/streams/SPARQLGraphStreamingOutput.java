package de.unikiel.inf.comsys.neo4j.http.streams;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;

public class SPARQLGraphStreamingOutput implements StreamingOutput {

	private final GraphQuery query;
	private final RDFWriterFactory factory;

	public SPARQLGraphStreamingOutput(
			GraphQuery query,
			RDFWriterFactory writerFactory) {
		this.query = query;
		this.factory = writerFactory;
	}

	@Override
	public void write(OutputStream out)
			throws IOException, WebApplicationException {
		try {
			RDFWriter writer = factory.getWriter(out);
			query.evaluate(writer);
		} catch (QueryEvaluationException | RDFHandlerException ex) {
			throw new WebApplicationException(ex);
		}
	}
}
