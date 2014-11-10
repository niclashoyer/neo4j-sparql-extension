
package de.unikiel.inf.comsys.neo4j.inference.debug;

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
