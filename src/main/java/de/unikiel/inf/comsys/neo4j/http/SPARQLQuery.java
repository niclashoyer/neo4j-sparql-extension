
package de.unikiel.inf.comsys.neo4j.http;

import info.aduna.iteration.CloseableIteration;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.sail.SailConnection;

public class SPARQLQuery extends AbstractSailsResource {
	
	public SPARQLQuery(SailConnection sc, ValueFactory vf) {
		super(sc, vf);
	}
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response query(@QueryParam("query") String queryString) {
		try {
			String str = "";
			SPARQLParser parser = new SPARQLParser();
			CloseableIteration<? extends BindingSet, QueryEvaluationException> sparqlResults;
			ParsedQuery query = parser.parseQuery(queryString, "http://example.com/");
			sparqlResults = sc.evaluate(query.getTupleExpr(), query.getDataset(), new EmptyBindingSet(), false);
			while (sparqlResults.hasNext()) {
				str += sparqlResults.next() + "\n";
			}
			return Response.status(Response.Status.OK).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (Exception ex) {
			// DEBUG
			String str = ex.toString();
			return Response.status(500).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		}
    }
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response queryPOSTEncoded(
			@FormParam("query") String query,
			@FormParam("default-graph-uri") List<String> defgraphs,
			@FormParam("named-graph-uri") List<String> namedgraphs) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}

	@POST
	@Consumes("application/sparql-query")
	public Response queryPOSTDirect(
			@FormParam("default-graph-uri") List<String> defgraphs,
			@FormParam("named-graph-uri") List<String> namedgraphs,
			InputStream query) {
		return Response.status(Status.NOT_IMPLEMENTED).build();
	}
}
