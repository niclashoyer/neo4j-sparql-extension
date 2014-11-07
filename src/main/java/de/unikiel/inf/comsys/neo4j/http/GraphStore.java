package de.unikiel.inf.comsys.neo4j.http;

import de.unikiel.inf.comsys.neo4j.http.streams.ChunkedCommitHandler;
import de.unikiel.inf.comsys.neo4j.SPARQLExtensionProps;
import de.unikiel.inf.comsys.neo4j.http.streams.RDFStreamingOutput;
import de.unikiel.inf.comsys.neo4j.inference.QueryRewriterFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;

/**
 * Implementation of the SPARQL 1.1 Graph Store HTTP Protocol.
 *
 * @see <a href="http://www.w3.org/TR/sparql11-http-rdf-update/">
 * SPARQL 1.1 Graph Store HTTP Protocol
 * </a>
 */
public class GraphStore extends AbstractSailResource {

	private final ValueFactory vf;
	private final long chunksize;

	/**
	 * Create a new graph store management resource based on a repository.
	 *
	 * @param rep the repository this resources operates on
	 */
	public GraphStore(SailRepository rep) {
		super(rep);
		this.vf = rep.getValueFactory();
		String chunksizeStr = SPARQLExtensionProps.getProperty("chunksize");
		chunksize = Long.parseLong(chunksizeStr);
	}

	/**
	 * Indirect HTTP GET
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-http-rdf-update/#http-get">
	 * Section 5.2 "HTTP GET"
	 * </a>
	 * @param req JAX-RS {@link Request} object
	 * @param graphString the "graph" query parameter
	 * @param def the "default" query parameter
	 * @return the content of the request graph as HTTP response
	 */
	@GET
	@Produces({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_JSON
	})
	public Response graphIndirectGet(
			@Context Request req,
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def) {
		return handleGet(req, def, graphString);
	}

	/**
	 * Indirect HTTP PUT
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-http-rdf-update/#http-put">
	 * Section 5.3 "HTTP PUT"
	 * </a>
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param type Content-Type HTTP header field
	 * @param graphString the "graph" query parameter
	 * @param def the "default" query parameter
	 * @param chunked the "chunked" query parameter
	 * @param in HTTP body as {@link InputStream}
	 * @return "204 No Content", if operation was successful
	 */
	@PUT
	@Consumes({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_XML
	})
	public Response graphIndirectPut(
			@Context UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def,
			@QueryParam("chunked") String chunked,
			InputStream in) {
		return handleAdd(uriInfo, type, graphString, def, in, chunked, true);
	}

	/**
	 * Indirect HTTP DELETE
	 *
	 * @see <a
	 * href="http://www.w3.org/TR/sparql11-http-rdf-update/#http-delete">
	 * Section 5.4 "HTTP DELETE"
	 * </a>
	 * @param graphString the "graph" query parameter
	 * @param def the "default" query parameter
	 * @return "204 No Content", if operation was successful
	 */
	@DELETE
	public Response graphIndirectDelete(
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def) {
		return handleClear(graphString);
	}

	/**
	 * Indirect HTTP POST
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-http-rdf-update/#http-post">
	 * Section 5.5 "HTTP POST"
	 * </a>
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param type Content-Type HTTP header field
	 * @param graphString the "graph" query parameter
	 * @param def the "default" query parameter
	 * @param chunked the "chunked" query parameter
	 * @param in HTTP body as {@link InputStream}
	 * @return "204 No Content", if operation was successful
	 */
	@POST
	@Consumes({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_XML
	})
	public Response graphIndirectPost(
			@Context UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def,
			@QueryParam("chunked") String chunked,
			InputStream in) {
		return handleAdd(uriInfo, type, graphString, def, in, chunked, false);
	}

