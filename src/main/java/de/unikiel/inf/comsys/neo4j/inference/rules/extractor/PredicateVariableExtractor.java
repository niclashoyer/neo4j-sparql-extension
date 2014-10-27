
package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.Rule;
import de.unikiel.inf.comsys.neo4j.inference.rules.PredicateVariable;
import de.unikiel.inf.comsys.neo4j.inference.rules.SubPropertyOf;
import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.vocabulary.OWL;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

public class PredicateVariableExtractor extends AbstractExtractor {
	
	private static final String OWLTOPPROP = OWL.NAMESPACE + "topObjectProperty";
	
	@Override
	public List<Rule> extract(OWLOntology ot) {
		List<Rule> list = new ArrayList<>();
		List<String> ps = new ArrayList<>();
		ps.add(OWLTOPPROP);
		OWLEntity e;
		String op;
		for (OWLDeclarationAxiom a : ot.getAxioms(AxiomType.DECLARATION)) {
			e = a.getEntity();
			if (e.isOWLObjectProperty()) {
				op = getString(e.asOWLObjectProperty());
				ps.add(op);
				list.add(new SubPropertyOf(op, OWLTOPPROP));
			}
		}
		list.add(new PredicateVariable(ps));
		return list;
	}
	
}
