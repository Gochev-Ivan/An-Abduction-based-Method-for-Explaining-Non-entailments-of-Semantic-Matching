package SyntheticExperiments;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import com.xai.methods.XeNON;

import OWLUtils.owlUtils;
import Trees.DTNode;
import Trees.DescriptionTree;
import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;

public class Experiments {
	/**
	 * Function that returns the explanations for a non-entailment.
	 * 
	 * @param T1
	 * @param T2
	 * @return H
	 * 
	 * @throws OWLOntologyCreationException 
	 */
	@SuppressWarnings("unchecked")
	public static Set<Set<String>> call_XeNON(DescriptionTree T1, DescriptionTree T2) throws OWLOntologyCreationException {
		
		Set<Set<String>> H = new HashSet<Set<String>>();
		
		// Generate Hypotheses:
		@SuppressWarnings("rawtypes")
		XeNON xenon = new XeNON(T1, T2);
		
		@SuppressWarnings("rawtypes")
		Map i = xenon.subtreeIsomorphisms();
		
		System.out.println("# Subtree Isomorphisms : " + i.size());
		
		H = xenon.constructHypotheses(i, "SubClassOf");
		
		System.out.println("# Hypotheses : " + H.size());
		
		return H;
	}
	
	/**
	 * Function that returns the explanations for a non-entailment.
	 * 
	 * @param T1
	 * @param T2
	 * @return H
	 * 
	 * @throws OWLOntologyCreationException 
	 */
	@SuppressWarnings("unchecked")
	public static Set<Set<String>> call_XeNON_heuristics(DescriptionTree T1, DescriptionTree T2) throws OWLOntologyCreationException {
		
		Set<Set<String>> H = new HashSet<Set<String>>();
		
		// Generate Hypotheses:
		@SuppressWarnings("rawtypes")
		XeNON xenon = new XeNON(T1, T2);
		
		@SuppressWarnings("rawtypes")
		Map i = xenon.heuristicsSubtreeIsomorphisms();
		
		H = xenon.constructHypotheses(i, "SubClassOf");
		
		return H;
	}
	
	/**
	 * Function that returns the explanations for a non-entailment.
	 * 
	 * @param H
	 * @return Hypotheses
	 * 
	 * @throws OWLOntologyCreationException 
	 */
	public static Set<Set<OWLAxiom>> translateHypotheses(Set<Set<String>> H) throws OWLOntologyCreationException {
		
		Set<Set<OWLAxiom>> Hypotheses = new HashSet<Set<OWLAxiom>>();
		
		return Hypotheses;
	}
	
	/**
	 * Function that displays the hypotheses.
	 * 
	 * @param H
	 * @return 
	 * 
	 */
	public static void displayHypotheses(Set<Set<String>> H, int numberOfHypothesesToDisplay) {
		
		int counter = 0;
		
		System.out.println("H = ");
		for (Set<String> element : H) {
			
			if (counter == numberOfHypothesesToDisplay) {
				break;
			}
			
			counter++;
			
			for (String el : element) {
				System.out.println("\t" + el);
			}
			System.out.println("----------");
		}
		
	}
	
