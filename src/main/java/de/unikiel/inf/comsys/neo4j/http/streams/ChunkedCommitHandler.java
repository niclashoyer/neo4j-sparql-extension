
package de.unikiel.inf.comsys.neo4j.http.streams;

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

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

/**
 * A {@link RDFHandler} implementation that accepts {@link Statement}s and
 * commits them in chunks to a repository.
 */
public class ChunkedCommitHandler implements RDFHandler {
	
	private final RepositoryConnection conn;
	private final long size;
	private final Resource dctx;
	private long count;

	/**
	 * Create a new chunked commit handler with given chunk size that
	 * commits to the given graph and connection.
	 * 
	 * @param conn the connection to commit to
	 * @param size the chunk size
	 * @param dctx the graph in that the triples should be stored
	 */
	public ChunkedCommitHandler(
			RepositoryConnection conn, long size, Resource dctx) {
		this.conn  = conn;
		this.size  = size;
		this.dctx  = dctx;
		this.count = 0;
	}

	/**
	 * Marks the beginning of RDF data.
	 * 
	 * @throws RDFHandlerException
	 */
	@Override
	public void startRDF() throws RDFHandlerException {
		this.count = 0;
	}

	/**
	 * Marks the end of RDF data.
	 * 
	 * @throws RDFHandlerException 
	 */
	@Override
	public void endRDF() throws RDFHandlerException {
	}

	/**
	 * Handles a namespace (ignored).
	 * @param string
	 * @param string1
	 * @throws org.openrdf.rio.RDFHandlerException
	 */
	@Override
	public void handleNamespace(String string, String string1) throws RDFHandlerException {
	}

	/**
	 * Handles a statement.
	 * 
	 * The statements will be added up until chunk size is reached.
	 * After a chunk of statements is added the transaction will be committed
	 * and new transaction will be started.
	 * @param stmnt
	 * @throws RDFHandlerException 
	 */
	@Override
	public void handleStatement(Statement stmnt) throws RDFHandlerException {
		try {
			// check if triple should be added to a specific graph
			if (dctx != null) {
				conn.add(stmnt, dctx);
			} else {
				conn.add(stmnt);
			}
			// check if chunk size is reached and transaction should be
			// committed
			count++;
			if (count >= size) {
				count = 0;
				conn.commit();
				conn.begin();
			}
		} catch (RepositoryException ex) {
			throw new RDFHandlerException(ex);
		}
	}

	/**
	 * Handles a comment (ignored).
	 * @param string the comment
	 * @throws RDFHandlerException 
	 */
	@Override
	public void handleComment(String string) throws RDFHandlerException {
	}
	
}
