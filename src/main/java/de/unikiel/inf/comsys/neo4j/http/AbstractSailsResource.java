
package de.unikiel.inf.comsys.neo4j.http;

import org.openrdf.model.ValueFactory;
import org.openrdf.sail.SailConnection;

public class AbstractSailsResource {
	protected final SailConnection sc;
	protected final ValueFactory vf;
	
	protected static class Status {
		public static int NOT_IMPLEMENTED = 501;
	}
	
	public AbstractSailsResource(SailConnection sc, ValueFactory vf) {
		this.sc = sc;
		this.vf = vf;
	}
}
