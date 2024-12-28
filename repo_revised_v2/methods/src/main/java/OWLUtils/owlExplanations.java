package OWLUtils;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;

import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;
import openllet.owlapi.explanation.PelletExplanation;

public class owlExplanations {

	/**
     * Function that returns the explanations for an entailment.
     * 
     * @param numberOfExplanations
     * 
     * @return explanations
     */
    public static Set<Set<OWLAxiom>> explainEntailment(OWLOntology ontology, OWLAxiom entailment, int numberOfExplanations) {

        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontology);

        reasoner.precomputeInferences(InferenceType.values());

        PelletExplanation explanationGenerator = new PelletExplanation(reasoner);

        Set<Set<OWLAxiom>> explanations = explanationGenerator.getEntailmentExplanations(entailment);

        return explanations;
    }
    
    /**
     * Function that checks whether the axiom is entailed or not entailed.
     * 
     * @return true / false
     */
    public boolean isEntailed(OWLOntology ontology, OWLAxiom axiom) {

        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontology);

        reasoner.precomputeInferences(InferenceType.values());

        return reasoner.isEntailed(axiom);
    }
}