	/**
	 * Direct HTTP GET
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-http-rdf-update/#http-get">
	 * Section 5.2 "HTTP GET"
	 * </a>
	 * @param req JAX-RS {@link Request} object
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param graphString the "graph" query parameter
	 * @return the content of the request graph as HTTP response
	 */
	@GET
	@Produces({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_JSON
	})
	@Path("/{graph}")
	public Response graphDirectGet(
			@Context Request req,
			@Context UriInfo uriInfo,
			@PathParam("graph") String graphString) {
		String graphuri = uriInfo.getAbsolutePath().toASCIIString();
		return handleGet(req, null, graphuri);
	}

	/**
	 * Direct HTTP PUT
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-http-rdf-update/#http-put">
	 * Section 5.3 "HTTP PUT"
	 * </a>
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param type Content-Type HTTP header field
	 * @param chunked the "chunked" query parameter
	 * @param in HTTP body as {@link InputStream}
	 * @return "204 No Content", if operation was successful
	 */
	@PUT
	@Consumes({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_XML
	})
	@Path("/{graph}")
	public Response graphDirectPut(
			@Context UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			@QueryParam("chunked") String chunked,
			InputStream in) {
		String graphuri = uriInfo.getAbsolutePath().toASCIIString();
		return handleAdd(uriInfo, type, graphuri, null, in, chunked, true);
	}

	/**
	 * Direct HTTP DELETE
	 *
	 * @see <a
	 * href="http://www.w3.org/TR/sparql11-http-rdf-update/#http-delete">
	 * Section 5.4 "HTTP DELETE"
	 * </a>
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @return "204 No Content", if operation was successful
	 */
	@DELETE
	@Path("/{graph}")
	public Response graphDirectDelete(@Context UriInfo uriInfo) {
		String graphuri = uriInfo.getAbsolutePath().toASCIIString();
		return handleClear(graphuri);
	}

	/**
	 * Direct HTTP POST
	 *
	 * @see <a href="http://www.w3.org/TR/sparql11-http-rdf-update/#http-post">
	 * Section 5.5 "HTTP POST"
	 * </a>
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param type Content-Type HTTP header field
	 * @param chunked the "chunked" query parameter
	 * @param in HTTP body as {@link InputStream}
	 * @return "204 No Content", if operation was successful
	 */
	@POST
	@Consumes({
		RDFMediaType.RDF_TURTLE,
		RDFMediaType.RDF_XML,
		RDFMediaType.RDF_NTRIPLES,
		RDFMediaType.RDF_XML
	})
	@Path("/{graph}")
	public Response graphDirectPost(
			@Context UriInfo uriInfo,
			@HeaderParam("Content-Type") MediaType type,
			@QueryParam("chunked") String chunked,
			InputStream in) {
		String graphuri = uriInfo.getAbsolutePath().toASCIIString();
		return handleAdd(uriInfo, type, graphuri, null, in, chunked, false);
	}

