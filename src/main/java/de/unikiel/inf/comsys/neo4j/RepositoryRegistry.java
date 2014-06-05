package de.unikiel.inf.comsys.neo4j;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;
import java.util.WeakHashMap;
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
	
	private static final WeakHashMap<GraphDatabaseService, RepositoryRegistry>
		map = new WeakHashMap<>();
	private static boolean rioInitialized = false;
	private final Repository rep;
 
    private RepositoryRegistry(GraphDatabaseService database)
			throws RepositoryException {
		initRio();
		Graph graph = new Neo4j2Graph(database);
		Sail sail = new GraphSail((KeyIndexableGraph) graph);
		this.rep = new SailRepository(sail);
		rep.initialize();
	}
 
    public static synchronized RepositoryRegistry getInstance(
		GraphDatabaseService database) throws RepositoryException {
		RepositoryRegistry inst;
        if (!map.containsKey(database)) {
			inst = new RepositoryRegistry(database);
			map.put(database, inst);
        } else {
			inst = map.get(database);
		}
        return inst;
    }
	
	public Repository getRepository() {
		return rep;
	}
	
	/**
	 * This is needed, because Rio is unable to find the Parser/Writer
	 * Factories automatically when the jar gets deployed as plugin
	 * inside the Neo4j Server.
	 */
	private synchronized void initRio() {
		if (!rioInitialized) {
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
			rioInitialized = true;
		}
	}
}
