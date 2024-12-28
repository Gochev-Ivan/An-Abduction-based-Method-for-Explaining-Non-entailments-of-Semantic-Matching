package ExperimentalSetup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.javatuples.Pair;

import OWLUtils.Explanations;
import OWLUtils.Ontologies;
import OWLUtils.owlUtils;
import Utils.Functions;

public class ExperimentalSetup {
	
	public static ManchesterOWLSyntaxOWLObjectRendererImpl rend = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	
	/**
	 * Function that returns the type of an axiom parsed as a String.
	 * 
	 * @param inputAxiom
	 * @return
	 */
	public static String getAxiomType(String inputAxiom) {
		String TypeOfAxiom = "";
		 if (inputAxiom.contains("SubClassOf")) { TypeOfAxiom = "SubClassOf"; } 
		 else if (inputAxiom.contains("EquivalentTo")) { TypeOfAxiom = "EquivalentTo"; }
		 else if (inputAxiom.contains("EquivalentClasses")) { TypeOfAxiom = "EquivalentClasses"; }
		 
		 return TypeOfAxiom;
	}
	
	/**
	 * Function that takes as an input an axiom and w.r.t. to some currently loaded ontology it gets the LHS class and RHS class
	 * from the axiom.
	 * 
	 * @param inputAxiom
	 * @param onto
	 * @return
	 */
	public static Pair<OWLClass, OWLClass> getClassesFromOWLAxiom(OWLAxiom inputAxiom, Ontologies onto) {
		 IRI LHS = null;
		 IRI RHS = null;
		 String[] arr = inputAxiom.toString().split(" ");
		 for (int x = 0 ; x < arr.length ; x++) {
			 if (x == 0) {
				 LHS = IRI.create(arr[x].substring(arr[x].indexOf('<') + 1, arr[x].indexOf('>')));
			 } else if (x == 1) {
				 RHS = IRI.create(arr[x].substring(arr[x].indexOf('<') + 1, arr[x].indexOf('>')));
			 }
		 }
		 
		 OWLClass LHS_class = onto.manager.getOWLDataFactory().getOWLClass(LHS);
		 OWLClass RHS_class = onto.manager.getOWLDataFactory().getOWLClass(RHS);
		 
		 return new Pair<OWLClass, OWLClass>(LHS_class, RHS_class);
	}
	
	/**
	 * Function that takes as an input an axiom and if it contains a conjunction in either the LHS or RHS of the axiom 
	 * it returns a set of axioms constructed from the conjuncts (takes a defined and returns a primitive version the the concepts).
	 * 
	 * @param hypothesisAxiom
	 * @param hypothesisTypeOfAxiom
	 * @return
	 */
	public static Set<String> getPrimitiveAxioms(String hypothesisAxiom) {
		
		String hypothesisTypeOfAxiom = getAxiomType(hypothesisAxiom);
		
		System.out.println("hypothesisTypeOfAxiom = " + hypothesisAxiom);
		System.out.println("hypothesisTypeOfAxiom = " + hypothesisTypeOfAxiom);
		
		 String splitAxiom[] = hypothesisAxiom.split(" " + hypothesisTypeOfAxiom + " ");
		 String lhsExpr = splitAxiom[0];
		 String rhsExpr = splitAxiom[1];
		 
		 Set<String> newAxiomsToAdd = new HashSet<String>();
		 for (String a : owlUtils.getTopLevelConjuncts(lhsExpr)) {
			 for (String b : owlUtils.getTopLevelConjuncts(rhsExpr)) {
				 if ((!a.equals("T")) && (!b.equals("T"))) {
					 if (!a.equals(b)) {
						 newAxiomsToAdd.add(a + " " + hypothesisTypeOfAxiom + " " + b);
					 }
				 } 
			 }
		 }
		 for (String a : owlUtils.getTopLevelConjuncts(lhsExpr)) {
			 for (String b : owlUtils.getConjuncts(rhsExpr)) {
				 if ((!a.equals("T")) && (!b.equals("T"))) {
					 if (!a.equals(b)) {
						 newAxiomsToAdd.add(a + " " + hypothesisTypeOfAxiom + " " + b);
					 }
				 } 
			 }
		 }
		 for (String a : owlUtils.getConjuncts(lhsExpr)) {
			 for (String b : owlUtils.getTopLevelConjuncts(rhsExpr)) {
				 if ((!a.equals("T")) && (!b.equals("T"))) {
					 if (!a.equals(b)) {
						 newAxiomsToAdd.add(a + " " + hypothesisTypeOfAxiom + " " + b);
					 }
				 } 
			 }
		 }
		 for (String a : owlUtils.getConjuncts(lhsExpr)) {
			 for (String b : owlUtils.getConjuncts(rhsExpr)) {
				 if ((!a.equals("T")) && (!b.equals("T"))) {
					 if (!a.equals(b)) {
						 newAxiomsToAdd.add(a + " " + hypothesisTypeOfAxiom + " " + b);
					 }
				 }
			 }
		 }
		 return newAxiomsToAdd;
	 }
	
