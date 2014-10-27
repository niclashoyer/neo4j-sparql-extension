
package de.unikiel.inf.comsys.neo4j.inference.rules.extractor;

import de.unikiel.inf.comsys.neo4j.inference.rules.Extractor;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

public abstract class AbstractExtractor implements Extractor {
	
	protected static String getString(OWLClassExpression clazz) {
		return ((OWLClass) clazz).getIRI().toURI().toASCIIString();
	}
	
	protected static String getString(OWLObjectPropertyExpression property) {
		return property.getNamedProperty().getIRI().toURI().toASCIIString();
	}
	
	protected static String getString(OWLDataPropertyExpression property) {
		return property.asOWLDataProperty().getIRI().toURI().toASCIIString();
	}
	
	protected static List<String> getStrings(List<OWLObjectPropertyExpression> properties) {
		List<String> strs = new ArrayList<>();
		for (OWLObjectPropertyExpression op : properties) {
			strs.add(getString(op));
		}
		return strs;
	}
	
}
