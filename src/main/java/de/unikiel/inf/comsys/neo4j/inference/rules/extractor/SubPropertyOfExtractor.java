
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
import de.unikiel.inf.comsys.neo4j.inference.rules.SubPropertyOf;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

/**
 * {@link Extractor} implementation that extracts
 * {@link SubPropertyOf} rules.
 */
public class SubPropertyOfExtractor extends AbstractExtractor {

	private void addAxiom(List<Rule> list, OWLSubDataPropertyOfAxiom a) {
		String op1 = getString(a.getSubProperty());
		String op2 = getString(a.getSuperProperty());
		list.add(new SubPropertyOf(op1, op2));
	}
	
	private void addAxiom(List<Rule> list, OWLSubObjectPropertyOfAxiom a) {
		if (!(a.getSubProperty().isAnonymous() ||
		      a.getSuperProperty().isAnonymous())) {
			String op1 = getString(a.getSubProperty());
			String op2 = getString(a.getSuperProperty());
			list.add(new SubPropertyOf(op1, op2));
		}
	}
	
	private void addObjectAxioms(List<Rule> list,
			Set<OWLSubObjectPropertyOfAxiom> axs) {
		for (OWLSubObjectPropertyOfAxiom a : axs) {
			addAxiom(list, a);
		}
	}
	
	private void addDataAxioms(List<Rule> list,
			Set<OWLSubDataPropertyOfAxiom> axs) {
		for (OWLSubDataPropertyOfAxiom a : axs) {
			addAxiom(list, a);
		}
	}
	
	/**
     * Extracts {@link SubPropertyOf} rules.
	 * 
	 * @param ot ontology
	 * @return extracted rules
	 */
	@Override
	public List<Rule> extract(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		// direct mapping of SubObjectPropertyOf axioms
		addObjectAxioms(list, ot.getAxioms(AxiomType.SUB_OBJECT_PROPERTY));
		// direct mapping of SubDataPropertyOf axioms
		addDataAxioms(list, ot.getAxioms(AxiomType.SUB_DATA_PROPERTY));
		// indirect mapping of shortcut axioms
		for (OWLEquivalentObjectPropertiesAxiom a :
				ot.getAxioms(AxiomType.EQUIVALENT_OBJECT_PROPERTIES)) {
			addObjectAxioms(list, a.asSubObjectPropertyOfAxioms());
		}
		for (OWLEquivalentDataPropertiesAxiom a :
				ot.getAxioms(AxiomType.EQUIVALENT_DATA_PROPERTIES)) {
			addDataAxioms(list, a.asSubDataPropertyOfAxioms());
		}
		return list;
	}
	
}
