package OWLUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import Utils.Functions;
import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;

public class owlFunctions {
	
	/**
	 * Function that returns all axioms the are in an ontology.
	 * 
	 * @param ontology
	 * @return
	 */
	public static Set<OWLAxiom> getAllAxiomsFromOntology(OWLOntology ontology) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>(ontology.getAxioms());
		
		return axioms;
	}
	
	/**
	 * Function that creates an OWL class.
	 * The class URI is constructed as: ontologyIRI + className.
	 * 
	 * @param ontology
	 * @param className
	 * @return
	 */
	public static OWLClass createClass(OWLOntology ontology, OWLDataFactory dataFactory, String className) {
		IRI ontologyIRI = owlUtils.getOntologyIRI(ontology);
		
		OWLClass owlClass = dataFactory.getOWLClass(IRI.create(ontologyIRI.toString() + "#" + className));
		
		return owlClass;
	}
	
	/**
	 * Function that returns a specific class from an ontology which has a specific prefix. 
	 * 
	 * @param dataFactory
	 * @param ontologyClassName
	 * @return
	 */
	public static OWLClass getClassFromOntology(OWLDataFactory dataFactory, String prefix, String ontologyClassName) {
		return dataFactory.getOWLClass(IRI.create(prefix + ontologyClassName)); 
	}
	
	/**
	 * Function that returns all assertive equivalent classes / class expressions to the provided ontologyClass.
	 * 
	 * @param ontology
	 * @param ontologyClass
	 * @return
	 */
	public static Set<OWLEquivalentClassesAxiom> getEquivalentClassesFromOntology(OWLOntology ontology, OWLClass ontologyClass) {
		return ontology.getEquivalentClassesAxioms(ontologyClass); 
	}
	
	/**
	 * Function that creates a conjunction / intersection between OWL classes.
	 * 
	 * @param ontology
	 * @param dataFactory
	 * @param classes
	 * @return
	 */
	public static OWLObjectIntersectionOf createConjunctionOfConcepts(OWLOntology ontology, OWLDataFactory dataFactory, List<OWLClass> classes) {
		OWLObjectIntersectionOf conjunction = dataFactory.getOWLObjectIntersectionOf(classes);
		
		return conjunction;
	}
	
	/**
	 * Function that returns the set of direct SuperClasses Of a provided concept expression, with the help of the reasoner.
	 * 
	 * @param ontology
	 * @param reasoner
	 * @param concept1
	 * @return
	 */
	public static Set<OWLClass> getDirectSuperClassesOfConceptViaReasoning(OWLOntology ontology, OWLReasoner reasoner, OWLClassExpression concept1) {
		NodeSet<OWLClass> nodeSet = reasoner.getSuperClasses(concept1, true);
		Iterator<Node<OWLClass>> nodeSet_iterator = nodeSet.iterator();
		
		Set<OWLClass> up_c = new HashSet<OWLClass>();
		
		while ( nodeSet_iterator.hasNext() ) {
			Node<OWLClass> element = nodeSet_iterator.next();
			for (OWLClass x : element.getEntities()) {
				up_c.add(x);
			}
			
		}
		
		return up_c;
	}
	
	/**
	 * Function that returns the set of all SuperClasses Of a provided concept expression, with the help of the reasoner.
	 * 
	 * @param ontology
	 * @param reasoner
	 * @param concept1
	 * @return
	 */
	public static Set<OWLClass> getAllSuperClassesOfConceptViaReasoning(OWLOntology ontology, OWLReasoner reasoner, OWLClassExpression concept1) {
		NodeSet<OWLClass> nodeSet = reasoner.getSuperClasses(concept1, false);
		Iterator<Node<OWLClass>> nodeSet_iterator = nodeSet.iterator();
		
		Set<OWLClass> sup_c = new HashSet<OWLClass>();
		
		while ( nodeSet_iterator.hasNext() ) {
			Node<OWLClass> element = nodeSet_iterator.next();
			for (OWLClass x : element.getEntities()) {
				sup_c.add(x);
			}
			
		}
		
		return sup_c;
	}
	
	/**
	 * Function that returns the set of direct SubClasses Of a provided concept expression, with the help of the reasoner.
	 * 
	 * @param ontology
	 * @param reasoner
	 * @param concept1
	 * @return
	 */
	public static Set<OWLClass> getDirectSubClassesOfConceptViaReasoning(OWLOntology ontology, OWLReasoner reasoner, OWLClassExpression concept1) {
		NodeSet<OWLClass> nodeSet = reasoner.getSubClasses(concept1, true);
		Iterator<Node<OWLClass>> nodeSet_iterator = nodeSet.iterator();
		
		Set<OWLClass> down_c = new HashSet<OWLClass>();
		
		while ( nodeSet_iterator.hasNext() ) {
			Node<OWLClass> element = nodeSet_iterator.next();
			for (OWLClass x : element.getEntities()) {
				down_c.add(x);
			}
			
		}
		
		return down_c;
	}
	
	/**
	 * Function that returns the set of all SubClasses Of a provided concept expression, with the help of the reasoner. 
	 * 
	 * @param ontology
	 * @param reasoner
	 * @param concept1
	 * @return
	 */
	public static Set<OWLClass> getAllSubClassesOfConceptViaReasoning(OWLOntology ontology, OWLReasoner reasoner, OWLClassExpression concept1) {
		NodeSet<OWLClass> nodeSet = reasoner.getSubClasses(concept1, false);
		Iterator<Node<OWLClass>> nodeSet_iterator = nodeSet.iterator();
		
		Set<OWLClass> sub_c = new HashSet<OWLClass>();
		
		while ( nodeSet_iterator.hasNext() ) {
			Node<OWLClass> element = nodeSet_iterator.next();
			for (OWLClass x : element.getEntities()) {
				sub_c.add(x);
			}
			
		}
		
		return sub_c;
	}
	
	/**
	 * Function that returns the set of all concept expressions that are in a CNF defining a concept.
	 * 
	 * @param ontology
	 * @param concept
	 * @return
	 */
	public static Set<OWLClassExpression> CD2Sce(OWLOntology ontology, OWLClass concept) {
		Set<OWLEquivalentClassesAxiom> EQ_concept = getEquivalentClassesFromOntology(ontology, concept);
		
		OWLEquivalentClassesAxiom EQ = Functions.getNthElementOfSet(EQ_concept, 0);
		
		Set<OWLClassExpression> C = EQ.getClassExpressions();
//		OWLClassExpression Name_concept = Functions.getNthElementOfSet(C, 0);
//		OWLClassExpression Desc_concept = Functions.getNthElementOfSet(C, 1);
		
		Set<OWLClassExpression> s = Functions.getNthElementOfSet(C, 1).asConjunctSet();
		
		return s;
	}
	
	/**
     * Function that checks whether the axiom is entailed or not entailed.
     * @return true / false
     */
    public static boolean isEntailed(OWLAxiom axiom, OWLOntology ontology) {

        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontology);

        reasoner.precomputeInferences(InferenceType.values());

        return reasoner.isEntailed(axiom);

    }
    
    /**
     * Function that removes the extension of a file name.
     * 
     * @param fname
     * @return
     */
    public static String removeExtension(String fname) {
	      int pos = fname.lastIndexOf('.');
	      if(pos > -1)
	         return fname.substring(0, pos);
	      else
	         return fname;
	   }
}

























