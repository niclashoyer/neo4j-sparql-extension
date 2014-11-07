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

/**
 * Implementation of the "query operation" part of the SPARQL 1.1 Protocol
 * standard.
 *
 * @see <a href="http://www.w3.org/TR/sparql11-protocol/#query-operation">
 * SPARQL 1.1 Protocol
 * </a>
 */
public class SPARQLQuery extends AbstractSailResource {

	private final List<Variant> queryResultVariants;
	private final List<Variant> booleanResultVariants;
	private final QueryRewriterFactory qwfactory;
	private final int timeout;

	/**
	 * Create a new SPARQL 1.1 query resource based on a repository.
	 *
	 * @param rep the repository this resources operates on
	 */
	public SPARQLQuery(SailRepository rep) {
		super(rep);
		// initialize additional result MIME-Types
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
		// get reference to query rewriting component
		this.qwfactory = QueryRewriterFactory.getInstance(rep);
		// get query timeout from properties
		String sout = SPARQLExtensionProps.getProperty("query.timeout");
		this.timeout = Integer.parseInt(sout);
	}

	/**
	 * Query via GET.
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-protocol/#query-operation">
	 * SPARQL 1.1 Protocol
	 * </a>
	 * @param req JAX-RS {@link Request} object
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param queryString the "query" query parameter
	 * @param defgraphs the "default-graph-uri" query parameter
	 * @param namedgraphs the "named-graph-uri" query parameter
	 * @param inference the "inference" query parameter
	 * @return the result of the SPARQL query
	 */
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

	/**
	 * Query via URL-encoded POST.
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-protocol/#query-operation">
	 * SPARQL 1.1 Protocol
	 * </a>
	 * @param req JAX-RS {@link Request} object
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param queryString the "query" form encoded parameter
	 * @param defgraphs the "default-graph-uri" form encoded parameter
	 * @param namedgraphs the "named-graph-uri" form encoded parameter
	 * @param inference the "inference" form encoded parameter
	 * @return the result of the SPARQL query
	 */
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

	/**
	 * Query via POST directly.
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-protocol/#query-operation">
	 * SPARQL 1.1 Protocol
	 * </a>
	 * @param req JAX-RS {@link Request} object
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param defgraphs the "default-graph-uri" form encoded parameter
	 * @param namedgraphs the "named-graph-uri" form encoded parameter
	 * @param inference the "inference" form encoded parameter
	 * @param queryString query as string (from HTTP request body)
	 * @return the result of the SPARQL query
	 */
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

	/**
	 * Query via GET (with inference).
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-protocol/#query-operation">
	 * SPARQL 1.1 Protocol
	 * </a>
	 * @param req JAX-RS {@link Request} object
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param queryString the "query" query parameter
	 * @param defgraphs the "default-graph-uri" query parameter
	 * @param namedgraphs the "named-graph-uri" query parameter
	 * @return the result of the SPARQL query
	 */
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

	/**
	 * Query via URL-encoded POST (with inference).
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-protocol/#query-operation">
	 * SPARQL 1.1 Protocol
	 * </a>
	 * @param req JAX-RS {@link Request} object
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param queryString the "query" form encoded parameter
	 * @param defgraphs the "default-graph-uri" form encoded parameter
	 * @param namedgraphs the "named-graph-uri" form encoded parameter
	 * @return the result of the SPARQL query
	 */
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

	/**
	 * Query via POST directly (with inference).
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-protocol/#query-operation">
	 * SPARQL 1.1 Protocol
	 * </a>
	 * @param req JAX-RS {@link Request} object
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param defgraphs the "default-graph-uri" form encoded parameter
	 * @param namedgraphs the "named-graph-uri" form encoded parameter
	 * @param queryString query as string (from HTTP request body)
	 * @return the result of the SPARQL query
	 */
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

	/**
	 * Implements the handling of a SPARQL query.
	 * 
	 * This method accepts the different parameters for SPARQL requests,
	 * executes the request (with optional inference) and returns the
	 * result as JAX-RS HTTP response. The response will be streamed, so large
	 * result sets are possible.
	 * 
	 * @see SPARQLTupleStreamingOutput
	 * @see SPARQLBooleanStreamingOutput
	 * @see SPARQLGraphStreamingOutput
	 * @param req JAX-RS {@link Request} object
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param queryString SPARQL query to execute
	 * @param defgraphs the "default-graph-uri" query parameter
	 * @param namedgraphs the "named-graph-uri" query parameter
	 * @param inference true, if the results should include inferred solutions
	 * @return the result of the SPARQL query
	 */
	private Response handleQuery(
			Request req,
			UriInfo uriInfo,
			String queryString,
			List<String> defgraphs,
			List<String> namedgraphs,
			String inference) {
		SailRepositoryConnection conn = null;
		try {
			// check for empty query
			if (queryString == null) {
				throw new MalformedQueryException("Missing query parameter");
			}
			conn = getConnection();
			final Query query;
			// check if the query should be rewritten for inference
			if (inference != null && inference.equals("true")) {
				// hand over to query rewriting component
				QueryRewriter qw = qwfactory.getRewriter(conn);
				query = qw.rewrite(
						QueryLanguage.SPARQL,
						queryString,
						uriInfo.getAbsolutePath().toASCIIString());
			} else {
				// direct preparation using Sesame repository
				query = conn.prepareQuery(
						QueryLanguage.SPARQL,
						queryString,
						uriInfo.getAbsolutePath().toASCIIString());
			}
			// limit query execution time
			query.setMaxQueryTime(timeout);
			// check query form and possible result variants
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
			// if acceptable variants does not match "Accept" header, abort
			if (variant == null) {
				return Response.notAcceptable(acceptable).build();
			}
			final MediaType mt = variant.getMediaType();
			final String mtstr = mt.getType() + "/" + mt.getSubtype();
			StreamingOutput stream;
			// select result writer based on query form and return streaming
			// output
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
			// syntax error
			close(conn, ex);
			String str = ex.getMessage();
			return Response.status(Response.Status.BAD_REQUEST).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (RepositoryException ex) {
			// server error
			close(conn, ex);
			throw new WebApplicationException(ex);
		}
	}

	/**
	 * Returns a {@link RDFWriterFactory} that produces RDF data according to a
	 * given MIME-type.
	 *
	 * @param mimetype the mimetype
	 * @return the corresponding writer factory
	 */
	private RDFWriterFactory getRDFWriterFactory(String mimetype) {
		RDFWriterRegistry registry = RDFWriterRegistry.getInstance();
		return registry.get(getRDFFormat(mimetype));
	}

	/**
	 * Returns a {@link TupleQueryResultWriterFactory} that returns a writer
	 * that writes SPARQL query results in the format of a given MIME-Type.
	 *
	 * @param mimetype the mimetype
	 * @return the corresponding query result writer factory
	 */
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

	/**
	 * Returns a {@link BooleanQueryResultWriterFactory} that returns a writer
	 * that writes SPARQL query results in the format of a given MIME-Type.
	 *
	 * @param mimetype the mimetype
	 * @return the corresponding query result writer factory
	 */
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
