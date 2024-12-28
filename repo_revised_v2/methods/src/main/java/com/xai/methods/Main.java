package com.xai.methods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.javatuples.Pair;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;

import Examples.WorkingExample;
import OWLUtils.Explanations;
import OWLUtils.Ontologies;
import OWLUtils.owlUtils;
import SyntheticExperiments.Experiments;
import Trees.DescriptionTree;
import Utils.Functions;
import Utils.displayFunctions;
import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;

public class Main {
	
	/**
	 * Function that invokves the working example from the paper.
	 * 
	 * @throws OWLOntologyCreationException
	 */
	public static void invokeWorkingExample() throws OWLOntologyCreationException {
		Pair<DescriptionTree, DescriptionTree> exampleTrees = WorkingExample.workingExample();
		DescriptionTree T1 = exampleTrees.getValue0();
		DescriptionTree T2 = exampleTrees.getValue1();
		XeNON xenon = new XeNON(T1, T2);
		Set<Set<String>> H = xenon.explainNonEntailment("SubClassOf");
		displayFunctions.displayHypotheses(H);
	}
	
	/**
	 * Function that allows a user to create an abduction problem and solve it w.r.t. a chosen ontology.
	 * 
	 * @throws IOException
	 * @throws OWLOntologyCreationException
	 */
	public static void abductionProblem() throws IOException, OWLOntologyCreationException {
		Ontologies ontologies = new Ontologies();
		
		InputStreamReader r=new InputStreamReader(System.in);  
	    BufferedReader br=new BufferedReader(r);  
	    
	    System.out.println("Enter the complete path to the ontology you want to load:");
	    String ontoPath = br.readLine();
	    ontologies.loadOntologyFromFile(ontoPath);
	    ontologies.getELPlusPlusFragmentFromCurrentlyLoadedOntology();
	    ontologies.syncOntology(ontologies.el_currentlyLoadedOntology);
	    
	    System.out.println("Enter the non-entailment you want to explain in the following format : (LHS expression type-of-axiom RHS expression)");
	    System.out.println("Example inputs: ");
	    System.out.println("\t\t 1) A and B SubClassOf C");
	    System.out.println("\t\t 2) A and (r some B) SubClassOf C");
	    System.out.println("\t\t 3) A and (r some (s some C) EquivalentTo D and E and (r some (F))");
	    String nonEntailment = br.readLine();
	    System.out.println("Inputted non-entailment: " + nonEntailment);
	    
	    System.out.println("Input the type of axiom (SubClassOf or EquivalentTo): ");
	    String typeOfAxiom = br.readLine();
	    System.out.println("Type of Axiom: " + typeOfAxiom);
	    
	    // Define and initialize the explanations class:
	    OWLAxiom non_entailment_axiom = owlUtils.parseClassExpression(ontologies.el_currentlyLoadedOntology, 
	    															  ontologies.manager, 
	    															  ontologies.manager.getOWLDataFactory(), 
	    															  nonEntailment);
	    
	    Explanations expl = new Explanations(ontologies.el_currentlyLoadedOntology, non_entailment_axiom);
	    
	    Set<Set<String>> Hypotheses = expl.getExplanationsNonEntailment(ontologies.el_currentlyLoadedOntology, nonEntailment, typeOfAxiom);
	    
	    boolean check_entailment_T_union_H = true;
		for (Set<String> hypothesis : Hypotheses) {
			// Perform a check if the hypotheses size is less than 200, otherwise it takes too long.
			if (Hypotheses.size() < 200) {
				Set<OWLAxiom> axiomsToRemove = new HashSet<OWLAxiom>();
				
				for (String alpha : hypothesis) {
					
					OWLAxiom alpha_axiom = owlUtils.parseClassExpression(ontologies.el_currentlyLoadedOntology, 
																		 ontologies.manager, 
																		 ontologies.manager.getOWLDataFactory(), 
																		 alpha);
					
					axiomsToRemove.add(alpha_axiom);
					
					ontologies.el_currentlyLoadedOntology.add(alpha_axiom);
				}
				
				System.out.println("Onto axioms for a hypothesis : " + ontologies.el_currentlyLoadedOntology.getAxioms());
		        
		        System.out.println("Onto axioms for a hypothesis (reasoned) : " + ontologies.el_currentlyLoadedOntology.getAxioms());
				
		        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontologies.el_currentlyLoadedOntology);

		        reasoner.precomputeInferences(InferenceType.values());

		        check_entailment_T_union_H = reasoner.isEntailed(non_entailment_axiom);
		        
		        reasoner.dispose();
				
				if (check_entailment_T_union_H == false) { 
					
					System.out.println("---");
					
					System.out.println("ONTO AXIOMS FALSE: " + ontologies.el_currentlyLoadedOntology.getAxioms());
					
					System.out.println("hypothesis for which the expl failed : " + hypothesis);
					
					System.out.println("---");
					
					break; 
				}
				
				ontologies.el_currentlyLoadedOntology.removeAxioms(axiomsToRemove);
				
				System.out.println("Onto axioms at the end : " + ontologies.el_currentlyLoadedOntology.getAxioms());
			}
			else {
				System.out.println("Skipping check (too many hypotheses to check)");
			}
	    }
	    
