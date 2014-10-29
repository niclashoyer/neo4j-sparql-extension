package de.unikiel.inf.comsys.neo4j.http;

import de.unikiel.inf.comsys.neo4j.SPARQLExtensionProps;
import de.unikiel.inf.comsys.neo4j.http.streams.SPARQLBooleanStreamingOutput;
import de.unikiel.inf.comsys.neo4j.http.streams.SPARQLGraphStreamingOutput;
import de.unikiel.inf.comsys.neo4j.http.streams.SPARQLTupleStreamingOutput;
import de.unikiel.inf.comsys.neo4j.inference.QueryRewriter;
import de.unikiel.inf.comsys.neo4j.inference.QueryRewriterFactory;
import java.nio.charset.Charset;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.resultio.BooleanQueryResultWriterFactory;
import org.openrdf.query.resultio.TupleQueryResultWriterFactory;
import org.openrdf.query.resultio.sparqljson.SPARQLBooleanJSONWriterFactory;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriterFactory;
import org.openrdf.query.resultio.sparqlxml.SPARQLBooleanXMLWriterFactory;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriterFactory;
import org.openrdf.query.resultio.text.BooleanTextWriterFactory;
import org.openrdf.query.resultio.text.csv.SPARQLResultsCSVWriterFactory;
import org.openrdf.query.resultio.text.tsv.SPARQLResultsTSVWriterFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;

public class SPARQLQuery extends AbstractSailsResource {

	private final List<Variant> queryResultVariants;
	private final List<Variant> booleanResultVariants;
	private final QueryRewriterFactory qwfactory;
	private final int timeout;