	/**
	 * Function that returns a single set of all justification axioms - the axioms are in rendered syntax.
	 * 
	 * @param justifications
	 * @return
	 */
	public static Set<String> getAllJustificationAxiomsInSingleSet(Set<Set<OWLAxiom>> justifications) {
		Set<String> allJustificationAxioms = new HashSet<String>();
		 for (Set<OWLAxiom> justification : justifications) {
			 for (OWLAxiom justificationAxiom : justification) {
				 allJustificationAxioms.add(rend.render(justificationAxiom));
			 }
		 }
		 return allJustificationAxioms;
	}
	
	/**
	 * Function that runs the Semantic Matching Scenario of the experiments.
	 * 
	 * The Semantic Matching scenario is based on picking two random concepts from the signature of an ontology, 
	 * establishing them in a random matching axiom which is not entailed by the ontology and explaining that 
	 * matching axiom.  
	 * 
	 * @param ontologies
	 * @param ontoFile 
	 * @param ontoName 
	 * @param LHS_Class
	 * @param RHS_Class
	 * @param constructedAxiom
	 * @param expl
	 * @throws OWLException
	 * @throws IOException
	 */
	public void run_SemanticMatchingExperiments(Ontologies ontologies, String ontoName, File ontoFile, 
			OWLClass LHS_Class, OWLClass RHS_Class, OWLAxiom constructedAxiom, Explanations expl, FileWriter myWriter, 
			FileWriter resultsWriter, FileWriter logWriter) throws OWLException, IOException {
		// --- CONCEPT MATCHING EXPERIMENT --- 
		try {
			// Flag indicating whether the method finished working or not
			boolean isComplete = true;
				
	   	 	// Get all direct subclasses of the LHS class from the random axiom (if it is not an equivalence definition, if it is, then take the equivalence expression):
			 Set<OWLSubClassOfAxiom> LHS_directSubclasses = ontologies.el_currentlyLoadedOntology.getSubClassAxiomsForSubClass(LHS_Class);
			 
			 // Get all direct subclasses of the RHS class from the random axiom (if it is not an equivalence definition, if it is, then take the equivalence expression):
			 Set<OWLSubClassOfAxiom> RHS_directSubclasses = ontologies.el_currentlyLoadedOntology.getSubClassAxiomsForSubClass(RHS_Class);
			 
			 // Get the LHS and RHS concept definitions:
			 String LHS_conceptDef = ontologies.getDefinedClass(LHS_directSubclasses, LHS_Class, RHS_Class);
			 String RHS_conceptDef = ontologies.getDefinedClass(RHS_directSubclasses, RHS_Class, LHS_Class);
			 
			 // Get concept definitions of LHS and RHS as EquivalenTo axioms: 
			 String LHS_conceptDefEqAxiom = rend.render(LHS_Class) + " EquivalentTo " + LHS_conceptDef;
			 String RHS_conceptDefEqAxiom = rend.render(RHS_Class) + " EquivalentTo " + RHS_conceptDef;
	   	 
			 String typeOfAxiom = constructedAxiom.getAxiomType().toString();
			 String nonEntailment = LHS_conceptDef + " " + typeOfAxiom + " " + RHS_conceptDef;
			 
			 System.out.println();
			 System.out.println();
			 System.out.println("======================================================================");
			 System.out.println("|                     --- MATCHING PROBLEM ---                       |");
			 System.out.println("| constructedAxiom = " + constructedAxiom + "                        |");
			 System.out.println("| typeOfAxiom = " + typeOfAxiom + "                                  |");
			 System.out.println("| nonEntailment = " + nonEntailment + "                              |");
			 System.out.println("======================================================================");
			 
			 // Statistics:
			 double median = 0;
			 double avg = 0;
			 int max = 0;
			 List<Integer> lengthsHypotheses = new LinkedList<Integer>();
			 
			 // Run our method:
			 long startTime = System.nanoTime();
			 Set<Set<String>> Hypotheses = expl.getExplanationsNonEntailment(ontologies.el_currentlyLoadedOntology, nonEntailment, typeOfAxiom);
			 long estimatedTime = System.nanoTime() - startTime;
			 
			 estimatedTime = TimeUnit.MILLISECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS);
			 double estTime = (estimatedTime / 1000.0);  // in seconds
			 
			 // Trivial solution or not:
			 boolean onlyTrivialSol = true;
			 if (Hypotheses.size() <= 2) { onlyTrivialSol = false; }
			 
			 // +++
			 int verifiedAxiom = -1;
			 int unverifiedAxiom = -1;
			 int acceptedHypothesis = -1;
			 int rejectedHypothesis = -1;
			 int numberOfAxiomsFromAcceptedHypotheses = -1;
			 int numberOfAxiomsFromRejectedHypotheses = -1;
			 int newKnowledgeIntroduced = -1;
			 int existingKnowledgeFound = -1;
			 int roleInSolution = 0;
			 int conceptInSolution = 0;
			 int totalExprInSolutions = 0;
			 Set<String> allhypAxiomsToAdd = new HashSet<String>();
			 
			 Set<String> visitedAxioms = new HashSet<String>();
			 Set<String> visitedAxiomsRejectedHypotheses = new HashSet<String>();
			 
			 // For each hypothesis (h) in the set of all Hypotheses (H):
			 for (Set<String> hypothesis : Hypotheses) {
				
				// |h|
				int h_cardinality = hypothesis.size();
				
				// Save |h| (for later computing the median):
				lengthsHypotheses.add(h_cardinality);
				
				// average:
				avg += h_cardinality;
				
				// max:
				if (h_cardinality > max) { max = h_cardinality; }
				 
				allhypAxiomsToAdd.addAll(hypothesis);
			 }
			 
			 // For each axiom in all solutions:
			 for (String hypAx : allhypAxiomsToAdd) {
				 
				 // Get the axiom type and split the axiom:
				 String hypAxType = getAxiomType(hypAx);
				 String[] splittedHypAx = hypAx.split(" " + hypAxType + " ");
				 
				 // Count if the LHS contains a role restriction or a concept, respectively:
				 if (splittedHypAx[0].contains("some")) { roleInSolution++; } 
				 else { conceptInSolution++; }
				 
				 // Count if the RHS contains a role restriction or a concept, respectively:
				 if (splittedHypAx[1].contains("some")) { roleInSolution++; } 
				 else { conceptInSolution++; }
				 
				 // Count the total amount of expressions in solutions:
				 totalExprInSolutions += 2;
			 }
			 
			// average |h|:
			avg = avg / Hypotheses.size();
				
			// median |h|:
			median = Functions.median(lengthsHypotheses);
			
			String output = "====================================================================================================\n" +
							"|                                  --- General Info ---                                            |\n" +
							"----------------------------------------------------------------------------------------------------\n" +
							"Ontology file : " + ontoFile +
							"Ontology : " + ontologies.getOntologyIRI(ontologies.currentlyLoadedOntology) + "\n" +
							"====================================================================================================\n" +
							"|                                  --- Random axiom ---                                         |\n" +
							"----------------------------------------------------------------------------------------------------\n" +
							"Random axiom : " + rend.render(constructedAxiom) + "\n" +
							"====================================================================================================\n" +
							"|                                  --- Justifications ---                                          |\n" +
							"----------------------------------------------------------------------------------------------------\n" +
							"-" + "\n" +
							"====================================================================================================\n" +
							"|                                --- Concept descriptions ---                                      |\n" +
							"----------------------------------------------------------------------------------------------------\n" +
							"LHS : " + LHS_conceptDefEqAxiom + "\n" +
							"RHS : " + RHS_conceptDefEqAxiom + "\n" +
							"====================================================================================================\n" +
							"|                                   --- Non-entailment ---                                         |\n" +
							"----------------------------------------------------------------------------------------------------\n" +
							"n : " + nonEntailment + "\n" +
							"====================================================================================================\n" +
							"|                                  --- All Hypotheses axioms ---                                       |\n" +
							"----------------------------------------------------------------------------------------------------\n";
			for (String ad : allhypAxiomsToAdd) { output += ad + "\n"; }
			output += "====================================================================================================\n" +
					  "|                                    --- Results ---                                               |\n" +
					  "----------------------------------------------------------------------------------------------------\n" +
					  "Type of experiment = " + "MATCHING" + "\n" +
					  "Total number of hypotheses found (#H) = " + Functions.round(lengthsHypotheses.size(), 3) + "\n" +
					  "# Accepted hypotheses = " + acceptedHypothesis + "\n" +
					  "# Rejected hypotheses = " + rejectedHypothesis + "\n" +
					  "New knowledge introduced = " + newKnowledgeIntroduced + "\n" +
					  "Existing knowledge found = " + existingKnowledgeFound + "\n" +
					  "newKnowledgeIntroduced % = " + (int) Math.ceil((float) newKnowledgeIntroduced / numberOfAxiomsFromAcceptedHypotheses * 100) + "%" + "\n" +
					  "existingKnowledgeFound % = " + (int) Math.ceil((float) existingKnowledgeFound / numberOfAxiomsFromAcceptedHypotheses * 100) + "%" + "\n" +
					  "Runtime of the method [s] = " + estTime + "\n" +
					  "avg (|H|) = " + avg + "\n" +
					  "max (|H|) = " + max + "\n" +
					  "median (|H|) = " + median + "\n" +
					  "Complete = " + isComplete + "\n" +
					  "Trivial Solution = " + onlyTrivialSol + "\n" +
					  "# concepts in hypotheses axioms = " + conceptInSolution + "\n" +
					  "# role restrictions in hypotheses axioms = " + roleInSolution + "\n" +
					  "# total concepts and role restrictions in hypotheses axiom = " + totalExprInSolutions + "\n" +
					  "####################################################################################################\n\n";
			
			myWriter.write(output);
			
			resultsWriter.write("MATCHING," + isComplete + "," + onlyTrivialSol + "," 
					+ Functions.round(lengthsHypotheses.size(), 3) + "," + avg + "," + max + "," + median + "," + estTime + ","
					+ acceptedHypothesis + "," + rejectedHypothesis + "," + conceptInSolution + "," + roleInSolution + "," 
					+ newKnowledgeIntroduced + "," + existingKnowledgeFound + "," 
					+ (int) Math.ceil((float) newKnowledgeIntroduced / numberOfAxiomsFromAcceptedHypotheses * 100) + ","
					+ (int) Math.ceil((float) existingKnowledgeFound / numberOfAxiomsFromAcceptedHypotheses * 100) + "\n");
			 
		 } catch (Exception e) {
			 logWriter.write(e + " at line 468, catch block Semantic Matching Experiment function.\n");
			 
			 boolean isComplete = false;
			 String output = "====================================================================================================\n" +
						"|                                  --- General Info ---                                            |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"Ontology file : " + ontoFile +
						"Ontology : " + ontologies.getOntologyIRI(ontologies.currentlyLoadedOntology) + "\n" +
						"====================================================================================================\n" +
						"|                                  --- Random axiom ---                                         |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"Random axiom : " + rend.render(constructedAxiom) + "\n" +
						"====================================================================================================\n" +
						"|                                  --- Justifications ---                                          |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"-" + "\n" +
						"====================================================================================================\n" +
						"|                                --- Concept descriptions ---                                      |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"LHS : " + null + "\n" +
						"RHS : " + null + "\n" +
						"====================================================================================================\n" +
						"|                                   --- Non-entailment ---                                         |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"n : " + null + "\n" +
						"====================================================================================================\n" +
						"|                                  --- Hypotheses axioms ---                                       |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"-" + "\n";
						
		output += "====================================================================================================\n" +
				  "|                                    --- Results ---                                               |\n" +
				  "----------------------------------------------------------------------------------------------------\n" +
				  "Type of experiment = " + "MATCHING" + "\n" +
				  "Total number of hypotheses found (#H) = " + null + "\n" +
				  "# Accepted hypotheses = " + null + "\n" +
				  "# Rejected hypotheses = " + null + "\n" +
				  "New knowledge introduced = " + null + "\n" +
				  "Existing knowledge found = " + null + "\n" +
				  "newKnowledgeIntroduced % = " + null + "%" + "\n" +
				  "existingKnowledgeFound % = " + null + "%" + "\n" +
				  "Runtime of the method [s] = " + null + "\n" +
				  "avg (|H|) = " + null + "\n" +
				  "max (|H|) = " + null + "\n" +
				  "median (|H|) = " + null + "\n" +
				  "Complete = " + isComplete + "\n" +
				  "Trivial Solution = " + null + "\n" +
				  "# concepts in hypotheses axioms = " + null + "\n" +
				  "# role restrictions in hypotheses axioms = " + null + "\n" +
				  "# total concepts and role restrictions in hypotheses axiom = " + null + "\n" +
				  "####################################################################################################\n\n";
		myWriter.write(output);
		
		resultsWriter.write("MATCHING," + isComplete + "," + null + "," 
				+ null + "," + null + "," + null + "," + null + "," + null + ","
				+ null + "," + null + "," + null + "," + null + "," 
				+ null + "," + null + "," 
				+ null + ","
				+ null + "\n");
			}
		 catch (OutOfMemoryError e) {
			 logWriter.write(e + " at line 529, catch block Semantic Matching Experiment function.\n");
			 
			 boolean isComplete = false;
			 String output = "====================================================================================================\n" +
						"|                                  --- General Info ---                                            |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"Ontology file : " + ontoFile +
						"Ontology : " + ontologies.getOntologyIRI(ontologies.currentlyLoadedOntology) + "\n" +
						"====================================================================================================\n" +
						"|                                  --- Random axiom ---                                         |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"Random axiom : " + rend.render(constructedAxiom) + "\n" +
						"====================================================================================================\n" +
						"|                                  --- Justifications ---                                          |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"-" + "\n" +
						"====================================================================================================\n" +
						"|                                --- Concept descriptions ---                                      |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"LHS : " + null + "\n" +
						"RHS : " + null + "\n" +
						"====================================================================================================\n" +
						"|                                   --- Non-entailment ---                                         |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"n : " + null + "\n" +
						"====================================================================================================\n" +
						"|                                  --- Hypotheses axioms ---                                       |\n" +
						"----------------------------------------------------------------------------------------------------\n" +
						"-" + "\n";
						
		output += "====================================================================================================\n" +
				  "|                                    --- Results ---                                               |\n" +
				  "----------------------------------------------------------------------------------------------------\n" +
				  "Type of experiment = " + "MATCHING" + "\n" +
				  "Total number of hypotheses found (#H) = " + null + "\n" +
				  "# Accepted hypotheses = " + null + "\n" +
				  "# Rejected hypotheses = " + null + "\n" +
				  "New knowledge introduced = " + null + "\n" +
				  "Existing knowledge found = " + null + "\n" +
				  "newKnowledgeIntroduced % = " + null + "%" + "\n" +
				  "existingKnowledgeFound % = " + null + "%" + "\n" +
				  "Runtime of the method [s] = " + null + "\n" +
				  "avg (|H|) = " + null + "\n" +
				  "max (|H|) = " + null + "\n" +
				  "median (|H|) = " + null + "\n" +
				  "Complete = " + isComplete + "\n" +
				  "Trivial Solution = " + null + "\n" +
				  "# concepts in hypotheses axioms = " + null + "\n" +
				  "# role restrictions in hypotheses axioms = " + null + "\n" +
				  "# total concepts and role restrictions in hypotheses axiom = " + null + "\n" +
				  "####################################################################################################\n\n";
		myWriter.write(output);
		
		resultsWriter.write("MATCHING," + isComplete + "," + null + "," 
				+ null + "," + null + "," + null + "," + null + "," + null + ","
				+ null + "," + null + "," + null + "," + null + "," 
				+ null + "," + null + "," 
				+ null + ","
				+ null + "\n");
		 }
	}
}
