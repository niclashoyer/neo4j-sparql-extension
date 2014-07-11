
package de.unikiel.inf.comsys.neo4j.inference;

import de.unikiel.inf.comsys.neo4j.inference.rules.Extractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.InverseObjectPropertiesExtractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.ObjectPropertyChainExtractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.PredicateVariableExtractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.SubClassOfExtractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.SymmetricPropertyExtractor;
import de.unikiel.inf.comsys.neo4j.inference.rules.extractor.TransitiveObjectPropertyExtractor;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Rules {
	
	private Rules() {
	}
	
	public static List<Rule> fromOntology(OWLOntologyDocumentSource src) {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			System.out.println("[RULES] Loading ontology...");
			manager.loadOntologyFromOntologyDocument(src);
			Set<OWLOntology> ontologies = manager.getOntologies();
			System.out.println("[RULES] Ontologies loaded: " + ontologies.size());
			if (ontologies.isEmpty()) {
				return Collections.EMPTY_LIST;
			} else {
				return fromOntology(ontologies.iterator().next());
			}
		} catch (OWLOntologyCreationException ex) {
			throw new IllegalArgumentException(
					"Loading ontology stream failed", ex);
		}
	}
	
	public static List<Rule> fromOntology(InputStream in) {
		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			manager.loadOntologyFromOntologyDocument(in);
			Set<OWLOntology> ontologies = manager.getOntologies();
			if (ontologies.isEmpty()) {
				return Collections.EMPTY_LIST;
			} else {
				return fromOntology(ontologies.iterator().next());
			}
		} catch (OWLOntologyCreationException ex) {
			throw new IllegalArgumentException(
					"Loading ontology stream failed", ex);
		}
	}
	
	public static List<Rule> fromOntology(OWLOntology ot) {
		ArrayList<Rule> list = new ArrayList<>();
		List<Extractor> extractors = new ArrayList<>();
		extractors.add(new InverseObjectPropertiesExtractor());
		extractors.add(new ObjectPropertyChainExtractor());
		extractors.add(new PredicateVariableExtractor());
		extractors.add(new SubClassOfExtractor());
		extractors.add(new SymmetricPropertyExtractor());
		extractors.add(new TransitiveObjectPropertyExtractor());
		System.out.println("== AXIOMS ==");
		for (OWLAxiom a : ot.getAxioms()) {
			System.out.println(a);
		}
		for (Extractor extr : extractors) {
			list.addAll(extr.extract(ot));
		}
		System.out.println("== RULES ==");
		for (Rule r : list) {
			System.out.println(r);
		}
		return list;
	}
	
}
