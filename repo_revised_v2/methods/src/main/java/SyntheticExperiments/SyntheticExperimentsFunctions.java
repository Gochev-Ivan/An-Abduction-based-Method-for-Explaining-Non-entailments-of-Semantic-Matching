package SyntheticExperiments;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.javatuples.Pair;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import Trees.DescriptionTree;

public class SyntheticExperimentsFunctions {
	
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
			
			for (List<String> part : expressionParts) {
				
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
	
	public static List<String> getConjuncts(String C) {
		int x = 0;
		int p = 0;
		int start_idx = 0;
		int end_idx = 0;
//		int p1_idx = 0;
//		int p2_idx = 0;
		List<String> result = new LinkedList<String>();
		while (x < C.length()) {
			
			if (C.charAt(x) == '(' && p == 0) { start_idx = x; }
			
//			if (C.charAt(x) == '(' && p == 1) { p1_idx = x; }
//			
//			if (C.charAt(x) == ')' && p == 1) { p2_idx = x; }
			
			if (C.charAt(x) == '(') { p++; }
			
			if (C.charAt(x) == ')') { p--; }
			
			if (C.charAt(x) == ')' && p == 0) { 
				end_idx = x; 
				
//				System.out.println(start_idx + " / " + (p1_idx - 2) + " / " + (p1_idx + 2) + " / " + p2_idx);
				
				result.add(C.substring(start_idx + 1, end_idx)); }
			
			x++;
		}
		
		return result;
	}
	
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
	
	public static Pair<List<String>, List<String>> getSignatureFromExpr(String input, OWLOntology ontology, OWLOntologyManager manager, OWLDataFactory dataFactory) {
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
		
//		List<String> visitedDeclaredClassNames = new LinkedList<String>();
//		for (String className : Signature.getValue0()) {
//			if (!visitedDeclaredClassNames.contains(className)) {
//				OWLClass A = dataFactory.getOWLEntity(EntityType.CLASS, IRI.create(className));
//				OWLDeclarationAxiom declareA = dataFactory.getOWLDeclarationAxiom(A);
//				manager.addAxiom(ontology, declareA);
//				visitedDeclaredClassNames.add(className);
//			}
//		}
//		
//		List<String> visitedDeclaredPropertyNames = new LinkedList<String>();
//		for (String propertyName : Signature.getValue1()) {
//			if (!visitedDeclaredPropertyNames.contains(propertyName)) {
//				OWLObjectProperty property = dataFactory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(propertyName));
//				OWLDeclarationAxiom declareProperty = dataFactory.getOWLDeclarationAxiom(property);
//				manager.addAxiom(ontology, declareProperty);
//				visitedDeclaredPropertyNames.add(propertyName);
//			}
//		}
		
		return Signature;
	}
}