	/**
	 * Function that returns the explanations for a non-entailment.
	 * 
	 * @param vertex_notation
	 * @return T
	 * 
	 * @throws OWLOntologyCreationException 
	 */
	public static DescriptionTree generateRandomDescriptionTree(char vertex_notation, int a, int b, int c, int d) {
		
		DescriptionTree T = new DescriptionTree(vertex_notation);
		
		// starting point of num_random_parts
		int e = 1;
		
	    // Define role names - currently there are 26 role names:
		String role_names = "abcdefghijklmnopqrstuvwxyz";
		
		// Get random number of vertices:
		int num_vertices_T = a + (int)(Math.random() * ((b - a) + 1));
		
		// Generate random vertex sets with labels:
		Map<Integer, LinkedList<String>> V = new HashMap<Integer, LinkedList<String>>();
		
		for (int x = 0 ; x < num_vertices_T ; x++) {
			LinkedList<String> label_v = new LinkedList<String>();
			
			label_v.add("A" + x);
			
			V.put(x, label_v);
		}
		
		// Generate random edge sets with labels:
		int num_labels_T = c + (int)(Math.random() * ((d - c) + 1));
		
		List<Character> E_labels = new LinkedList<Character>(); 
		for (int idx = 0 ; idx < num_labels_T ; idx++) {
			E_labels.add(role_names.charAt(idx));
		}
		
		Map<Pair<Integer, Integer>, Character> E = new HashMap<Pair<Integer, Integer>, Character>();
		
		List<Integer> num_random_parts = new LinkedList<Integer>();
		
		int x = V.size() - e;
		
		if (x == 0) {
			num_random_parts.add(0);
		}
		else {
			
			int randomNum = 1 + (int)(Math.random() * ((x - 1) + 1));
			
			num_random_parts.add(randomNum);
			
			x = Math.abs(x - num_random_parts.get(num_random_parts.size() - 1));
			
			while (x != 0) {
				
				int sum_random_parts = 0;
				for (Integer el : num_random_parts) {
					sum_random_parts += el;
				}
				
				int y = 1 + (int)(Math.random() * (((V.size() - sum_random_parts - 1) - 1) + 1));
				
				num_random_parts.add(y);
				
				x = Math.abs(x - y);
				
				if (x == 0) { break; }
			}
		}
		
		for (int i = 0 ; i < num_random_parts.size() ; i++) {
			
			int sum_num_random_parts_i = 0;
			for (int k = 0 ; k < num_random_parts.size() ; k++) {
				
				if (k == i) { break; }
				sum_num_random_parts_i += num_random_parts.get(k);
				
				
			}
			sum_num_random_parts_i++;
			
			int sum_num_random_parts_i1 = 0;
			for (int k = 0 ; k < num_random_parts.size() ; k++) {
				
				if (k == i + 1) { break; }
				
				sum_num_random_parts_i1 += num_random_parts.get(k);
				
				
			}
			sum_num_random_parts_i1++;
			
			for (int j = sum_num_random_parts_i ; j < sum_num_random_parts_i1 ; j++) {
				if (i != j) {
					
					int random_index = 0 + (int)(Math.random() * (((E_labels.size() - 1) - 0) + 1));
					
					E.put(new Pair<Integer, Integer>(i, j), E_labels.get(random_index));
				}
			}
		}
		
		for ( Entry<Pair<Integer, Integer>, Character> entry : E.entrySet()) {
			Pair<Integer, Integer> edge = entry.getKey();
			Character role = entry.getValue();
			
			LinkedList<String> Lv = V.get(edge.getValue0());
			LinkedList<String> Lu = V.get(edge.getValue1());
			
			T.addEdge(edge.getValue0(), edge.getValue1(), role, Lv, Lu);
		}
		
		return T;
	}
	
	public static Set<Character> signatureRoles(DescriptionTree T) {
		
		Set<Character> Sigma_T = new HashSet<Character>();
		
		for (Entry<Pair<Integer, Integer>, Character> entry : T.E.entrySet()) {
			Sigma_T.add(entry.getValue());
		}
		
		return Sigma_T;
	}
	
	public static Map<Integer, Integer> hypothesesLengths(Set<Set<String>> Hypotheses) {
		
		Map<Integer, Integer> lengthsHypotheses = new HashMap<>();
		
		for (Set<String> hypothesis : Hypotheses) {
			if (!lengthsHypotheses.containsKey(hypothesis.size())) {
				lengthsHypotheses.put(hypothesis.size(), 1);
			}
			else {
				
				
				int c = lengthsHypotheses.get(hypothesis.size());
				
				c++;
				
				lengthsHypotheses.put(hypothesis.size(), c);
				
			}
		}
		
		
		return lengthsHypotheses;
	}
	
