package OWLUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.javatuples.Pair;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import Trees.DTNode;
import Trees.DescriptionTree;

public class owlUtils {
	
//	 public static String prefix1 = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";
	 public static String prefix1 = "http://www.semanticweb.org/goche/ontologies/2021/9/untitled-ontology-6#";
	 public static String prefix2 = "http://www.w3.org/2004/02/skos/core#";
	 public static String prefix3 = "http://www.co-ode.org/ontologies/pizza#";
	 public static String prefix4 = "http://www.w3.org/2002/07/owl#";
	 public static OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	 public static OWLOntology ontology;
	 
	 /**
	  * Function that returns the reasoner.
	  * 
	  * @param onto
	  * 
	  * @return reasoner
	  */
     public static OWLReasoner getReasoner(OWLOntology onto) {
    	 OWLReasonerFactory reasonerFactory = new ReasonerFactory();
         OWLReasoner reasoner = reasonerFactory.createReasoner(onto);
         
    	 return reasoner;
     }
     
     /**
      * Function that synchronizes the reasoner.
      * 
      * @param reasoner
      * @param new_path
      * 
      * @throws OWLOntologyCreationException 
      */
     public static void SyncReasoner(OWLReasoner reasoner, String new_path) throws OWLOntologyCreationException {
    	 reasoner.precomputeInferences(InferenceType.values());
 		
	    // Synchronize the reasoner and save all original knowledge + inferences in a new ontology:
	    // To generate an inferred ontology we use implementations of inferred axiom generators
     	List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
     	gens.add(new InferredSubClassAxiomGenerator());
     	gens.add(new InferredEquivalentClassAxiomGenerator());
     	
     	// Put the inferred axioms into a fresh empty ontology.
     	OWLOntologyManager outputOntologyManager = OWLManager.createOWLOntologyManager();
     	OWLOntology infOnt = outputOntologyManager.createOntology();
     	InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner,
     			gens);
     	iog.fillOntology(outputOntologyManager.getOWLDataFactory(), infOnt);
     	
