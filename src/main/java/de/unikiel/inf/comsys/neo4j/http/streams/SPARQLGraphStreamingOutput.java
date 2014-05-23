
package de.unikiel.inf.comsys.neo4j.http.streams;

import info.aduna.iteration.CloseableIteration;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;

public class SPARQLGraphStreamingOutput implements StreamingOutput {

	private final ParsedQuery query;
	private final CloseableIteration<? extends BindingSet, QueryEvaluationException> results;
	private final RDFWriterFactory factory;
	private final ValueFactory vf;
	
	public SPARQLGraphStreamingOutput(
		ParsedQuery query,
		CloseableIteration<? extends BindingSet, QueryEvaluationException> results,
		RDFWriterFactory writerFactory,
		ValueFactory vf) {
		this.query   = query;
		this.results = results;
		this.factory = writerFactory;
		this.vf      = vf;
	}
	
	@Override
	public void write(OutputStream out)
		throws IOException, WebApplicationException {
			try {
				Statement stmnt;
				BindingSet next;
				RDFWriter writer = factory.getWriter(out);
				Set<String> set = query.getTupleExpr().getAssuredBindingNames();
				List<String> bindings = new ArrayList<>(set);
				writer.startRDF();
				while (results.hasNext()) {
					next = results.next();
					stmnt = vf.createStatement(
						(Resource) next.getValue("subject"),
						(URI) next.getValue("predicate"),
						next.getValue("object")
					);
					writer.handleStatement(stmnt);
				}
				writer.endRDF();
			} catch (QueryEvaluationException ex) {
				throw new WebApplicationException(ex);
			} catch (RDFHandlerException ex) {
			Logger.getLogger(SPARQLGraphStreamingOutput.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
}
