
package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
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
	
	@Override
	public List<Rule> extract(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		addObjectAxioms(list, ot.getAxioms(AxiomType.SUB_OBJECT_PROPERTY));
		addDataAxioms(list, ot.getAxioms(AxiomType.SUB_DATA_PROPERTY));
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
