
package de.unikiel.inf.comsys.neo4j.inference.debug;

import org.openrdf.query.algebra.QueryModelNode;

public class QueryModelNodeTree {
	
	private QueryModelNodeTree() {
	}
	
	public static QueryModelNode getRoot(QueryModelNode node) {
		QueryModelNode parent;
		parent = node;
		while(parent.getParentNode() != null) {
			parent = parent.getParentNode();
		}
		return parent;
	}
	
	public static int getRootDistance(QueryModelNode node) {
		int i = 0;
		QueryModelNode parent;
		parent = node;
		while(parent.getParentNode() != null) {
			parent = parent.getParentNode();
			i++;
		}
		return i;
	}
	
}
