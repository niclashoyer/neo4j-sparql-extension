package de.unikiel.inf.comsys.neo4j;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;
import de.unikiel.inf.comsys.neo4j.http.GraphStore;
import de.unikiel.inf.comsys.neo4j.http.SPARQLQuery;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParserRegistry;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.rio.ntriples.NTriplesParserFactory;
import org.openrdf.rio.ntriples.NTriplesWriterFactory;
import org.openrdf.rio.rdfjson.RDFJSONParserFactory;
import org.openrdf.rio.rdfjson.RDFJSONWriterFactory;
import org.openrdf.rio.rdfxml.RDFXMLParserFactory;
import org.openrdf.rio.rdfxml.RDFXMLWriterFactory;
import org.openrdf.rio.turtle.TurtleParserFactory;
import org.openrdf.rio.turtle.TurtleWriterFactory;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;

@Path("/")
public class RDFServerExtension {
	
    private final GraphDatabaseService database;
	private final SPARQLQuery query;
	private final GraphStore graphStore;

    public RDFServerExtension(@Context GraphDatabaseService database) throws SailException, RepositoryException {
		initRio();
        this.database = database;
		Graph graph = new Neo4j2Graph(database);
		Sail sail = new GraphSail((KeyIndexableGraph) graph);
		Repository rep = new SailRepository(sail);
		rep.initialize();
		RepositoryConnection conn = rep.getConnection();
		query = new SPARQLQuery(conn);
		graphStore = new GraphStore(conn);
    }
	
	/**
	 * This is needed, because Rio is unable to find the Parser/Writer
	 * Factories automatically when the jar gets deployed as plugin
	 * inside the Neo4j Server.
	 */
	private void initRio() {
		RDFParserRegistry parserRegistry = RDFParserRegistry.getInstance();
		parserRegistry.add(new TurtleParserFactory());
		parserRegistry.add(new RDFXMLParserFactory());
		parserRegistry.add(new NTriplesParserFactory());
		parserRegistry.add(new RDFJSONParserFactory());
		RDFWriterRegistry writerRegistry = RDFWriterRegistry.getInstance();
		writerRegistry.add(new TurtleWriterFactory());
		writerRegistry.add(new RDFXMLWriterFactory());
		writerRegistry.add(new NTriplesWriterFactory());
		writerRegistry.add(new RDFJSONWriterFactory());
	}

	@Path("/graph")
	public GraphStore graph() {
			return graphStore;
	}
	
    @Path("/query")
    public SPARQLQuery query() {
		return query;
    }
	
}
