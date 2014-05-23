
package de.unikiel.inf.comsys.neo4j.http;

import de.unikiel.inf.comsys.neo4j.http.streams.SPARQLGraphStreamingOutput;
import de.unikiel.inf.comsys.neo4j.http.streams.SPARQLResultStreamingOutput;
import info.aduna.iteration.CloseableIteration;
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
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Variant;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.QueryParserUtil;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriterFactory;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriterFactory;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriterFactory;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriterFactory;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.ntriples.NTriplesWriterFactory;
import org.openrdf.rio.rdfjson.RDFJSONWriterFactory;
import org.openrdf.rio.rdfxml.RDFXMLWriterFactory;
import org.openrdf.rio.turtle.TurtleWriterFactory;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

public class SPARQLQuery extends AbstractSailsResource {
	
	private final List<Variant> queryResultVariants;
	private final List<Variant> rdfResultVariants;
	
	public SPARQLQuery(SailConnection sc, ValueFactory vf) {
		super(sc, vf);
		queryResultVariants = Variant.mediaTypes(
			MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_JSON),
			MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_XML),
			MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_CSV),
			MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_TSV)
		).add().build();
		rdfResultVariants = Variant.mediaTypes(
			MediaType.valueOf(RDFMediaType.RDF_TURTLE),
			MediaType.valueOf(RDFMediaType.RDF_NTRIPLES),
			MediaType.valueOf(RDFMediaType.RDF_XML),
			MediaType.valueOf(RDFMediaType.RDF_JSON)
		).add().build();
	}
	
    @GET
    @Produces({
		RDFMediaType.SPARQL_RESULTS_JSON,
		RDFMediaType.SPARQL_RESULTS_XML,
		RDFMediaType.SPARQL_RESULTS_CSV,
		RDFMediaType.SPARQL_RESULTS_TSV,
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_JSON
	})
    public Response query(
			@Context Request req,
			@Context UriInfo uriInfo,
			@QueryParam("query") String queryString,
			@QueryParam("default-graph-uri") List<String> defgraphs,
			@QueryParam("named-graph-uri") List<String> namedgraphs) {
		return handleQuery(req, uriInfo, queryString, defgraphs, namedgraphs);
    }
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response queryPOSTEncoded(
			@Context Request req,
			@Context UriInfo uriInfo,
			@FormParam("query") String queryString,
			@FormParam("default-graph-uri") List<String> defgraphs,
			@FormParam("named-graph-uri") List<String> namedgraphs) {
		return handleQuery(req, uriInfo, queryString, defgraphs, namedgraphs);
	}

	@POST
	@Consumes("application/sparql-query")
	public Response queryPOSTDirect(
			@Context Request req,
			@Context UriInfo uriInfo,
			@FormParam("default-graph-uri") List<String> defgraphs,
			@FormParam("named-graph-uri") List<String> namedgraphs,
			String queryString) {
		return handleQuery(req, uriInfo, queryString, defgraphs, namedgraphs);
	}
	
	private Response handleQuery (
			Request req,
			UriInfo uriInfo,
			String queryString,
			List<String> defgraphs,
			List<String> namedgraphs) {
		try {
			final CloseableIteration<? extends BindingSet, QueryEvaluationException> results;
			final ParsedQuery query = QueryParserUtil.parseQuery(
				QueryLanguage.SPARQL,
				queryString,
				uriInfo.getAbsolutePath().toASCIIString());
			final List<Variant> acceptable;
			boolean isGraphQuery = false;
			if (query instanceof ParsedGraphQuery) {
				acceptable = rdfResultVariants;
				isGraphQuery = true;
			} else {
				acceptable = queryResultVariants;
			}
			final Variant variant = req.selectVariant(acceptable);
			if (variant == null) {
				return Response.notAcceptable(acceptable).build();
			}
			final MediaType mt = variant.getMediaType();
			final String mtstr = mt.getType() + "/" + mt.getSubtype();
			results = sc.evaluate(
					query.getTupleExpr(),
					query.getDataset(),
					new EmptyBindingSet(),
					false);
			StreamingOutput stream;
			if (isGraphQuery) {
				stream = new SPARQLGraphStreamingOutput(
					query,
					results,
					getRDFWriterFactory(mtstr),
					vf);				
			} else {
				stream = new SPARQLResultStreamingOutput(
					query,
					results,
					getResultWriterFactory(mtstr));
			}
			return Response.ok(stream).type(mt).build();
		} catch (MalformedQueryException ex) {
			String str = ex.getMessage();
			return Response.status(Response.Status.BAD_REQUEST).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (SailException ex) {
			throw new WebApplicationException(ex);
		}
    }
	
	private RDFWriterFactory getRDFWriterFactory(String mimetype) {
		switch (mimetype) {
			default:
			case RDFMediaType.RDF_TURTLE:
				return new TurtleWriterFactory();
			case RDFMediaType.RDF_NTRIPLES:
				return new NTriplesWriterFactory();
			case RDFMediaType.RDF_XML:
				return new RDFXMLWriterFactory();
			case RDFMediaType.RDF_JSON:
				return new RDFJSONWriterFactory();
		}
	}
	
	private TupleQueryResultWriterFactory getResultWriterFactory(String mimetype) {
		switch (mimetype) {
			default:
			case RDFMediaType.SPARQL_RESULTS_JSON:
				return new SPARQLResultsJSONWriterFactory();
			case RDFMediaType.SPARQL_RESULTS_XML:
				return new SPARQLResultsXMLWriterFactory();
			case RDFMediaType.SPARQL_RESULTS_CSV:
				return new SPARQLResultsCSVWriterFactory();
			case RDFMediaType.SPARQL_RESULTS_TSV:
				return new SPARQLResultsTSVWriterFactory();
		}
	}
}
