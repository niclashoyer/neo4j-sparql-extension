
package de.unikiel.inf.comsys.neo4j.http.streams;

import info.aduna.iteration.CloseableIteration;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;

public class SPARQLResultStreamingOutput implements StreamingOutput {

	private final ParsedQuery query;
	private final CloseableIteration<? extends BindingSet, QueryEvaluationException> results;
	private final TupleQueryResultWriterFactory factory;
	
	public SPARQLResultStreamingOutput(
		ParsedQuery query,
		CloseableIteration<? extends BindingSet, QueryEvaluationException> results,
		TupleQueryResultWriterFactory writerFactory) {
		this.query = query;
		this.results = results;
		this.factory = writerFactory;
	}
	
	@Override
	public void write(OutputStream out)
		throws IOException, WebApplicationException {
			try {
				TupleQueryResultWriter writer = factory.getWriter(out);
				Set<String> set = query.getTupleExpr().getAssuredBindingNames();
				List<String> bindings = new ArrayList<>(set);
				writer.startDocument();
				writer.startQueryResult(bindings);
				while (results.hasNext()) {
					BindingSet next = results.next();
					writer.handleSolution(next);
				}
				writer.endQueryResult();
			} catch (QueryEvaluationException |
					 QueryResultHandlerException ex) {
				throw new WebApplicationException(ex);
			} finally {
				try {
					results.close();
				} catch (QueryEvaluationException ex) {
					throw new WebApplicationException(ex);
				}
			}
	}
	
}
