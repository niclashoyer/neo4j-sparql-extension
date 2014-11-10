
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
import de.unikiel.inf.comsys.neo4j.inference.rules.PredicateVariable;
import de.unikiel.inf.comsys.neo4j.inference.rules.SubPropertyOf;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.vocabulary.OWL;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * {@link Extractor} implementation that extracts
 * {@link PredicateVariableExtractor} rules.
 */
public class PredicateVariableExtractor extends AbstractExtractor {
	
	private static final String TOPOBJ = OWL.NAMESPACE + "topObjectProperty";
	private static final String TOPDATA = OWL.NAMESPACE + "topDataProperty";
	
	/**
	 * Extracts {@link PredicateVariableExtractor} rules.
	 * 
	 * @param ot ontology
	 * @return extracted rules
	 */
	@Override
	public List<Rule> extract(OWLOntology ot) {
		List<Rule> list = new ArrayList<>();
		// list of predicates in ontology
		List<String> ps = new ArrayList<>();
		ps.add(TOPOBJ);
		ps.add(TOPDATA);
		OWLEntity e;
		String op;
		// check all declarations
		for (OWLDeclarationAxiom a : ot.getAxioms(AxiomType.DECLARATION)) {
			e = a.getEntity();
			if (e.isOWLObjectProperty()) {
				// if it is a object property declaration, add it to the list
				// and also add it as subproperty of owl:topObjectProperty
				op = getString(e.asOWLObjectProperty());
				ps.add(op);
				list.add(new SubPropertyOf(op, TOPOBJ));
			} else if (e.isOWLDataProperty()) {
				// if it is a data property declaration, add it to the list
				// and also add it as subproperty of owl:topDataProperty
				op = getString(e.asOWLDataProperty());
				ps.add(op);
				list.add(new SubPropertyOf(op, TOPDATA));
			}
		}
		list.add(new PredicateVariable(ps));
		return list;
	}
	
}
