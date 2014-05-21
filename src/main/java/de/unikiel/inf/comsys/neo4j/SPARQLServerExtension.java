package de.unikiel.inf.comsys.neo4j;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;
import info.aduna.iteration.CloseableIteration;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openrdf.model.Resource;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.impl.EmptyBindingSet;
import org.openrdf.query.parser.ParsedQuery;
import org.openrdf.query.parser.sparql.SPARQLParser;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.turtle.TurtleParser;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

@Path( "/" )
public class SPARQLServerExtension {
	
    private final GraphDatabaseService database;
	private final Sail sail;
	private final Graph graph;
	private final SailConnection sc;
	private final ValueFactory vf;

    public SPARQLServerExtension(@Context GraphDatabaseService database) throws SailException {
        this.database = database;
		graph = new Neo4j2Graph(database);
		sail = new GraphSail((KeyIndexableGraph) graph);
		sail.initialize();
		sc = sail.getConnection();
		vf = sail.getValueFactory();
    }

	@PUT
	@Consumes("text/turtle")
	@Path("/graph")
	public Response graph(
			@QueryParam("graph") String graphString,
			@QueryParam("default") String def,
			InputStream in) {
		try {
			RDFParser p = new TurtleParser();
			Resource dctx = null;
			String base = "http://example.com"; // FIXME
			if (graphString != null) {
				dctx = vf.createURI(graphString);
				base = dctx.stringValue();
			}
			p.setRDFHandler(new SailsRDFHandler(sc, dctx));
			p.parse(in, base);
			String str = graphString + "\n" + def;
			return Response.status(Status.OK).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (Exception ex) {
			// DEBUG
			String str = ex.toString();
			return Response.status(500).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		}
	}
	
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/query")
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
			return Response.status(Status.OK).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		} catch (Exception ex) {
			// DEBUG
			String str = ex.toString();
			return Response.status(500).entity(
					str.getBytes(Charset.forName("UTF-8"))).build();
		}
    }
}
