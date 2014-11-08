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

import javax.ws.rs.core.StreamingOutput;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Abstract super class for {@link StreamingOutput} implementations, that
 * contains a reference to a {@link RepositoryConnection} and helper methods
 * for connection handling.
 */
public abstract class AbstractStreamingOutput implements StreamingOutput {

	protected RepositoryConnection conn;

	/**
	 * Set reference to repository connection.
	 * @param conn the connection
	 */
	public AbstractStreamingOutput(RepositoryConnection conn) {
		this.conn = conn;
	}

	/**
	 * Rolls back a transaction, if there is a active transaction
	 * using a connection.
	 * 
	 * This method is intended to roll back a transaction after an exception
	 * occured. Any occuring exceptions while rolling back will be added as
	 * suppressed exceptions to the given exception.
	 * @param conn the connection on which the transaction should be rolled back
	 * @param ex an exception that caused the rollback
	 */
	protected void rollback(RepositoryConnection conn, Exception ex) {
		try {
			if (conn.isActive()) {
				conn.rollback();
			}
		} catch (RepositoryException ex2) {
			ex.addSuppressed(ex2);
		}
	}

	/**
	 * Closes a connection if it is open.
	 * 
	 * This method is intended to close a connection after an exception
	 * occured. Any occuring exceptions while closing will be added as
	 * suppressed exceptions to the given exception.
	 * @param conn the connection that should be closed
	 * @param ex an exception that caused the closing
	 */
	protected void close(RepositoryConnection conn, Exception ex) {
		if (conn != null) {
			try {
				if (conn.isOpen()) {
					conn.close();
				}
				Repository rep = conn.getRepository();
			} catch (RepositoryException ex2) {
				ex.addSuppressed(ex2);
			}
		}
	}

}
