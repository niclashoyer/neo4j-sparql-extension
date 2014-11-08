
package de.unikiel.inf.comsys.neo4j.inference.rules;

import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.Extractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.InverseObjectPropertiesExtractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.ObjectPropertyChainExtractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.PredicateVariableExtractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.SubClassOfExtractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.SubPropertyOfExtractor;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * A class containing static methods to get a list of rules from an
 * OWL-2 ontology.
 */
public class Rules {
	
	private Rules() {
	}
	
	/**
	 * Returns a list of rules extracted from the given OWL-2 ontology document.
	 * 
	 * @param src an ontology document
	 * @return a list of rules
	 */
	public static List<Rule> fromOntology(OWLOntologyDocumentSource src) {
		try {
			// use OWL-API to get a OWLOntology document from source
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			manager.loadOntologyFromOntologyDocument(src);
			Set<OWLOntology> ontologies = manager.getOntologies();
			if (ontologies.isEmpty()) {
				return Collections.EMPTY_LIST;
			} else {
				// use first ontology from given source
				return fromOntology(ontologies.iterator().next());
			}
		} catch (OWLOntologyCreationException ex) {
			throw new IllegalArgumentException(
					"Loading ontology stream failed", ex);
		}
	}
	
	/**
	 * Returns a list of rules extracted from the given OWL-2 ontology document.
	 * @param in an ontology document as stream
	 * @return a list of rules
	 */
	public static List<Rule> fromOntology(InputStream in) {
		try {
			// use OWL-API to get a OWLOntology document from source
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			manager.loadOntologyFromOntologyDocument(in);
			Set<OWLOntology> ontologies = manager.getOntologies();
			if (ontologies.isEmpty()) {
				return Collections.EMPTY_LIST;
			} else {
				// use first ontology from given source
				return fromOntology(ontologies.iterator().next());
			}
		} catch (OWLOntologyCreationException ex) {
			throw new IllegalArgumentException(
					"Loading ontology stream failed", ex);
		}
	}
	
	/**
	 * Returns a list of rules extracted from the given OWL-2 ontology document.
	 * @param ot an owl ontology object
	 * @return list of rules
	 */
	public static List<Rule> fromOntology(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		List<Extractor> extractors = new ArrayList<>();
		// create a list of extractors that will extract rules from the
		// given ontology
		extractors.add(new InverseObjectPropertiesExtractor());
		extractors.add(new ObjectPropertyChainExtractor());
		extractors.add(new PredicateVariableExtractor());
		extractors.add(new SubClassOfExtractor());
		extractors.add(new SubPropertyOfExtractor());
		// call each extractor and accumulate rules
		for (Extractor extr : extractors) {
			list.addAll(extr.extract(ot));
		}
		return list;
	}
	
}
