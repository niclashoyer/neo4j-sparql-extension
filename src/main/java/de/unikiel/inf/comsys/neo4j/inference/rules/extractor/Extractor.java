package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

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

import de.unikiel.inf.comsys.neo4j.inference.rules.Rule;
import java.util.List;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * An extractor extracts rules from an OWL-2 ontology.
 *
 * Each extractor should extract a specific rule from an ontology.
 */
public interface Extractor {

	/**
	 * Extract rules from an ontology.
	 *
	 * @param ot the ontology to extact from
	 * @return list of rules
	 */
	public List<Rule> extract(OWLOntology ot);

}