	public SPARQLQuery(SailRepository rep) {
		super(rep);
		queryResultVariants = Variant.mediaTypes(
				MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_JSON),
				MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_XML),
				MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_CSV),
				MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_TSV)
		).add().build();
		booleanResultVariants = Variant.mediaTypes(
				MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_JSON),
				MediaType.valueOf(RDFMediaType.SPARQL_RESULTS_XML),
				MediaType.valueOf(MediaType.TEXT_PLAIN)
		).add().build();
		this.qwfactory = QueryRewriterFactory.getInstance(rep);
		String sout = SPARQLExtensionProps.getProperty("query.timeout");
		this.timeout = Integer.parseInt(sout);
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
			@QueryParam("named-graph-uri") List<String> namedgraphs,
			@QueryParam("inference") String inference) {
		return handleQuery(
				req, uriInfo, queryString, defgraphs, namedgraphs, inference);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response queryPOSTEncoded(
			@Context Request req,
			@Context UriInfo uriInfo,
			@FormParam("query") String queryString,
			@FormParam("default-graph-uri") List<String> defgraphs,
			@FormParam("named-graph-uri") List<String> namedgraphs,
			@FormParam("inference") String inference) {
		return handleQuery(
				req, uriInfo, queryString, defgraphs, namedgraphs, inference);
	}

	@POST
	@Consumes(RDFMediaType.SPARQL_QUERY)
	public Response queryPOSTDirect(
			@Context Request req,
			@Context UriInfo uriInfo,
			@QueryParam("default-graph-uri") List<String> defgraphs,
			@QueryParam("named-graph-uri") List<String> namedgraphs,
			@QueryParam("inference") String inference,
			String queryString) {
		return handleQuery(
				req, uriInfo, queryString, defgraphs, namedgraphs, inference);
	}

	@GET
	@Path("/inference")
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
	public Response queryInference(
			@Context Request req,
			@Context UriInfo uriInfo,
			@QueryParam("query") String queryString,
			@QueryParam("default-graph-uri") List<String> defgraphs,
			@QueryParam("named-graph-uri") List<String> namedgraphs) {
		return handleQuery(
				req, uriInfo, queryString, defgraphs, namedgraphs, "true");
	}

	@POST
	@Path("/inference")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response queryPOSTEncodedInference(
			@Context Request req,
			@Context UriInfo uriInfo,
			@FormParam("query") String queryString,
			@FormParam("default-graph-uri") List<String> defgraphs,
			@FormParam("named-graph-uri") List<String> namedgraphs) {
		return handleQuery(
				req, uriInfo, queryString, defgraphs, namedgraphs, "true");
	}

	@POST
	@Path("/inference")
	@Consumes(RDFMediaType.SPARQL_QUERY)
	public Response queryPOSTDirectInference(
			@Context Request req,
			@Context UriInfo uriInfo,
			@QueryParam("default-graph-uri") List<String> defgraphs,
			@QueryParam("named-graph-uri") List<String> namedgraphs,
			String queryString) {
		return handleQuery(
				req, uriInfo, queryString, defgraphs, namedgraphs, "true");
	}

	private Response handleQuery(
			Request req,
			UriInfo uriInfo,
			String queryString,
			List<String> defgraphs,
			List<String> namedgraphs,
			String inference) {
		SailRepositoryConnection conn = null;
		try {
			if (queryString == null) {
				throw new MalformedQueryException("Missing query parameter");
			}
			conn = getConnection();
			final Query query;
			if (inference != null && inference.equals("true")) {
				QueryRewriter qw = qwfactory.getRewriter(conn);
				query = qw.rewrite(
						QueryLanguage.SPARQL,
						queryString,
						uriInfo.getAbsolutePath().toASCIIString());
			} else {
				query = conn.prepareQuery(
						QueryLanguage.SPARQL,
						queryString,
						uriInfo.getAbsolutePath().toASCIIString());
			}
			query.setMaxQueryTime(timeout);
			final List<Variant> acceptable;
			boolean isGraphQuery = false;
			boolean isBooleanQuery = false;
			if (query instanceof GraphQuery) {
				isGraphQuery = true;
			} else if (query instanceof BooleanQuery) {
				isBooleanQuery = true;
			}
			if (isGraphQuery) {
				acceptable = rdfResultVariants;
			} else if (isBooleanQuery) {
				acceptable = booleanResultVariants;
			} else {
				acceptable = queryResultVariants;
			}
			final Variant variant = req.selectVariant(acceptable);
			if (variant == null) {
				return Response.notAcceptable(acceptable).build();
			}
			final MediaType mt = variant.getMediaType();
			final String mtstr = mt.getType() + "/" + mt.getSubtype();
			StreamingOutput stream;
			if (isGraphQuery) {
				GraphQuery gq = (GraphQuery) query;
				stream = new SPARQLGraphStreamingOutput(
						gq, getRDFWriterFactory(mtstr), conn);
			} else if (isBooleanQuery) {
				BooleanQuery bq = (BooleanQuery) query;
				stream = new SPARQLBooleanStreamingOutput(
						bq, getBooleanWriterFactory(mtstr), conn);
			} else {
				TupleQuery tq = (TupleQuery) query;
				stream = new SPARQLTupleStreamingOutput(
						tq, getTupleWriterFactory(mtstr), conn);
			}
			return Response.ok(stream).type(mt).build();
		} catch (MalformedQueryException ex) {
			close(conn, ex);
			String str = ex.getMessage();
			return Response.status(Response.Status.BAD_REQUEST).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (RepositoryException ex) {
			close(conn, ex);
			throw new WebApplicationException(ex);
		}
	}

	private RDFWriterFactory getRDFWriterFactory(String mimetype) {
		RDFWriterRegistry registry = RDFWriterRegistry.getInstance();
		return registry.get(getRDFFormat(mimetype));
	}

	private TupleQueryResultWriterFactory getTupleWriterFactory(String mimetype) {
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

	private BooleanQueryResultWriterFactory getBooleanWriterFactory(String mimetype) {
		switch (mimetype) {
			default:
			case RDFMediaType.SPARQL_RESULTS_JSON:
				return new SPARQLBooleanJSONWriterFactory();
			case RDFMediaType.SPARQL_RESULTS_XML:
				return new SPARQLBooleanXMLWriterFactory();
			case MediaType.TEXT_PLAIN:
				return new BooleanTextWriterFactory();
		}
	}
}