		System.out.println("====================================================================================================");
		System.out.println("|                                    --- Hypotheses ---                                            |");
		displayFunctions.displayHypotheses(Hypotheses);
	}
	
	public static void helpFunction() {
		System.out.println("To perform an action press:");
		System.out.println("\t1) To chose an ontology to load and construct manually an abduction problem to solve.");
		System.out.println("\t2) Invoke the working example from the paper.");
		System.out.println("\t3) Run synthetic experiments.");
		System.out.println("\t4) Run experiments on realistic ontologies.");
		System.out.println("\t5) To call the help function.");
		System.out.println("\t6) 6 or type \'exit\' to exit.");
	}
	
	public static void main(String[] args) throws IOException, OWLException{
		
		InputStreamReader r=new InputStreamReader(System.in);  
	    BufferedReader br=new BufferedReader(r);  
	    
	    String action = "";
	    
	    System.out.println();
    	System.out.println();
    	System.out.println("####################################################################################################");
    	helpFunction();
	    
	    while (true) {
	    	try {
	    		System.out.println("--------------------------------------------------");
		    	System.out.println("Input an action: ");
		    	action = br.readLine();
		    	
		    	if (action.equals("exit") || action.equals("6")) { break; }
		    	
		    	else if (action.equals("1")) {
		    		/**
		    		 * 1 : abductionProblem (CMD UI option 1)
		    		 */
		    		abductionProblem();
		    	}
		    	
		    	else if (action.equals("2")) {
		    		/**
		    		 * 2 : invokeWorkingExample (CMD UI option 2)
		    		 */
		    		invokeWorkingExample();
		    	}
		    	
		    	else if (action.equals("3")) {
		    		/**
		    		 * 3 : runSyntheticExperiments (CMD UI option 3)
		    		 */
		    		Experiments syntheticExperiments = new Experiments();
		    		syntheticExperiments.runSyntheticExperiments();
		    	}
		    	
		    	else if (action.equals("4")) {
		    		/**
		    	     * 4 : SemanticMatchingExperiments (CMD UI option 4)
		    	     */
		    		Ontologies ontos = new Ontologies();
		    		ontos.SemanticMatchingExperiments();
		    	}
		    	
		    	else if (action.equals("5")) {
		    		/**
		    		 * 5: Help (CMD UI option 5)
		    		 */
		    		helpFunction();
		    	}
		    	
		    	else {
		    		System.out.println("Unknown action.");
		    	}
			} catch (Exception e) {
				System.out.println(e);
				continue;
			}
	    }
	    
		
		
		
		
		
		
		
	    
		
		
	}
}