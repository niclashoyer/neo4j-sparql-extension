
package de.unikiel.inf.comsys.neo4j.http.streams;

import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;

public class SPARQLResultStreamingOutput implements StreamingOutput {

	private final TupleQuery query;
	private final TupleQueryResultWriterFactory factory;
	
	public SPARQLResultStreamingOutput(
		TupleQuery query,
		TupleQueryResultWriterFactory writerFactory) {
		this.query = query;
		this.factory = writerFactory;
	}
	
	@Override
	public void write(OutputStream out)
		throws IOException, WebApplicationException {
			try {
				TupleQueryResultWriter writer = factory.getWriter(out);
				query.evaluate(writer);
			} catch (QueryEvaluationException |
					 QueryResultHandlerException ex) {
				throw new WebApplicationException(ex);
			}
	}
	
}
