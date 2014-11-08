
package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

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
