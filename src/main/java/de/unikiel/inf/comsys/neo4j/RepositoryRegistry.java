package de.unikiel.inf.comsys.neo4j;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;
import de.unikiel.inf.comsys.neo4j.http.GraphStore;
import de.unikiel.inf.comsys.neo4j.http.SPARQLQuery;
import de.unikiel.inf.comsys.neo4j.http.SPARQLUpdate;
import javax.ws.rs.core.Context;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openrdf.repository.Repository;
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

public class RepositoryRegistry {
	
    private static RepositoryRegistry INSTANCE = null;
	private final Repository rep;
 
    private RepositoryRegistry(GraphDatabaseService database) throws RepositoryException {
		initRio();
		Graph graph = new Neo4j2Graph(database);
		Sail sail = new GraphSail((KeyIndexableGraph) graph);
		this.rep = new SailRepository(sail);
		System.out.println("[NEW] New repository initialized");
		rep.initialize();
	}
 
    public static synchronized RepositoryRegistry getInstance(
		GraphDatabaseService database) throws RepositoryException {
        if (INSTANCE == null) {
            INSTANCE = new RepositoryRegistry(database);
        }
        return INSTANCE;
    }
	
	public Repository getRepository() {
		return rep;
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
}
