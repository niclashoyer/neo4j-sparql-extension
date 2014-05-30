
package de.unikiel.inf.comsys.neo4j.http.streams;

import javax.ws.rs.core.StreamingOutput;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public abstract class AbstractStreamingOutput implements StreamingOutput {
	
	protected RepositoryConnection conn;
	
	public AbstractStreamingOutput(RepositoryConnection conn) {
		this.conn = conn;
	}
	
	protected void rollback(RepositoryConnection conn, Exception ex) {
		try {
			if (conn.isActive()) {
				conn.rollback();
			}
		} catch (RepositoryException ex2) {
			ex.addSuppressed(ex2);
		}
	}
	
	protected void close(RepositoryConnection conn, Exception ex) {
		if (conn != null) {
			try {
				if (conn.isOpen()) {
					conn.close();
				}
				Repository rep = conn.getRepository();
				if (rep.isInitialized()) {
					rep.shutDown();
				}
			} catch (RepositoryException ex2) {
				ex.addSuppressed(ex2);
			}
		}
	}
	
}