	/**
	 * Adds RDF data to a graph in the repository.
	 *
	 * @param uriInfo JAX-RS {@link UriInfo} object
	 * @param type Content-Type HTTP header field
	 * @param graphString the "graph" query parameter
	 * @param def the "default" query parameter
	 * @param in RDF data
	 * @param chunkedStr the "chunked" query parameter
	 * @param clear true if the graph should be cleared before adding data
	 * @return "204 No Content", if operation was successful
	 */
	private Response handleAdd(
			UriInfo uriInfo,
			MediaType type,
			String graphString,
			String def,
			InputStream in,
			String chunkedStr,
			boolean clear) {
		SailRepositoryConnection conn;
		try {
			conn = getConnection();
		} catch (RepositoryException ex) {
			throw new WebApplicationException(ex);
		}
		try {
			boolean chunked = chunkedStr != null && chunkedStr.equals("true");
			Resource dctx = null;
			// get the base URI by using direct or indirect graph reference
			String base = uriInfo.getAbsolutePath().toASCIIString();
			if (graphString != null) {
				dctx = vf.createURI(graphString);
				base = dctx.stringValue();
			}
			// check if a Content-Type header was set
			if (type == null) {
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			String typestr = type.getType() + "/" + type.getSubtype();
			RDFFormat format = getRDFFormat(typestr);
			// begin import
			conn.begin();
			if (dctx != null) {
				if (clear) {
					conn.clear(dctx);
				}
				addToGraphstore(conn, in, base, format, dctx, chunked);
			} else {
				if (clear) {
					conn.clear();
				}
				addToGraphstore(conn, in, base, format, null, chunked);
			}
			conn.commit();
			// check for modifications of TBox-graph and notify the query
			// rewriting component
			if (dctx != null) {
				QueryRewriterFactory qr = QueryRewriterFactory.getInstance(rep);
				if (dctx.stringValue().equals(qr.getOntologyContext())) {
					qr.updateOntology(conn);
				}
			}
			close(conn);
			return Response.noContent().build();
		} catch (RDFParseException ex) {
			// rdf syntax error
			String str = ex.getMessage();
			close(conn, ex);
			return Response.status(400).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (IOException | RepositoryException | RDFHandlerException ex) {
			// server error
			close(conn, ex);
			throw new WebApplicationException(ex);
		}
	}

	/**
	 * Helper method for handleAdd.
	 */
	private void addToGraphstore(
			RepositoryConnection conn,
			InputStream in,
			String base,
			RDFFormat format,
			Resource dctx,
			boolean chunked) throws IOException, RDFParseException,
			RDFHandlerException, RepositoryException {
		if (chunked) {
			RDFParser parser = getRDFParser(format);
			parser.setRDFHandler(
					new ChunkedCommitHandler(conn, chunksize, dctx));
			parser.parse(in, base);
		} else {
			if (dctx != null) {
				conn.add(in, base, format, dctx);
			} else {
				conn.add(in, base, format);
			}
		}
	}

	/**
	 * Deletes all data from a graph in the repository.
	 *
	 * @param graphString the graph to delete
	 * @return "204 No Content", if operation was successful
	 */
	private Response handleClear(String graphString) {
		SailRepositoryConnection conn;
		try {
			conn = getConnection();
		} catch (RepositoryException ex) {
			throw new WebApplicationException(ex);
		}
		try {
			conn.begin();
			if (graphString != null) {
				Resource ctx = vf.createURI(graphString);
				conn.clear(ctx);
				// check if TBox graph has been cleared and notify query
				// rewriting component
				QueryRewriterFactory qr = QueryRewriterFactory.getInstance(rep);
				if (ctx.stringValue().equals(qr.getOntologyContext())) {
					qr.updateOntology(conn);
				}
			} else {
				conn.clear();
			}
			conn.commit();
			close(conn);
		} catch (RepositoryException ex) {
			// server error
			close(conn, ex);
			throw new WebApplicationException(ex);
		}
		return Response.noContent().build();
	}

	/**
	 * Returns RDF data from a graph in the repository.
	 *
	 * @see RDFStreamingOutput
	 * @param req JAX-RS {@link Request} object
	 * @param def the "default" query parameter
	 * @param graphString the "graph" query parameter
	 * @return RDF data as HTTP response
	 */
	private Response handleGet(
			Request req,
			String def,
			String graphString) {
		// select matching MIME-Type for response based on HTTP headers
		final Variant variant = req.selectVariant(rdfResultVariants);
		final MediaType mt = variant.getMediaType();
		final String mtstr = mt.getType() + "/" + mt.getSubtype();
		final RDFFormat format = getRDFFormat(mtstr);
		StreamingOutput stream;
		RepositoryConnection conn = null;
		try {
			// return data as RDF stream
			conn = getConnection();
			if (graphString != null) {
				Resource ctx = vf.createURI(graphString);
				if (conn.size(ctx) == 0) {
					return Response.status(Response.Status.NOT_FOUND).build();
				}
				stream = new RDFStreamingOutput(conn, format, ctx);
			} else {
				stream = new RDFStreamingOutput(conn, format);
			}
		} catch (RepositoryException ex) {
			// server error
			close(conn, ex);
			throw new WebApplicationException(ex);
		}
		return Response.ok(stream).build();
	}

}
