package OWLUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.javatuples.Pair;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredDisjointClassesAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

import ExperimentalSetup.ExperimentalSetup;
import Utils.Functions;
import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;

public class Ontologies {

	// this will contain (for now) all ontology IRIs -> List of ontologies with equal IRI loaded (different ontologies can have same IRI)
	public OWLOntology currentlyLoadedOntology = null;
	public OWLOntology el_currentlyLoadedOntology = null;
	public OWLOntology inferred_currentlyLoadedOntology = null;
	
	public File currentlyLoadedOntologyFile = null;
	public String currentlyLoadedOntologyName = null;
	
	// this will contain (for now) all ontology file names -> ontology File:
	public Map<String, File> ontologiesDatasetFiles = new HashMap<String, File>();
	
	// Ontology manager:
	public OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	/**
	 * Function that returns the defined class of a class (called mainClass):
	 * 
	 * @param directSubClassesToCheck
	 * @param mainClass
	 * 
	 * @return
	 */
	public String getDefinedClass(Set<OWLSubClassOfAxiom> directSubClassesToCheck, OWLClass mainClass, OWLClass toCheckClass) {
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("| mainClass = " + mainClass + "                                      |");
		System.out.println("| directSubClassesToCheck" + directSubClassesToCheck + "             |");
		System.out.println("| toCheckClass = " + toCheckClass + "                                |");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		List<OWLClassExpression> conjunctsOfExpr = new LinkedList<OWLClassExpression>();
		
		 for (OWLSubClassOfAxiom directSubclass : directSubClassesToCheck) {
			 
			 Set<OWLClassExpression> directSubclassNestedClassExpr = directSubclass.getNestedClassExpressions();
			 directSubclassNestedClassExpr.remove(mainClass);
			 
			 if (directSubclassNestedClassExpr.size() == 1) {
				 
				 for (OWLClassExpression expr : directSubclassNestedClassExpr) { conjunctsOfExpr.add(expr); }
				 
			 } else {
				 OWLClassExpression topExpr = null;
				 for (OWLClassExpression expr : directSubclassNestedClassExpr) {
					 if (expr.toString().contains("ObjectIntersectionOf")) {
						 if (topExpr == null) {
							 topExpr = expr;
						 } else if (expr.toString().length() > topExpr.toString().length()) {
							 topExpr = expr;
						 }
					 } else if (expr.isOWLClass()) {
						 continue;
					 } else if (expr.toString().contains("ObjectSomeValuesFrom")) {
						 if (topExpr == null) {
							 topExpr = expr;
						 } else if (expr.toString().length() > topExpr.toString().length()) {
							 topExpr = expr;
						 }
					 }
				 }
				 conjunctsOfExpr.add(topExpr);
			 }
		 }
		
		 ManchesterOWLSyntaxOWLObjectRendererImpl rend = new ManchesterOWLSyntaxOWLObjectRendererImpl();
		 
		 List<String> owlSyntax_conjunctsOfExpr = new LinkedList<String>();
		 for (OWLClassExpression expr : conjunctsOfExpr) {
//			 System.out.println("expr = " + expr);
			 String str_expr = Functions.cleanExpr(rend.render(expr));
			 
			 owlSyntax_conjunctsOfExpr.add(str_expr);
		 }
		 
		 //Get top level conjuncts:
		 Set<String> top_level_conj = new HashSet<String>();
		 List<String> role_restr_conj = new LinkedList<String>();
		 for (String str_expr : owlSyntax_conjunctsOfExpr) {
			 
			 String temp_str_expr = str_expr;
			 
			 if (str_expr.charAt(0) != '(') {
				 String temp_level_conj = "";
				 for (int x = 0 ; x < str_expr.length() ; x++) {
						if (str_expr.charAt(x) == '(') { break; }
						
						temp_level_conj += str_expr.charAt(x);
				 }
				 
				 if (!temp_level_conj.contains("some")) {
					 
					 String[] temp_level_conj_ARR = temp_level_conj.split(" and ");
					 
					 for (int i = 0 ; i < temp_level_conj_ARR.length ; i++) {
						 top_level_conj.add(temp_level_conj_ARR[i]);
					 }
					 
					 temp_str_expr = temp_str_expr.replace(temp_level_conj, "");
					 
					 if (!temp_str_expr.equals("")) {
						 String new_temp_str_expr = "";
						 int p_counter = 0;
						 
						 if (!(temp_str_expr.contains("(") || temp_str_expr.contains(")"))) {
							 temp_str_expr = "(" + temp_str_expr + ")";
						 }
						 
						 boolean addBracket = true;
						 for (int j = temp_str_expr.length() - 1 ; j >= 0 ; j--) {
							 
							 if ((p_counter == 0) && (temp_str_expr.charAt(j) == ')')) { p_counter++; new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
							 
							 else if ((p_counter != 0) && (temp_str_expr.charAt(j) == ')')) { p_counter++; new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
							 
							 else if (temp_str_expr.charAt(j) != ')') {
								 
								 if (p_counter == 0) { new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
								 
								 else if ((p_counter % 2 == 0) && (p_counter != 0)) { addBracket = true; p_counter = 0; new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
								 
								 if ((p_counter % 2 != 0) && (p_counter != 0)) {
									 if (temp_str_expr.charAt(j) == ' ') { addBracket = true; p_counter = 0; new_temp_str_expr = temp_str_expr.charAt(j) + "(" + new_temp_str_expr; }
									 
									 else if (temp_str_expr.charAt(j) != ' ') { 
										 if (addBracket == true) {
											 new_temp_str_expr = temp_str_expr.charAt(j) + ")" + new_temp_str_expr;
											 addBracket = false;
										 } else {
											 new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr;
										 }
										 
									 }
								 }
							 }
						 } // END (int j = temp_str_expr.length() - 1 ; j >= 0 ; j--)
						 
						 if (new_temp_str_expr.charAt(0) == '(') {
							 role_restr_conj.add(new_temp_str_expr);
						 } else {
							 role_restr_conj.add("(" + new_temp_str_expr + ")");
						 }
						 
						 
						 
					 } // END if (!temp_str_expr.equals(""))
					 
				 } else if ((temp_level_conj.contains("some"))) {
					 
					 String new_temp_str_expr = "";
					 int p_counter = 0;
					 
					 if (!(temp_str_expr.contains("(") || temp_str_expr.contains(")"))) {
						 temp_str_expr = "(" + temp_str_expr + ")";
					 }
					 
					 boolean addBracket = true;
					 for (int j = temp_str_expr.length() - 1 ; j >= 0 ; j--) {
						 
						 if ((p_counter == 0) && (temp_str_expr.charAt(j) == ')')) { p_counter++; new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
						 
						 else if ((p_counter != 0) && (temp_str_expr.charAt(j) == ')')) { p_counter++; new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
						 
						 else if (temp_str_expr.charAt(j) != ')') {
							 
							 if (p_counter == 0) { new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
							 
							 else if ((p_counter % 2 == 0) && (p_counter != 0)) { addBracket = true; p_counter = 0; new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
							 
							 if ((p_counter % 2 != 0) && (p_counter != 0)) {
								 if (temp_str_expr.charAt(j) == ' ') { addBracket = true; p_counter = 0; new_temp_str_expr = temp_str_expr.charAt(j) + "(" + new_temp_str_expr; }
								 
								 else if (temp_str_expr.charAt(j) != ' ') { 
									 if (addBracket == true) {
										 new_temp_str_expr = temp_str_expr.charAt(j) + ")" + new_temp_str_expr;
										 addBracket = false;
									 } else {
										 new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr;
									 }
									 
								 }
							 }
						 }
					 } // END (int j = temp_str_expr.length() - 1 ; j >= 0 ; j--)
					 
					 if (new_temp_str_expr.charAt(0) == '(') {
						 role_restr_conj.add(new_temp_str_expr);
					 } else {
						 role_restr_conj.add("(" + new_temp_str_expr + ")");
					 }
				 } // END else if ((temp_level_conj.contains("some")))
			 } // END if (str_expr.charAt(0) != '(')
			 
			 else if (str_expr.charAt(0) == '(') {
				 String new_temp_str_expr = "";
				 int p_counter = 0;
				 
				 boolean addBracket = true;
				 for (int j = temp_str_expr.length() - 1 ; j >= 0 ; j--) {
					 
					 if ((p_counter == 0) && (temp_str_expr.charAt(j) == ')')) { p_counter++; new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
					 
					 else if ((p_counter != 0) && (temp_str_expr.charAt(j) == ')')) { p_counter++; new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
					 
					 else if (temp_str_expr.charAt(j) != ')') {
						 
						 if (p_counter == 0) { new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
						 
						 else if ((p_counter % 2 == 0) && (p_counter != 0)) { addBracket = true; p_counter = 0; new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr; }
						 
						 if ((p_counter % 2 != 0) && (p_counter != 0)) {
							 if (temp_str_expr.charAt(j) == ' ') { addBracket = true; p_counter = 0; new_temp_str_expr = temp_str_expr.charAt(j) + "(" + new_temp_str_expr; }
							 
							 else if (temp_str_expr.charAt(j) != ' ') { 
								 if (addBracket == true) {
									 new_temp_str_expr = temp_str_expr.charAt(j) + ")" + new_temp_str_expr;
									 addBracket = false;
								 } else {
									 new_temp_str_expr = temp_str_expr.charAt(j) + new_temp_str_expr;
								 }
								 
							 }
						 }
					 }
				 } // END (int j = temp_str_expr.length() - 1 ; j >= 0 ; j--)
				 
				 if (new_temp_str_expr.charAt(0) == '(') {
					 role_restr_conj.add(new_temp_str_expr);
				 } else {
					 role_restr_conj.add("(" + new_temp_str_expr + ")");
				 }
			 }
			 
		 } // END for (String str_expr : owlSyntax_conjunctsOfExpr)
		 
//		 System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------");
//		 
//		 System.out.println("mainClass = " + mainClass);
//		 
//		 System.out.println();
//		 
//		 System.out.println("owlSyntax_conjunctsOfExpr = ");
//		 for (String conjExpr : owlSyntax_conjunctsOfExpr) {
//			 System.out.println(conjExpr);
//		 }
//		 
//		 System.out.println();
//		 
//		 System.out.println("top_level_conj = " + top_level_conj);
//		 
//		 System.out.println("role_restr_conj = ");
//		 for (String roleRestConjExpr : role_restr_conj) {
//			 System.out.println(roleRestConjExpr);
//		 }
//		 
//		 System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------");
		 
		 String toRemoveClass = rend.render(toCheckClass);
		 
		 if (top_level_conj.contains(toRemoveClass)) {
			 top_level_conj.removeAll(Collections.singleton(toRemoveClass));
		 }
		 
		 List<String> colToRemove = new LinkedList<String>();
		 for (String xRoleExpr : role_restr_conj) {
			 if (xRoleExpr.contains(toRemoveClass)) {
				 colToRemove.add(xRoleExpr);
			 }
		 }
		 role_restr_conj.removeAll(colToRemove);
		 
		 String conceptDef = "";
		 
		 if ((top_level_conj.isEmpty()) && (role_restr_conj.isEmpty())) {
			 conceptDef = rend.render(mainClass);
		 } 
		 
		 else if ((!top_level_conj.isEmpty()) && (role_restr_conj.isEmpty())) {
			 conceptDef = String.join(" and ", top_level_conj);
		 }
		 
		 else if ((top_level_conj.isEmpty()) && (!role_restr_conj.isEmpty())) {
			 conceptDef = String.join(" and ", role_restr_conj);
		 }
		 
		 else if ((!top_level_conj.isEmpty()) && (!role_restr_conj.isEmpty())) {
			 conceptDef = String.join(" and ", top_level_conj) + " and " + String.join(" and ", role_restr_conj);
		 }
		 
		return conceptDef;
	}
	/**
	 * Function that returns the ontology prefix to clear when presenting results.
	 * 
	 * @return
	 */
	public String getPrefixToClear() {
		return getOntologyIRI(currentlyLoadedOntology).toString().substring(0, getOntologyIRI(currentlyLoadedOntology).toString().lastIndexOf('/')+1);
	}
	
    /**
	  * Function to create and return the Pellet reasoner for an ontology.
	  * 
	  * @param onto
	  * 
	  * @return reasoner
	  */
	public static OpenlletReasoner getPelletReasoner(OWLOntology onto) {
		return OpenlletReasonerFactory.getInstance().createReasoner(onto);
	}
	
	/**
     * Function that synchronizes the Pellet reasoner.
     * 
     * @param reasoner
     * @param new_path
     * 
     * @throws OWLOntologyCreationException 
     */
    public void syncOntology(OWLOntology onto, String new_path) throws OWLOntologyCreationException {
    	
    	OpenlletReasoner reasoner = getPelletReasoner(onto);
//    	OWLReasonerFactory reasonerFactory = new ReasonerFactory();
//      OWLReasoner reasoner = reasonerFactory.createReasoner(onto);
        
//    	reasoner.precomputeInferences(InferenceType.values());

        // Synchronize the reasoner and save all original knowledge + inferences in a new ontology:
        // To generate an inferred ontology we use implementations of inferred axiom generators
    	List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
    	gens.add(new InferredSubClassAxiomGenerator());
    	gens.add(new InferredEquivalentClassAxiomGenerator());
        gens.add(new InferredDisjointClassesAxiomGenerator());
    	
   	 	// Put the inferred axioms into a fresh empty ontology.
    	OWLOntologyManager outputOntologyManager = OWLManager.createOWLOntologyManager();
    	OWLOntology infOnt = outputOntologyManager.createOntology();
    	InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner,
    			gens);
    	iog.fillOntology(outputOntologyManager.getOWLDataFactory(), infOnt);
    	
    	// Save the inferred ontology.
    	try {
    		
    		File new_file = new File(new_path);
    		
			outputOntologyManager.saveOntology(infOnt, new FunctionalSyntaxDocumentFormat(), IRI.create((new_file.toURI())));
			
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
    	
    	reasoner.dispose();
    }
    
    /**
     * Function that synchronizes the Pellet reasoner.
     * 
     * @param reasoner
     * @param new_path
     * 
     * @throws OWLOntologyCreationException 
     */
    public void syncOntology(OWLOntology onto) throws OWLOntologyCreationException {
    	
    	OpenlletReasoner reasoner = getPelletReasoner(onto);
//    	OWLReasonerFactory reasonerFactory = new ReasonerFactory();
//      OWLReasoner reasoner = reasonerFactory.createReasoner(onto);
        
    	reasoner.precomputeInferences(InferenceType.values());

        // Synchronize the reasoner and save all original knowledge + inferences in a new ontology:
        // To generate an inferred ontology we use implementations of inferred axiom generators
    	List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
    	gens.add(new InferredSubClassAxiomGenerator());
    	gens.add(new InferredEquivalentClassAxiomGenerator());
//        gens.add(new InferredDisjointClassesAxiomGenerator());
    	
   	 	// Put the inferred axioms into a fresh empty ontology.
    	OWLOntologyManager outputOntologyManager = OWLManager.createOWLOntologyManager();
    	OWLOntology infOnt = outputOntologyManager.createOntology();
    	InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner,
    			gens);
    	iog.fillOntology(outputOntologyManager.getOWLDataFactory(), infOnt);
    	
    	inferred_currentlyLoadedOntology = infOnt;
    	
//    	return infOnt;
    	
    	reasoner.dispose();
    }
    
    /**
	  * Function which returns the ontology IRI from a given ontology.
	  * 
	  * @param ontology
	  * 
	  * @return ontology.getOntologyID().getOntologyIRI().get()
	  */
	 public IRI getOntologyIRI(OWLOntology ontology) {
		 return ontology.getOntologyID().getOntologyIRI().get();
	 }
	 
    /**
	  * Function which loads a single ontology file from a given document file and adds it to the ontologiesDatasetFiles.
	  * 
	  * @param path
	  * 
	  * @throws OWLOntologyCreationException 
	  */
	 public void loadOntologyFromFile(String path) throws OWLOntologyCreationException {
	 	 File file = new File(path);
	 	 
	 	 try {
	 		this.currentlyLoadedOntology = null;
	 		this.el_currentlyLoadedOntology = null;
	 		this.inferred_currentlyLoadedOntology = null;
	 		 
	 		this.manager.clearOntologies();
	 		
	 		this.currentlyLoadedOntology = manager.loadOntologyFromOntologyDocument(file);
	 		 
	 		this.currentlyLoadedOntologyFile = file;
	 		this.currentlyLoadedOntologyName = file.getName();
	 		 
	 		if (!this.ontologiesDatasetFiles.containsKey(file.getName())) {
				 
				 this.ontologiesDatasetFiles.put(file.getName(), file);
				 
			 }
			
		} catch (Exception e) {
			System.out.println("----------------------------------------------------------------------------------------------------");
			System.out.println(file.getName() + " | " + e);
		}	 
	 }
	 
	 /**
	  * Function which loads a single ontology file from a given document file and adds it to the ontologiesDatasetFiles.
	  * 
	  * @param path
	  * 
	  * @throws OWLOntologyCreationException 
	  */
	 public void loadOntologyFromFile(File file) throws OWLOntologyCreationException {
	 	 try {
	 		 
	 		 manager.clearOntologies();
	 		 
	 		 currentlyLoadedOntology = manager.loadOntologyFromOntologyDocument(file);
	 		 
	 		 currentlyLoadedOntologyFile = file;
	 		 currentlyLoadedOntologyName = file.getName();
	 		 
	 		if (!this.ontologiesDatasetFiles.containsKey(file.getName())) {
				 
				 this.ontologiesDatasetFiles.put(file.getName(), file);
				 
			 }
			
		} catch (Exception e) {
			System.out.println("----------------------------------------------------------------------------------------------------");
			System.out.println(file.getName() + " | " + e);
		}	 
	 }
	 
	 /**
	  * Function which loads ontology files from a given folder and adds them to the ontologiesDatasetFiles map.
	  * 
	  * @param pathFolder
	  * 
	  * @throws OWLOntologyCreationException 
	  */
	 public void loadAllOntologyFilesFromDirectory(String directory) throws OWLOntologyCreationException {
	 	 // Load file:
	 	 File folder = new File(directory);
	 	 
	 	 int counter = 0;
	 	 int folder_size = folder.listFiles().length;
	 	 for (File file : folder.listFiles()) { 
	 		 
	 		 try {
	 			 if (!this.ontologiesDatasetFiles.containsKey(file.getName())) {
	 				 this.ontologiesDatasetFiles.put(file.getName(), file);
	 			 }
	 			 
	 			counter++;
				
			} catch (Exception e) {
				System.out.println("----------------------------------------------------------------------------------------------------");
				System.out.println(file.getName() + " | " + e);
			} 
	 	 }
	 }
	 
	 /**
	  * Function which loads ontology files from a given folder and adds them to the ontologiesDatasetFiles map.
	  * 
	  * @param pathFolder
	  * 
	  * @throws OWLOntologyCreationException 
	  */
	 public void loadOntologyFilesFromDirectory(String directory, int numberOfOntologiesToLoad) throws OWLOntologyCreationException {
	 	 // Load file:
	 	 File folder = new File(directory);
	 	 
	 	 int counter = 0;
	 	 int folder_size = folder.listFiles().length;
	 	 for (File file : folder.listFiles()) { 
	 		 
	 		 Functions.clrscr();
//	 		 Functions.clearConsole();
	 		 System.out.println("Loading " + (int) Math.ceil((float) counter / folder_size * 100) + "%");
	 		 
	 		 try { 
//	 			 System.out.println("Loading file number " + counter + " : " + file.getAbsolutePath());
	 			 
	 			 if (!this.ontologiesDatasetFiles.containsKey(file.getName())) {
	 				 
	 				 this.ontologiesDatasetFiles.put(file.getName(), file);
	 				 
	 			 }
	 			 
	 			counter++;
				
			} catch (Exception e) {
				System.out.println("----------------------------------------------------------------------------------------------------");
				System.out.println(file.getName() + " | " + e);
			}
	 		 
	 		 if (counter == numberOfOntologiesToLoad) {
	 			break;
	 		 } // end if (counter == numberOfOntologiesToLoad) 
	 	 } // end for (File file : folder.listFiles())
	 }
	 
	 /**
	  * Function that returns the EL++ fragment from an ontology.
	  * 
	  * @param ontology
	  * @return elOntology
	  * @throws OWLOntologyCreationException
	  */
	 public OWLOntology getELPlusPlusFragmentFromOntology(OWLOntology ontology, OWLOntologyManager m) throws OWLOntologyCreationException {
		 
		 IRI elOntologyIRI = IRI.create(this.getOntologyIRI(ontology).toString() + "_EL++");
		 
		 OWLOntology elOntology = m.createOntology(elOntologyIRI);
		 
		 // Get declarations and EL++ axioms:
		 elOntology.add(ontology.getAxioms(AxiomType.DECLARATION));
		 elOntology.add(ontology.getAxioms(AxiomType.SUBCLASS_OF));
		 elOntology.add(ontology.getAxioms(AxiomType.EQUIVALENT_CLASSES));
		 elOntology.add(ontology.getAxioms(AxiomType.DISJOINT_CLASSES));
		 
		 List<OWLAxiom> axiomsToRemove = new LinkedList<OWLAxiom>();
		 for (OWLAxiom axiom : elOntology.getAxioms()) {
			 if (axiom.toString().contains("ObjectAllValuesFrom") || axiom.toString().contains("ObjectUnionOf")) {
				 axiomsToRemove.add(axiom);
			 }
		 }
		 
		 elOntology.remove(axiomsToRemove);
		 
		 return elOntology;
	 }
	 
	 /**
	  * Function that returns the EL++ fragment from the currently loaded ontology.
	  * 
	  * @param ontology
	  * @return elOntology
	  * @throws OWLOntologyCreationException
	  */
	 public void getELPlusPlusFragmentFromCurrentlyLoadedOntology() throws OWLOntologyCreationException {
		 
		 IRI elOntologyIRI = IRI.create(getOntologyIRI(this.currentlyLoadedOntology).toString() + "_EL++");
		 
		 OWLOntology elOntology = manager.createOntology(elOntologyIRI);
		 
		 // Get declarations and EL++ axioms:
		 elOntology.add(this.currentlyLoadedOntology.getAxioms(AxiomType.DECLARATION));
		 elOntology.add(this.currentlyLoadedOntology.getAxioms(AxiomType.SUBCLASS_OF));
		 elOntology.add(this.currentlyLoadedOntology.getAxioms(AxiomType.EQUIVALENT_CLASSES));
		 elOntology.add(this.currentlyLoadedOntology.getAxioms(AxiomType.DISJOINT_CLASSES));
		 
		 List<OWLAxiom> axiomsToRemove = new LinkedList<OWLAxiom>();
		 for (OWLAxiom axiom : elOntology.getAxioms()) {
			 if (axiom.toString().contains("ObjectAllValuesFrom") || axiom.toString().contains("ObjectUnionOf") 
					 || axiom.toString().contains("value")
					 || axiom.toString().contains("hasValue")
					 || axiom.toString().contains("ObjectMinCardinality")
					 || axiom.toString().contains("ObjectMaxCardinality")
					 || axiom.toString().contains("individual")
					 || axiom.toString().contains("Individual")
					 || axiom.toString().contains("ObjectComplementOf")
					 || axiom.toString().contains("ObjectExactCardinality")
					 || axiom.toString().contains("exactly")
					 || axiom.toString().contains("min")
					 || axiom.toString().contains("max")) {
				 axiomsToRemove.add(axiom);
			 }
		 }
		 
		 elOntology.remove(axiomsToRemove);
		 
		 el_currentlyLoadedOntology = elOntology;
		 
//		 return elOntology;
	 }
	 
	 /**
	  * Function that returns the signature of an ontology.
	  * 
	  * @param ontology
	  * 
	  * @return (Nc, Nr)
	  */
	 public Pair<List<OWLEntity>, List<OWLEntity>> getSignatureFromOntology(OWLOntology ontology) {
		 // This is the signature of the EL fragment of the ontology:
		 List<OWLEntity> Signature = ontology.signature().collect(Collectors.toList());
		 List<OWLEntity> Nc = new LinkedList<OWLEntity>();
		 List<OWLEntity> Nr = new LinkedList<OWLEntity>();
		 for (OWLEntity entity : Signature) {
			 if (entity.isOWLClass()) {
				 Nc.add(entity);
			 } else if (entity.isOWLObjectProperty()) {
				 Nr.add(entity);
			 }
		 }
		 
		 return new Pair<List<OWLEntity>, List<OWLEntity>>(Nc, Nr);
	 }
	 
	 /**
	  * Function that returns the signature of the currently loaded ontology.
	  * 
	  * @param ontology
	  * 
	  * @return (Nc, Nr)
	  */
	 public Pair<List<OWLEntity>, List<OWLEntity>> getSignatureFromCurrentlyLoadedOntology() {
		 // This is the signature of the EL fragment of the ontology:
		 List<OWLEntity> Signature = this.currentlyLoadedOntology.signature().collect(Collectors.toList());
		 List<OWLEntity> Nc = new LinkedList<OWLEntity>();
		 List<OWLEntity> Nr = new LinkedList<OWLEntity>();
		 for (OWLEntity entity : Signature) {
			 if (entity.isOWLClass()) {
				 Nc.add(entity);
			 } else if (entity.isOWLObjectProperty()) {
				 Nr.add(entity);
			 }
		 }
		 
		 return new Pair<List<OWLEntity>, List<OWLEntity>>(Nc, Nr);
	 }
	 
	 /**
	  * Function that performs the semantic matching experiments:
	  * 
	  * @throws IOException
	  * @throws OWLRuntimeException
	  * @throws OWLException
	  * @throws StringIndexOutOfBoundsException
	  * @throws ConcurrentModificationException
	  */
	 public void SemanticMatchingExperiments() throws IOException, OWLRuntimeException, OWLException, StringIndexOutOfBoundsException, ConcurrentModificationException {
//		 ManchesterOWLSyntaxOWLObjectRendererImpl rend = new ManchesterOWLSyntaxOWLObjectRendererImpl();
		 Ontologies ontologies = new Ontologies();
		 
		 InputStreamReader r=new InputStreamReader(System.in);  
		 BufferedReader br=new BufferedReader(r);
		 
		 String header = "ID,Experiment,Complete,Success,#H,avg(|H|),max(|H|),median(|H|),t[s],"
				 	   + "# Accepted hypotheses,# Rejected hypotheses,"
				 	   + "# Concepts in H,# Role Restrictions in H,New Knowledge Introduced,Existing Knowledge Found,"
				 	   + "New Knowledge Introduced %,Existing Knowledge Found %\n";
		 
		 // Code to input the folder from which the ontologies dataset will be read:
		 System.out.println("Input complete path to folder from which you want to read the ontologies:");
	     String folder = br.readLine();
		 System.out.println("Inputted path: " + folder);
	     
	     System.out.println("Enter the complete path of the folder in which you want to save the experimental results: ");  
	     String filePath = br.readLine();
	     System.out.println("Inputted path: " + filePath);
		 
	     System.out.println("Input the number of AP problems you want to construct for each ontology:");
		 int predefinedValue = Integer.parseInt(br.readLine());
		 System.out.println("#AP problems per loaded ontology: " + predefinedValue);
		 
	     FileWriter resultsWriter = new FileWriter(filePath + "\\" + "Semantic_Matching_Experiments_results.txt");
	     
	     FileWriter logWriter = new FileWriter(filePath + "\\" + "captains_log.txt");
	     
	     // Success := is there only trivial solution
	     resultsWriter.write(header);
	     
//	     int predefinedValue = 10;  // number of times the experiment will be conducted for each ontology
//	     int predefinedValue = 250;  // number of times the experiment will be conducted for each ontology
	     
		 // Load all ontology files from the directory:
		 ontologies.loadAllOntologyFilesFromDirectory(folder);
		 
		 int experiment_id = 0;
		 
		 for (Entry<String, File> ontologiesFilesEntry : ontologies.ontologiesDatasetFiles.entrySet()) {
			 
			 // Get the ontology name:
			 String ontoName = ontologiesFilesEntry.getKey();
			 
			 // Define the file name in which the results from the experiments performed on the currently loaded ontology are saved:
			 String fileName = ontoName + ".txt";
			 
			 // Load and prepare a current ontology to work with:
			 File ontoFile = ontologiesFilesEntry.getValue();
			 
			 // Filter ontologies (no filter included):
//			 if (ontoFile.length() >= filterFileSize) {
//				 System.out.println("Skipping : " + ontoName + " (large file size)");
//				 continue;
//			 } else if(ontologies.currentlyLoadedOntology.getAxiomCount() >= filterNumberOfAxioms) {
//				 System.out.println("Skipping : " + ontoName + " (large axiom count)");
//				 continue;
//			 } else {
//				 System.out.println("Working with : " + ontoName);
//			 }
			 
			 // Load ontology file:
			 try {
				 ontologies.loadOntologyFromFile(ontoFile);
				 ontologies.getELPlusPlusFragmentFromCurrentlyLoadedOntology();
			     ontologies.syncOntology(ontologies.el_currentlyLoadedOntology);
			} catch (Exception e) {
				logWriter.write(e + " at line 819, catch block CONCEPT MATCHING Experiment.\n");
				System.out.println("Skipping : " + ontoName + ", " + e);
				continue;
			}
			 
		    // Define the writer for the results from the experiments on the currently loaded ontology: 
		    FileWriter myWriter = new FileWriter(filePath + "\\" + fileName);
		    
		    // Perform predefinedValue number of experiments onto the currently loaded ontology:
		    for (int numberOfExperimentsForCurrentOnto = 0 ; numberOfExperimentsForCurrentOnto < predefinedValue ; numberOfExperimentsForCurrentOnto++) {
		    	 
		    	 List<OWLEntity> Nc = new LinkedList<OWLEntity>();
		    	 OWLClass LHS_Class = null;
		    	 OWLClass RHS_Class = null;
		    	 
		    	 try {
		    		// Pick random concepts from the signature (LHS_Class and RHS_Class):
				    Nc = ontologies.getSignatureFromCurrentlyLoadedOntology().getValue0();
				    LHS_Class = (OWLClass) Functions.getByRandomClass(new HashSet<OWLEntity>(Nc));
				    RHS_Class = (OWLClass) Functions.getByRandomClass(new HashSet<OWLEntity>(Nc));
				} catch (Exception e) {
					logWriter.write(e + " at line 844, catch block CONCEPT MATCHING Experiment.\n");
					System.out.println("Skipping : " + ontoName + ", " + e);
					continue;
				}
		    	 
		    	 // Initial write of global results for all experiments
		    	 resultsWriter.write(experiment_id + ",");
		    	 experiment_id++;
		    	 
			     // Construct a random (SubClassOf or EquivalentTo) axiom from the randomly picked classes (LHS_Class and RHS_Class):
			     Set<String> axiomTypesSet = new HashSet<String>();
			     axiomTypesSet.add("SubClassOf");
			     axiomTypesSet.add("EquivalentTo");
			     String randomTypeOfAxiom = Functions.getByRandomClass(axiomTypesSet);
			     OWLAxiom constructedAxiom = null;
			     if (randomTypeOfAxiom.equals("SubClassOf")) {
			    	 constructedAxiom = ontologies.manager.getOWLDataFactory().getOWLSubClassOfAxiom(LHS_Class, RHS_Class);
			     }
			     else {
			    	 constructedAxiom = ontologies.manager.getOWLDataFactory().getOWLEquivalentClassesAxiom(LHS_Class, RHS_Class);
			     }
			     
			     // Define and initialize the explanations class:
			     Explanations expl = new Explanations(ontologies.el_currentlyLoadedOntology, constructedAxiom);
			     
			     try {
			    	 ExperimentalSetup experimentalSetup = new ExperimentalSetup();
				     
				     experimentalSetup.run_SemanticMatchingExperiments(ontologies, ontoName, ontoFile, 
																	   LHS_Class, RHS_Class, constructedAxiom, expl, 
																	   myWriter, resultsWriter, logWriter);
				} catch (Exception e) {
					logWriter.write(e + " at line 874, catch block CONCEPT MATCHING Experiment.\n");
					resultsWriter.write(null + "," + false + "," + null + "," 
							+ null + "," + null + "," + null + "," + null + "," + null + ","
							+ null + "," + null + "," + null + "," + null + "," 
							+ null + "," + null + "," 
							+ null + ","
							+ null + "\n");
				}
			     
		     }
		     myWriter.close();
		 }
		 resultsWriter.close();
		 logWriter.close();
	 }
}