	public static Triplet<Double, Double, Integer> hypothesesStats(Set<Set<String>> Hypotheses) {
		
		List<Integer> lengthsHypotheses = new LinkedList<Integer>();
		
		double median = 0;
		double avg = 0;
		int max = 0;
		
		for (Set<String> hypothesis : Hypotheses) {
			
			int h_cardinality = hypothesis.size();
			
			// median:
			lengthsHypotheses.add(h_cardinality);
			
			// average:
			avg += h_cardinality;
			
			// max:
			if (h_cardinality > max) {
				max = h_cardinality;
			}
		}
		
		// average:
		avg = avg / Hypotheses.size();
		
		// median:
		Collections.sort(lengthsHypotheses);
		if (lengthsHypotheses.size() % 2 == 0) {
			median = (((double) lengthsHypotheses.get((lengthsHypotheses.size() - 1) / 2)) + ((double) lengthsHypotheses.get((lengthsHypotheses.size() - 1) / 2 + 1))) / 2;
		}
		else {
			median = (double) lengthsHypotheses.get((lengthsHypotheses.size() - 1) / 2);
		}
		
		Triplet<Double, Double, Integer> statistics = Triplet.with(median, avg, max);
		
		return statistics;
	}
	
	public static double avgSizeHypotheses(Set<Set<String>> Hypotheses) {
		
		double avg = 0;
		
		for (Set<String> hypothesis : Hypotheses) {
			avg += hypothesis.size();
		}
		
		avg = avg / Hypotheses.size();
		
		return avg;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static String getRandomConceptDescription(int random_number_of_concepts, int random_number_of_roles) {
		
		String role_names = "abcdefghijklmnopqrstuvwxyz";
		
		String expr = "A0";
		
		
		
		String conjunct = "";
		
		int cnt_iterations = 0;
		
		while (cnt_iterations < random_number_of_concepts) {
			
			cnt_iterations++;
			
			double pick = Math.random();
			
			if (pick >= 0.5) {
				
				int random_role_idx = 1 + (int)(Math.random() * ((random_number_of_roles - 1) + 1));  //  min + (int)(Math.random() * ((max - min) + 1));
				
				char random_role = role_names.charAt(random_role_idx);
				
				conjunct += " and (" + random_role + " some (A" + cnt_iterations;
				
				
				
			} else {
				
				// count how many '(' and close them:
				int brackets = 0;
				for (int x = 0 ; x < conjunct.length() ; x++) { if (conjunct.charAt(x) == '(') { brackets++; } }
				
				for (int x = 0 ; x < brackets ; x++) {
					conjunct += ')';
				}
				
//				System.out.println("HERE " + conjunct);
				
				expr += conjunct;
				
				conjunct = "";
				
			}
			
//			System.out.println("===");
//			System.out.println("expr = " + expr);
//			System.out.println("===");
		}
		
		return expr;
	}
	
	/**
	 * Function that returns a list of class names and property names for a given input, in the form of a string, of an axiom.
	 * 
	 * @param input
	 * 
	 * @return <classWords, propertyWords>
	 */
	public static Pair<List<String>, List<String>> getClassAndPropertyNames(String input) {
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
		return new Pair<List<String>, List<String>>(classWords, propertyWords);
	}
	
	
	public static String tree2expr(DescriptionTree T, int root) {
		
//		System.out.println(T.tree);
		
		String expr = "";
		
		LinkedList<LinkedList<Integer>> paths = T.DFSCompletePaths(root);
		
//		System.out.println(paths);
		
		for (LinkedList<Integer> path : paths) {
			
			String conjunct = "";
			
			
			for (int i = 0 ; i < path.size() - 1 ; i++) {
				
				int node1_idx = path.get(i);
				int node2_idx = path.get(i + 1);
				
				DTNode node1 = T.V.get(node1_idx);
				DTNode node2 = T.V.get(node2_idx);
				
				conjunct += String.join(" and ", node1.label) + " and (" + T.E.get(new Pair<Integer, Integer>(node1_idx, node2_idx)) + " some (";
				
			}
			
			conjunct += String.join(" and ", T.V.get(path.get(path.size() - 1)).label);
			
			int cnt = 0;
			for (int x = 0 ; x < conjunct.length() ; x++) { if (conjunct.charAt(x) == '(') { cnt++; } }
			
			for (int x = 0 ; x < cnt ; x++) {
				conjunct += ')';
			}
			
			conjunct += " and ";
			
			expr += conjunct;
			
		}
		
		expr = expr.substring(0, expr.length() - 5);
		
		return expr;
		
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

	public static String getNonEntailmentAxiom(String C, String D) {
		
		String non_entailment_axiom = C + " SubClassOf " + D;
		
		return non_entailment_axiom;
	}
	
	/**
	 * Main function that runs the experiments.
	 * 
	 * @param 
	 * @return 
	 * 
	 * @throws OWLOntologyCreationException 
	 * @throws IOException 
	 * @throws OWLOntologyStorageException 
	 */
	public void runSyntheticExperiments() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
		
		InputStreamReader r=new InputStreamReader(System.in);  
	    BufferedReader br=new BufferedReader(r);  

	    System.out.println("Enter the complete path of the folder in which you want to save the ontologies generated as a result from the simulations: ");  
	    String filePath = br.readLine();  
	    System.out.println("Inputted path: " + filePath); 
		
	    System.out.println("Enter the name of the txt file in which you want to save the results from the simulations: ");  
	    String fileName = br.readLine();  
	    fileName += ".txt";
	    System.out.println("Inputted path: " + fileName); 
	    
	    // Simulations:
// 		int number_of_simulations = 250;
	 		
	    System.out.println("Enter the number of simulations (#AP)");  
	    int number_of_simulations = Integer.parseInt(br.readLine());  
	    System.out.println("#AP: " + number_of_simulations); 
	    
	    System.out.println("Enter the simulation parameters (in the format: a, b, c, d): ");  
	    String input_param = br.readLine();  
	    String[] input_param_arr = input_param.split(", ");
	    
	    int a = Integer.parseInt(input_param_arr[0]);
	    int b = Integer.parseInt(input_param_arr[1]);
	    int c = Integer.parseInt(input_param_arr[2]);
	    int d = Integer.parseInt(input_param_arr[3]);
		
		// Create manager for ontology:
		OWLOntologyManager manager = owlUtils.manager;
//		
//		// Get the data factory:
		OWLDataFactory dataFactory = owlUtils.createDataFactory();
		
		FileWriter myWriter = new FileWriter(filePath + "\\" + fileName);
		
		myWriter.write("ID,#C,#R,|V1|,|V2|,Average Branching Factor T1,Average Branching Factor T2,Sigma_R_T1,Sigma_R_T2,Sigma_R_T1 intersection Sigma_R_T2,#H,Average Size Hypotheses,Max Size Hypotheses,Median Size Hypotheses,t[s],T U H entails C SubClassOf D,Reasoner Check\n");
		
		int onto_counter = 0;
		
//		==============================================================================================================
		// Create IRI of ontology:
		onto_counter++;
		IRI ontologyIRI = IRI.create("http://create-hypothesis" + onto_counter + "#");
		
		// Create an empty ontology:
		OWLOntology ontology = manager.createOntology(ontologyIRI);
		
//		OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontology);
		
		// Get class and property names:
//		Pair<List<String>, List<String>> classAndPropertyNames = getClassAndPropertyNames(non_entailment);
		
//		List<String> classNames = classAndPropertyNames.getValue0();
//		List<String> propertyNames = classAndPropertyNames.getValue1();
		
		List<String> classNames = new LinkedList<String>(Arrays.asList("A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "A12", "A13", "A14", "A15", "A16", "A17", "A18", "A19", "A20", "A21", "A22", "A23", "A24", "A25", "A26", "A27", "A28", "A29", "A30", "A31", "A32", "A33", "A34", "A35", "A36", "A37", "A38", "A39", "A40", "A41"));
		
		List<String> propertyNames = new LinkedList<String>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
		
		List<String> visitedDeclaredClassNames = new LinkedList<String>();
		List<String> visitedDeclaredPropertyNames = new LinkedList<String>();
		
		// Declare class names:
		for (String className : classNames) {
			if (!visitedDeclaredClassNames.contains(className)) {
				OWLClass A = dataFactory.getOWLEntity(EntityType.CLASS, IRI.create(className));
				OWLDeclarationAxiom declareA = dataFactory.getOWLDeclarationAxiom(A);
				manager.addAxiom(ontology, declareA);
				visitedDeclaredClassNames.add(className);
			}
		}
		// Declare property names:
		for (String propertyName : propertyNames) {
			if (!visitedDeclaredPropertyNames.contains(propertyName)) {
				OWLObjectProperty property = dataFactory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(propertyName));
				OWLDeclarationAxiom declareProperty = dataFactory.getOWLDeclarationAxiom(property);
				manager.addAxiom(ontology, declareProperty);
				visitedDeclaredPropertyNames.add(propertyName);
			}
		}
		
		for (int i = 0 ; i < number_of_simulations ; i++) {
			
			System.out.println("Simulation " + i);
			
			int random_number_of_concepts = a + (int)(Math.random() * ((b - a) + 1));  //  min + (int)(Math.random() * ((max - min) + 1));
			
			int random_number_of_roles = c + (int)(Math.random() * ((d - c) + 1));  //  min + (int)(Math.random() * ((max - min) + 1));
			
			String LHS_concept = getRandomConceptDescription(random_number_of_concepts, random_number_of_roles);
			String RHS_concept = getRandomConceptDescription(random_number_of_concepts, random_number_of_roles);
			System.out.println("C = " + LHS_concept);
			System.out.println("D = " + RHS_concept);
			
			String non_entailment = LHS_concept + " SubClassOf " + RHS_concept;
			System.out.println("non_entailment = " + non_entailment);
			
			// Generate random description trees:
			DescriptionTree T1 = SyntheticExperimentsFunctions.expr2tree(LHS_concept, 'v');
			DescriptionTree T2 = SyntheticExperimentsFunctions.expr2tree(RHS_concept, 'w');
			System.out.println(T1.E);
			System.out.println(T2.E);
			System.out.println(T1.V);
			System.out.println(T2.V);
			
			// Find Sigma_T1 and Sigma_T2:
			Set<Character> Sigma_T1 = signatureRoles(T1);
			Set<Character> Sigma_T2 = signatureRoles(T2);
			
			// Find Sigma_T1 intersection Sigma_T2:
			Set<Character> intersection = new HashSet<Character>(Sigma_T1); // use the copy constructor
			intersection.retainAll(Sigma_T2);
			
			// Find the average branching factors of the trees:
			double b1_hat = round((T1.V.size() - 1) / (T1.tree.size() + 0.0001), 2);
			double b2_hat = round((T2.V.size() - 1) / (T2.tree.size() + 0.0001), 2);
			
			try {
				
				// Find isomorphisms and construct hypotheses (Call XeNON):
				long startTime = System.nanoTime();
				
				Set<Set<String>> Hypotheses = call_XeNON(T1, T2);
				
				long estimatedTime = System.nanoTime() - startTime;
				
				estimatedTime = TimeUnit.MILLISECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS);
				double estTime = (estimatedTime / 1000.0);  // in seconds
				
//				==============================================================================================================
				boolean check_entailment_T_union_H = true;
				double median = 0;
				double avg = 0;
				int max = 0;
				List<Integer> lengthsHypotheses = new LinkedList<Integer>();
				
				OWLAxiom non_entailment_axiom = owlUtils.parseClassExpression(ontology, manager, dataFactory, non_entailment);
				
//				==============================================================================================================
				boolean checkReason = true;
				
				for (Set<String> hypothesis : Hypotheses) {
					
					int h_cardinality = hypothesis.size();
					
					// median:
					lengthsHypotheses.add(h_cardinality);
					
					// average:
					avg += h_cardinality;
					
					// max:
					if (h_cardinality > max) {
						max = h_cardinality;
					}
					
//					==============================================================================================================
					// Perform a check if the hypotheses size is less than 200, otherwise it takes too long.
					if (Hypotheses.size() < 200) {
						Set<OWLAxiom> axiomsToRemove = new HashSet<OWLAxiom>();
						
						for (String alpha : hypothesis) {
							
							OWLAxiom alpha_axiom = owlUtils.parseClassExpression(ontology, manager, dataFactory, alpha);
							
							axiomsToRemove.add(alpha_axiom);
							
							ontology.add(alpha_axiom);
						}
						
						System.out.println("Onto axioms for a hypothesis : " + ontology.getAxioms());
				        
				        System.out.println("Onto axioms for a hypothesis (reasoned) : " + ontology.getAxioms());
						
				        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontology);

				        reasoner.precomputeInferences(InferenceType.values());

				        check_entailment_T_union_H = reasoner.isEntailed(non_entailment_axiom);
				        
				        reasoner.dispose();
						
						if (check_entailment_T_union_H == false) { 
							
							System.out.println("---");
							
							System.out.println("ONTO AXIOMS FALSE: " + ontology.getAxioms());
							
							System.out.println("hypothesis for which the expl failed : " + hypothesis);
							
							System.out.println("---");
							
							break; 
						}
						
						ontology.removeAxioms(axiomsToRemove);
						
						System.out.println("Onto axioms at the end : " + ontology.getAxioms());
					}
					else {
						checkReason = false;
					}
//					==============================================================================================================
					
				}
				
				// average:
				avg = avg / Hypotheses.size();
				
				// median:
				if (lengthsHypotheses.size() == 0) {
					lengthsHypotheses.add(1);
					median = 1;
					avg = 1;
					max = 1;
				}
				else {
					Collections.sort(lengthsHypotheses);
					if (lengthsHypotheses.size() % 2 == 0) {
						median = (((double) lengthsHypotheses.get((lengthsHypotheses.size() - 1) / 2)) + ((double) lengthsHypotheses.get((lengthsHypotheses.size() - 1) / 2 + 1))) / 2;
					}
					else {
						median = (double) lengthsHypotheses.get((lengthsHypotheses.size() - 1) / 2);
					}
				}
				
				
				String output = random_number_of_concepts + "," 
								+ random_number_of_roles + "," 
								+ T1.V.size() + "," 
								+ T2.V.size() + "," 
								+ b1_hat + "," 
								+ b2_hat + "," 
								+ Sigma_T1.size() + "," 
								+ Sigma_T2.size() + "," 
								+ intersection.size() + "," 
								+ round(lengthsHypotheses.size(), 3) + "," 
								+ avg + "," 
								+ max + "," 
								+ median + ","
								+ estTime + "," 
								+ check_entailment_T_union_H + "," 
								+ checkReason;
				
				myWriter.write("Simulation " + i + "," + output + "\n");
				
			} catch (OutOfMemoryError e) {
				
				String output1 = random_number_of_concepts + "," 
								+ random_number_of_roles + "," 
								+ T1.V.size() + "," 
								+ T2.V.size() + "," 
								+ b1_hat + "," 
								+ b2_hat + "," 
								+ Sigma_T1.size() + ","
								+ Sigma_T2.size() + "," 
								+ intersection.size() + "," 
								+ "timeout" + "," 
								+ "timeout" + "," 
								+ "timeout" + ","
								+ "timeout" + "," 
								+ "timeout" + "," 
								+ "no check" + "," 
								+ "false";
				
				myWriter.write("Simulation " + i + "," + output1 + "\n");
			}

		}
		myWriter.close();
	}
	
}






