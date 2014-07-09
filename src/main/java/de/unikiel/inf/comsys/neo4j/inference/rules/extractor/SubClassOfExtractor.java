
package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.SubClassOf;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class SubClassOfExtractor extends AbstractExtractor {
	
	@Override
	public List<Rule> extract(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		for (OWLSubClassOfAxiom a :
				ot.getAxioms(AxiomType.SUBCLASS_OF)) {
			String ce1 = getString(a.getSubClass());
			String ce2 = getString(a.getSuperClass());
			list.add(new SubClassOf(ce1, ce2));
		}
		return list;
	}
	
}
