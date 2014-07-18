
package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.SubClassOf;
import de.unikiel.inf.comsys.neo4j.inference.rules.SubObjectPropertyOf;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;

public class SubObjectPropertyOfExtractor extends AbstractExtractor {
	
	@Override
	public List<Rule> extract(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		for (OWLSubObjectPropertyOfAxiom a :
				ot.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) {
			String op1 = getString(a.getSubProperty());
			String op2 = getString(a.getSuperProperty());
			list.add(new SubObjectPropertyOf(op1, op2));
		}
		return list;
	}
	
}