     	// Save the inferred ontology.
     	try {
 			outputOntologyManager.saveOntology(infOnt,
 					new FunctionalSyntaxDocumentFormat(),
 					IRI.create((new File(new_path).toURI())));
 					// IRI.create((new File("C:\\Users\\goche\\Desktop\\Documents\\PhD\\Protege\\Models\\Pizza_ontology\\pizza-inferred.owl").toURI())));
 		} catch (OWLOntologyStorageException e) {
 			e.printStackTrace();
 		}
     }
     
	 /**
	  * Function which creates an ontology manager.
	  * 
	  * @return manager
	  */
	 public static OWLOntologyManager createOntologyManager() {
	 	 return manager;
	 }
	 
	 /**
	  * Function that creates and returns the data factory.
	  * 
	  * @return manager.getOWLDataFactory()
	  */
	 public static OWLDataFactory createDataFactory() {
	 	 return manager.getOWLDataFactory();
	 }
	 
	 /**
	  * Function which loads the ontology from a given document file.
	  * 
	  * @param path
	  * 
	  * @return ontology
	  * 
	  * @throws OWLOntologyCreationException 
	  */
	 public static OWLOntology loadOntology(String path) throws OWLOntologyCreationException {
	 	 // Load file:
	 	 
	 	 File file = new File(path);
	 	 
	 	 // Loading the ontology:
	 	 ontology = manager.loadOntologyFromOntologyDocument(file);
	 	 
	 	 return ontology;
	 }
	 
	 /**
	  * Function which returns the ontology IRI from a given ontology.
	  * 
	  * @param ontology
	  * 
	  * @return ontology.getOntologyID().getOntologyIRI().get()
	  */
	 public static IRI getOntologyIRI(OWLOntology ontology) {
	 	 return ontology.getOntologyID().getOntologyIRI().get();
	 }
	 
	 /**
	  * Function that creates trees from LHS and RHS of an input axiom:
	  * 
	  * @param inputAxiom
	  * @return
	  */
	 public static Pair<DescriptionTree, DescriptionTree> createTrees(String inputAxiom) {
		// (The method is implemented to work with characters as roles and not full strings, thus we map each string of a role in the expressions to a unique character).
		 String TOA = "";
		 if (inputAxiom.contains("SubClassOf")) { TOA = "SubClassOf"; } 
		 else if (inputAxiom.contains("EquivalentTo")) { TOA = "EquivalentTo"; }
		 else if (inputAxiom.contains("EquivalentClasses")) { TOA = "EquivalentClasses"; }
		 
		 
		  String splitted_input[] = inputAxiom.split(" " + TOA + " ");
		  String LHS_conceptDef = splitted_input[0];
		  String RHS_conceptDef = splitted_input[1];
		  
		  List<String> LHS_Nr = owlUtils.getSignatureFromExpr(LHS_conceptDef).getValue1();
		  List<String> RHS_Nr = owlUtils.getSignatureFromExpr(RHS_conceptDef).getValue1();
		  
		  Map<String, Character> rolesMap = new HashMap<String, Character>();
		  String listOfTreeRoles = "abcdefghijklmnopqrstuvyxwz";
		  int listOfTreeRolesCounter = 0;
		  
		  for (String lhsRole : LHS_Nr) { 
			  if (!rolesMap.containsKey(lhsRole)) { 
				  rolesMap.put(lhsRole, listOfTreeRoles.charAt(listOfTreeRolesCounter)); 
				  listOfTreeRolesCounter++; 
			  }
		  }
		  
		  for (String rhsRole : RHS_Nr) {
			  if (!rolesMap.containsKey(rhsRole)) { 
				  rolesMap.put(rhsRole, listOfTreeRoles.charAt(listOfTreeRolesCounter)); 
				  listOfTreeRolesCounter++; 
			  }
		  }
		  
		  String LHS_conceptDefToInput = LHS_conceptDef;
		  String RHS_conceptDefToInput = RHS_conceptDef;
		  for (Entry<String, Character> entry : rolesMap.entrySet()) {
			  LHS_conceptDefToInput = LHS_conceptDefToInput.replace(entry.getKey(), entry.getValue() + "");
			  RHS_conceptDefToInput = RHS_conceptDefToInput.replace(entry.getKey(), entry.getValue() + "");
		  }
		  
		  String nonEntailmentToInput = LHS_conceptDefToInput + " " + TOA + " " + RHS_conceptDefToInput;
		  
	      String splitted_input_1[] = nonEntailmentToInput.split(" " + TOA + " ");
	      String string1 = splitted_input_1[0];
	      String string2 = splitted_input_1[1];

	      DescriptionTree T1 = owlUtils.expr2tree(string1,'v');
	      DescriptionTree T2 = owlUtils.expr2tree(string2,'w');
	      
	      return new Pair<DescriptionTree, DescriptionTree>(T1, T2);
	 }
	 
	 /**
	  * Function which parses a string into an ontology axiom.
	  * 
	  * @param ontology
	  * @param manager
	  * @param dataFactory
	  * @param classExpressionString
	  * 
	  * @return parser.parseAxiom()
	  */
	 public static OWLAxiom parseClassExpression(OWLOntology ontology, OWLOntologyManager manager, OWLDataFactory dataFactory, String classExpressionString) {
	        
	        @SuppressWarnings("deprecation")
			ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(dataFactory, classExpressionString);
	        parser.setDefaultOntology(ontology);
	        
	        Set<OWLOntology> importsClosure = ontology.getImportsClosure();
	        
	        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
	        BidirectionalShortFormProvider bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager, importsClosure, shortFormProvider);
			OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
			
	        parser.setOWLEntityChecker(entityChecker);
	        
	        return parser.parseAxiom();
	    }
	 
	 /**
	  * Function that returns the top level conjuncts of an EL concept expression.
	  * 
	  * @param C
	  * 
	  * @return label_v
	  */
	 public static LinkedList<String> getTopLevelConjuncts(String C) {
			
			if (C.charAt(0) == '(') {
				LinkedList<String> label_v = new LinkedList<String>();
				label_v.add("T");
				return label_v;
			}
			
			LinkedList<String> label_v = new LinkedList<String>();
			
			String current_concept_label = "";
			
			for (int x = 0 ; x < C.length() ; x++) {
				if (C.charAt(x) == '(') { break; }
				
				current_concept_label += C.charAt(x);
			}
			
			String[] arr_currentConcept = current_concept_label.split(" and ");
			
			for (int x = 0 ; x < arr_currentConcept.length ; x++) {
				if (!arr_currentConcept[x].equals(" ") || !arr_currentConcept[x].equals("") || !arr_currentConcept[x].equals(null)) {
					label_v.add(arr_currentConcept[x].replaceAll(" ", ""));
				}	
			}
			
			return label_v;
		}
	 
	 /**
	  *  Function that identifies all conjuncts from an EL concept expression.
	  *  
	  * @param C
	  * 
	  * @return result
	  */
	 public static List<String> getConjuncts(String C) {
			int x = 0;
			int p = 0;
			int start_idx = 0;
			int end_idx = 0;
			List<String> result = new LinkedList<String>();
			while (x < C.length()) {
				
				if (C.charAt(x) == '(' && p == 0) { start_idx = x; }
				
				if (C.charAt(x) == '(') { p++; }
				
				if (C.charAt(x) == ')') { p--; }
				
				if (C.charAt(x) == ')' && p == 0) { 
					end_idx = x; 
					
					result.add(C.substring(start_idx + 1, end_idx)); }
				
				x++;
			}
			
			return result;
		}
	 
	 /**
	  * Function that returns all conjuncts from an EL concept expression.
	  * 
	  * @param result
	  * 
	  * @return expressionConj
	  */
	 public static List<List<String>> getExpressionParts(List<String> result) {
			List<List<String>> expressionConj = new LinkedList<List<String>>();
			
			for (String expr : result) {
				
				List<String> temp = new LinkedList<String>();
				
				if (!expr.contains("(")) {
					temp.add(expr);
					expressionConj.add(temp);
					continue;
				}
				
				for (int x = 0 ; x < expr.length() ; x++) {
					
					char character = expr.charAt(x);
					
					if (character == '(') {
						temp.add(expr.substring(0, x - 1));
						temp.add(expr.substring(x + 1, expr.length() - 1));
						break;
					}
				}
				expressionConj.add(temp);
			}
			
			return expressionConj;
		}
	 
	 /**
	  * Function that returns the signature from an EL concept expression
	  * @param input
	  * @param ontology
	  * @param manager
	  * @param dataFactory
	  * 
	  * @return Signature
	  */
	 public static Pair<List<String>, List<String>> getSignatureFromExpr(String input) {
			String[] arrayOfString = input.split(" ");
			for (int x = 0 ; x < arrayOfString.length ; x++) { arrayOfString[x] = arrayOfString[x].replaceAll("\\(", "").replaceAll("\\)", ""); }
			
	        List<String> classWords = new LinkedList<String>();
	        List<String> propertyWords = new LinkedList<String>();
	        
			for (int x = 0 ; x < arrayOfString.length ; x++) {
				if (!arrayOfString[x].equals("and") && !arrayOfString[x].equals("some") && !arrayOfString[x].equals("SubClassOf")) {
					if (x + 1 < arrayOfString.length) {
						if (arrayOfString[x + 1].equals("some")) {
							if (!propertyWords.contains(arrayOfString[x])) {
								propertyWords.add(arrayOfString[x]);
							}
						} else {
							if (!classWords.contains(arrayOfString[x])) {
								classWords.add(arrayOfString[x]);							
							}
							
						}
					} else {
						if (!classWords.contains(arrayOfString[x])) {
							classWords.add(arrayOfString[x]);
						}
						
					}
					
				}
	        	
	        }
			Pair<List<String>, List<String>> Signature = new Pair<List<String>, List<String>>(classWords, propertyWords);
			
			return Signature;
		}
	 
	 /**
	  * Function that translates an EL concept expression to a description tree.
	  *  
	  * @param C
	  * @param inputted_vertex_notation
	  * 
	  * @return T
	  */
	 public static DescriptionTree expr2tree(String C, char inputted_vertex_notation) {
			DescriptionTree T = new DescriptionTree(inputted_vertex_notation);
			
			Queue<Pair<String, Integer>> queue = new PriorityQueue<Pair<String, Integer>>();
			
			int index_v = 0;
			int index_u = 1;
			
			queue.add(new Pair<String, Integer>(C, index_v));
			
			while (!queue.isEmpty()) {
				Pair<String, Integer> pair = queue.poll();
				
				String current_C = pair.getValue0();
				
				int idx_v = pair.getValue1();
				
				List<String> result = getConjuncts(current_C);
				
				List<List<String>> expressionParts = getExpressionParts(result);
				
				List<List<String>> newExpressionParts = new LinkedList<List<String>>();
				
				for (List<String> part : expressionParts) {
					List<String> newPart = new LinkedList<String>();
					newPart.add(0, part.get(0));
					for (int part_idx = 1 ; part_idx < part.size() ; part_idx++) {
						
						if ((part.get(part_idx).contains(" some ")) && (!part.get(part_idx).contains(" and "))) {
							newPart.add(part_idx, "T and (" + part.get(part_idx) + ")");
						} else {
							newPart.add(part.get(part_idx));
						}
					}
					newExpressionParts.add(newPart);
				}
				
				for (List<String> part : newExpressionParts) {
					String edge_label = "";
					String concepts_labels = "";
					
					if (part.size() == 1) {
						
						String[] arr = part.get(0).split(" some ");
						
						edge_label = arr[0];
						concepts_labels = arr[1];
					} else {
						for (int x = 0 ; x < part.get(0).length() ; x++) { if (part.get(0).charAt(x) == ' ') { break; } edge_label += part.get(0).charAt(x); }
						
						for (int x = 0 ; x < part.get(1).length() ; x++) {
							if (part.get(1).charAt(x) == '(') { break; }
							
							concepts_labels += part.get(1).charAt(x);
						}
					}
					
					String[] arr = concepts_labels.split(" and ");
					
					LinkedList<String> label_v = getTopLevelConjuncts(current_C);
					LinkedList<String> label_u = new LinkedList<String>();
					
					for (int x = 0 ; x < arr.length ; x++) {
						label_u.add(arr[x]);
					}
					
					char edge_label_char = Character.MIN_VALUE;
					
					if (edge_label.length() == 1) {
						edge_label_char = edge_label.charAt(0);
					}
					
					T.addEdge(idx_v, index_u, edge_label_char, label_v, label_u);
					
					if (part.size() != 1) {
						if (!part.get(1).isEmpty()) {
							queue.add(new Pair<String, Integer>(part.get(1), index_u));
						}
					}
					
					index_u++;
				}
			}
			
			return T;
		}
}
