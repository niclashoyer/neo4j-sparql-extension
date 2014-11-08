package de.unikiel.inf.comsys.neo4j.http.streams;

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
