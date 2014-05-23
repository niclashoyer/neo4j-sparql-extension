
package de.unikiel.inf.comsys.neo4j.http;

import de.unikiel.inf.comsys.neo4j.http.streams.SPARQLStreamingOutput;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.Variant;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriterFactory;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriterFactory;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriterFactory;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriterFactory;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

public class SPARQLQuery extends AbstractSailsResource {
	
	private final SPARQLParser parser;
	private final List<Variant> queryResultVariants;
	
	public SPARQLQuery(SailConnection sc, ValueFactory vf) {
		super(sc, vf);
		parser = new SPARQLParser();
		queryResultVariants = Variant.mediaTypes(
			MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_JSON),
			MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_XML),
			MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_CSV),
			MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_TSV)
		).add().build();
	}
	
    @GET
    @Produces({
		RDFMediaType.SPARQL_RESULTS_JSON,
		RDFMediaType.SPARQL_RESULTS_XML,
		RDFMediaType.SPARQL_RESULTS_CSV,
		RDFMediaType.SPARQL_RESULTS_TSV
	})
    public Response query(
			@Context Request req,
			@QueryParam("query") String queryString,
			@QueryParam("default-graph-uri") List<String> defgraphs,
			@QueryParam("named-graph-uri") List<String> namedgraphs) {
		try {
			TupleQueryResultWriterFactory factory;
			final MediaType mt    = req.selectVariant(queryResultVariants)
					                   .getMediaType();
			final String    mtstr = mt.getType() + "/" + mt.getSubtype();
			switch(mtstr) {
				case RDFMediaType.SPARQL_RESULTS_JSON:
					factory = new SPARQLResultsJSONWriterFactory();
					break;
				case RDFMediaType.SPARQL_RESULTS_XML:
					factory = new SPARQLResultsXMLWriterFactory();
					break;
				case RDFMediaType.SPARQL_RESULTS_CSV:
					factory = new SPARQLResultsCSVWriterFactory();
					break;
				case RDFMediaType.SPARQL_RESULTS_TSV:
					factory = new SPARQLResultsTSVWriterFactory();
					break;
				default:
					factory = new SPARQLResultsJSONWriterFactory();
					break;
			}
			final CloseableIteration<? extends BindingSet, QueryEvaluationException> results;
			final ParsedQuery query = parser.parseQuery(queryString, "http://example.com/");
			final TupleExpr te      = query.getTupleExpr();
			results = sc.evaluate(
					te,
					query.getDataset(),
					new EmptyBindingSet(),
					false);
			StreamingOutput stream = new SPARQLStreamingOutput(
				query,
				results,
				factory);
			return Response.ok(stream).build();
		} catch (MalformedQueryException ex) {
			String str = ex.getMessage();
			return Response.status(Response.Status.BAD_REQUEST).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (SailException ex) {
			throw new WebApplicationException(ex);
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
