package de.unikiel.inf.comsys.neo4j;

/*
 * #%L
 * neo4j-sparql-extension
 * %%
 * Copyright (C) 2014 Niclas Hoyer
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.neo4j2.Neo4j2Graph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;
import java.util.WeakHashMap;
import org.neo4j.graphdb.GraphDatabaseService;
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

/**
 * Sesame repository instance management.
 * 
 * This registry ensures that for each
 * {@link GraphDatabaseService} object there is only one
 * {@link Neo4j2Graph}, {@link GraphSail} and {@link SailRepository} instance.
 */
public class RepositoryRegistry {

	private static final WeakHashMap<GraphDatabaseService, RepositoryRegistry>
			map = new WeakHashMap<>();
	private static boolean rioInitialized = false;
	private final SailRepository rep;

	/**
	 * Initializes Sesame repository for Neo4j based on Blueprints
	 * implementation.
	 *
	 * @param database Neo4j database service
	 * @throws RepositoryException if there was a problem initializing the
	 * Sesame repository
	 */
	private RepositoryRegistry(GraphDatabaseService database)
			throws RepositoryException {
		initRio();
		Graph graph = new Neo4j2Graph(database);
		String patterns = SPARQLExtensionProps.getProperty("query.patterns");
		Sail sail = new GraphSail((KeyIndexableGraph) graph, patterns);
		this.rep = new SailRepository(sail);
		rep.initialize();
	}

	/**
	 * Returns a new registry for a Neo4j database service. This class uses a
	 * singleton pattern and thus at most one registry is created per database
	 * service.
	 *
	 * @param database
	 * @return a repository registry
	 * @throws RepositoryException if there was a problem while initializing the
	 * repository
	 */
	public static RepositoryRegistry getInstance(
			GraphDatabaseService database) throws RepositoryException {
		RepositoryRegistry inst;
		if (!map.containsKey(database)) {
			synchronized (RepositoryRegistry.class) {
				if (!map.containsKey(database)) {
					inst = new RepositoryRegistry(database);
					map.put(database, inst);
				}
			}
		}
		return map.get(database);
	}

	/**
	 * Returns the repository associated with this registry.
	 *
	 * @return the repository
	 */
	public SailRepository getRepository() {
		return rep;
	}

	/**
	 * This is needed, because Rio is unable to find the Parser/Writer Factories
	 * automatically when the jar gets deployed as plugin inside the Neo4j
	 * Server.
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
