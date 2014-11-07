
package de.unikiel.inf.comsys.neo4j.inference.debug;

import org.openrdf.query.algebra.QueryModelNode;

/**
 * A utility class that has static methods for traversing in a tree of
 * query nodes.
 */
public class QueryModelNodeTree {
	
	private QueryModelNodeTree() {
	}
	
	/**
	 * Returns the root of a node in a query model tree.
	 * 
	 * @param node the node to traverse up from
	 * @return the root of the tree
	 */
	public static QueryModelNode getRoot(QueryModelNode node) {
		QueryModelNode parent;
		parent = node;
		while(parent.getParentNode() != null) {
			parent = parent.getParentNode();
		}
		return parent;
	}
	
	/**
	 * Returns the distance to the root for a node in a query model tree.
	 * 
	 * @param node the node to traverse up from
	 * @return the distance to the root node
	 */
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
