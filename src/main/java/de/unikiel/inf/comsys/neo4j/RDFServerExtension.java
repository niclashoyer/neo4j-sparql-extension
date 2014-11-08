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

import de.unikiel.inf.comsys.neo4j.http.GraphStore;
import de.unikiel.inf.comsys.neo4j.http.SPARQLQuery;
import de.unikiel.inf.comsys.neo4j.http.SPARQLUpdate;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.SailException;

/**
 * JAX-RS class as entry point for the Neo4j-Plugin.
 */
@Path("/")
public class RDFServerExtension {

	private final SPARQLQuery query;
	private final SPARQLUpdate update;
	private final GraphStore graphStore;

	/**
	 * The constructor is called by Neo4j with a
	 * {@link GraphDatabaseService} object.
	 * The constructor creates a new repository based on the given
	 * database and initializes it.
	 *
	 * @param database
	 * @throws SailException
	 * @throws RepositoryException
	 */
	public RDFServerExtension(@Context GraphDatabaseService database)
			throws SailException, RepositoryException {
		SailRepository rep = RepositoryRegistry
				.getInstance(database)
				.getRepository();
		query = new SPARQLQuery(rep);
		update = new SPARQLUpdate(rep);
		graphStore = new GraphStore(rep);
	}

	/**
	 * Entry point for the implementation of SPARQL 1.1 query features.
	 *
	 * @return an instance of the implementation, see {@link SPARQLQuery}
	 */
	@Path("/query")
	public SPARQLQuery query() {
		return query;
	}

	/**
	 * Entry point for the implementation of SPARQL 1.1 update features.
	 *
	 * @return an instance of the implementation, see {@link SPARQLUpdate}
	 */
	@Path("/update")
	public SPARQLUpdate update() {
		return update;
	}

	/**
	 * Entry point for the implementation of SPARQL 1.1 Graph Store Protocol
	 * features.
	 *
	 * @return an instance of the implementation, see {@link GraphStore}
	 */
	@Path("/graph")
	public GraphStore graph() {
		return graphStore;
	}

}
