package Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import org.javatuples.Pair;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;

import OWLUtils.owlUtils;
import Trees.DTNode;
import Trees.DescriptionTree;

public class Functions {
	
	/**
	 * Function that prints a map.
	 * 
	 * @param map
	 * 
	 * @return None
	 */
	public static <K, V> void printMap(Map<K, V> map) {
		for ( Entry<K, V> entry : map.entrySet() ) {
		    System.out.println(entry.getKey().toString()
		    		.replaceAll(owlUtils.prefix1, "")
		    		.replaceAll("http://www.co-ode.org/ontologies/pizza/pizza.owl#", "")
		    		.replaceAll("http://www.w3.org/2004/02/skos/core#", "")
		    		.replaceAll("http://www.co-ode.org/ontologies/pizza#", "")
		    		.replaceAll("http://www.semanticweb.org/goche/ontologies/2021/9/untitled-ontology-6#", "")
		    		+ " --> " + entry.getValue());
		}
	}
	
	/**
	 * Function that prints a map that contains isomorphisms.
	 * 
	 * @param map
	 * 
	 * @return None
	 */
	public static <K, V> void printIsomorphisms(Map<Integer, List<Pair<DTNode, DTNode>>> map) {
		for ( Entry<Integer, List<Pair<DTNode, DTNode>>> entry : map.entrySet() ) {
			System.out.print(entry.getKey().toString() + " --> ");
		    for (Pair<DTNode, DTNode> currentMap : entry.getValue()) {
		    	System.out.print("(" + currentMap.getValue0().name + currentMap.getValue1().name + ")");
			}
		    System.out.println();
		}
	}
	
	/**
	 * Function that prints a map.
	 * 
	 * @param map
	 * 
	 * @return None
	 */
	public static <K, V> void printV(Map<Integer, DTNode> map) {
		for ( Entry<Integer, DTNode> entry : map.entrySet() ) {
		    System.out.println(entry.getKey().toString()
		    		.replaceAll(owlUtils.prefix1, "")
		    		.replaceAll("http://www.co-ode.org/ontologies/pizza/pizza.owl#", "")
		    		.replaceAll("http://www.w3.org/2004/02/skos/core#", "")
		    		.replaceAll("http://www.co-ode.org/ontologies/pizza#", "")
		    		.replaceAll("http://www.semanticweb.org/goche/ontologies/2021/9/untitled-ontology-6#", "")
		    		+ " --> " + entry.getValue().name);
		}
	}
	
	/**
	 * Function that prints a matrix that contains integers.
	 * 
	 * @param matrix
	 * 
	 * @return None
	 */
	public static <K, V> void printMatrix(int[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Function that prints a matrix that contains characters.
	 * 
	 * @param m
	 * 
	 * @return None
	 */
	public static <K, V> void printMatrix(char[][] matrix) {
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Function that prints a set in new lines.
	 * 
	 * @param setToPrint
	 * 
	 * @return None
	 */
	public static <T> void printSet(Set<T> setToPrint) {
		for (T element : setToPrint) {
			System.out.println(element.toString().replaceAll(owlUtils.prefix1, "").replaceAll("http://www.co-ode.org/ontologies/pizza/pizza.owl#", "")
					.replaceAll("http://www.w3.org/2004/02/skos/core#", "")
					.replaceAll("http://www.co-ode.org/ontologies/pizza#", "")
					.replaceAll("http://www.semanticweb.org/goche/ontologies/2021/9/untitled-ontology-6#", ""));
		}
	}
	
	/**
	 * Function that prints a list in new lines.
	 * 
	 * @param listToPrint
	 * 
	 * @return None
	 */
	public static <T> void printList(List<T> listToPrint) {
		for (T element : listToPrint) {
			System.out.println(element.toString().replaceAll(owlUtils.prefix1, "").replaceAll("http://www.co-ode.org/ontologies/pizza/pizza.owl#", "")
					.replaceAll("http://www.w3.org/2004/02/skos/core#", "")
					.replaceAll("http://www.co-ode.org/ontologies/pizza#", "")
					.replaceAll("http://www.semanticweb.org/goche/ontologies/2021/9/untitled-ontology-6#", ""));
		}
	}
	
	/**
	 * Function that prints a set of OWL axioms.
	 * 
	 * @param axioms
	 * 
	 * @return None
	 */
	public static void printAxioms(Set<OWLAxiom> axioms) {
		for (OWLAxiom ax : axioms) {
	    	System.out.println(ax.toString().replaceAll("http://www.co-ode.org/ontologies/pizza/pizza.owl#", "")
	    									.replaceAll("http://www.w3.org/2004/02/skos/core#", "")
	    									.replaceAll("http://www.co-ode.org/ontologies/pizza#", "")
	    									.replaceAll("http://www.semanticweb.org/goche/ontologies/2021/9/untitled-ontology-6#", ""));
		}
	}
	
	/**
	 * Function that prints a set of OWL class expressions.
	 * 
	 * @param s
	 * 
	 * @return None
	 */
	public static void printSetOfClassExpressions(Set<OWLClassExpression> s) {
		for (OWLClassExpression ax : s) {
	    	System.out.println(ax.toString().replaceAll(owlUtils.prefix1, "").replaceAll("http://www.co-ode.org/ontologies/pizza/pizza.owl#", "")
	    									.replaceAll("http://www.w3.org/2004/02/skos/core#", "")
	    									.replaceAll("http://www.co-ode.org/ontologies/pizza#", "")
	    									.replaceAll("http://www.semanticweb.org/goche/ontologies/2021/9/untitled-ontology-6#", ""));
		}
	}
	
	/**
	 * Function that prints a set of OWL equivalent classes axioms.
	 * 
	 * @param equivalentClasses
	 * 
	 * @return None
	 */
	public static void printSetOfEquivalentClassesAxioms(Set<OWLEquivalentClassesAxiom> equivalentClasses) {
		for (OWLAxiom ax : equivalentClasses) {
	    	System.out.println(ax.toString().replaceAll(owlUtils.prefix1, "").replaceAll("http://www.co-ode.org/ontologies/pizza/pizza.owl#", "")
	    									.replaceAll("http://www.w3.org/2004/02/skos/core#", "")
	    									.replaceAll("http://www.co-ode.org/ontologies/pizza#", "")
	    									.replaceAll("http://www.semanticweb.org/goche/ontologies/2021/9/untitled-ontology-6#", ""));
		}
	}
	
	/**
	 * Function that returns the n-th element of a set with elements OWL equivalent classes axioms.
	 * 
	 * @param s
	 * @param n
	 * 
	 * @return element
	 */
	public static <T> T getNthElementOfSet(Set<T> s, int n) {
		Iterator<T> it = s.iterator();
		int i = 0;
		
		while( it.hasNext() ) {
			T element = it.next();
	        if ( i == n ) {
	        	return element;
	        }
	        i++;
	    }
		
		return null;
	}
	
	/**
	 * Function that returns a String without ontology prefixes (for neat presentation).
	 * 
	 * @param concept_name_HVP
	 * 
	 * @return concept_name_HVP
	 */
	public static String clearPrefixes(OWLClassExpression concept_name_HVP) {
		return concept_name_HVP.toString().replaceAll(owlUtils.prefix1, "").replaceAll("http://www.co-ode.org/ontologies/pizza/pizza.owl#", "")
				.replaceAll("http://www.w3.org/2004/02/skos/core#", "")
				.replaceAll("http://www.co-ode.org/ontologies/pizza#", "")
				.replaceAll("http://www.semanticweb.org/goche/ontologies/2021/9/untitled-ontology-6#", "");
	}
	
	/**
	 * Function that returns a String without ontology prefixes (for neat presentation).
	 * 
	 * @param s
	 * 
	 * @return s
	 */
	public static String clearStringPrefixes(String s) {
		return s.replaceAll(owlUtils.prefix1, "").replaceAll("http://www.co-ode.org/ontologies/pizza/pizza.owl#", "")
				.replaceAll("http://www.w3.org/2004/02/skos/core#", "")
				.replaceAll("http://www.co-ode.org/ontologies/pizza#", "")
				.replaceAll("http://www.semanticweb.org/goche/ontologies/2021/9/untitled-ontology-6#", "");
	}
	
	/**
	 * Function that transfers elements from a set to a list.
	 * 
	 * @param s
	 * 
	 * @return l
	 */
	public static <T> List<T> set2list(Set<T> s) {
		List<T> l = new LinkedList<T>();
		
		for (T element : s) {
			l.add(element);
		}
		
		return l;
	}
	
	/**
	 * Function that transfers elements from a list to a set.
	 * 
	 * @param List<T> l
	 * 
	 * @return Set<T> s
	 */
	public static <T> Set<T> list2set(List<T> l) {
		Set<T> s = new HashSet<T>();
		
		for (T element : l) {
			s.add(element);
		}
		
		return s;
	}
	
	/**
	 * Function for displaying characteristics of two description trees T1 and T2.
	 * 
	 * @param DescriptionTree T1
	 * @param DescriptionTree T2
	 * 
	 * @return None
	 */
	public static void displayTree(DescriptionTree T1, DescriptionTree T2) {
		LinkedList<LinkedList<Integer>> paths_T1 = T1.DFSCompletePaths(0);
	    LinkedList<LinkedList<Integer>> paths_T2 = T2.DFSCompletePaths(0);
	    
	    System.out.println("========== T1 ==========");
	    T1.displayTree();
	    System.out.println("========== T2 ==========");
	    T2.displayTree();
	    System.out.println("==========");
	    System.out.println("h(T1) = " + T1.height);
	    System.out.println("h(T2) = " + T2.height);
	    System.out.println("==========");
	    Functions.printList(paths_T1);
	    System.out.println("-------");
	    Functions.printList(paths_T2);
	    System.out.println("==========");
	}
	
	/**
	 * Function for displaying a tree in the console.
	 * 
	 * @param DescriptionTree T
	 * 
	 * @return 
	 */
	public static void displayTree(DescriptionTree T) {
		boolean[] flag = new boolean[T.V.size()];
		for (int i = 0; i < flag.length; i++) { flag[i] = true; }
		
		T.printNTree(0, flag , 0, false);
	}
	
	/**
	 * Function that returns the adjacency matrix of a tree.
	 * 
	 * @param nodesMap
	 * @param edgeList
	 * 
	 * @return A
	 */
	public static int[][] adjacencyMatrix(Map<Integer, List<Pair<Integer, Integer>>> nodesMap, List<Pair<Integer, Integer>> edgeList) {
		// Define the adjacency matrix A:
		
		int[][] A = new int[nodesMap.keySet().size()][nodesMap.keySet().size()];
		
		// Initialize the adjacency matrix A (all values to 0):
		for (int i = 0; i < A.length; i++) { for (int j = 0; j < A.length; j++) { A[i][j] = 0; } }
		
		for (Pair<Integer, Integer> e : edgeList) { A[e.getValue0()][e.getValue1()] = 1; }
		
		return A;
	}
	
	/**
	 * Function that returns the set of all paths in a tree using the adjacency matrix of the tree.
	 * 
	 * @param A
	 * 
	 * @return paths
	 */
	public static List<List<Integer>> pathfinder(int[][] A) {
		Queue<Integer> indexes = new PriorityQueue<Integer>();
		List<List<Integer>> paths = new LinkedList<List<Integer>>();
		
		for (int j = 0 ; j < A.length ; j++) {
			if (A[0][j] == 1) {
				indexes.add(j);
				
				List<Integer> tempPath = new LinkedList<Integer>();
				tempPath.add(0);
				tempPath.add(j);
				
				paths.add(tempPath);
			}
		}
		
		while (!indexes.isEmpty()) {
			
			int idx = indexes.poll();
			
			if (idx >= A.length) { continue; }
			
			for (int k = 0 ; k < A[idx].length ; k++) {
				if (A[idx][k] == 1) {
					
					int i = 0;
					
					List<List<Integer>> tempPaths = new LinkedList<List<Integer>>(paths);
					
					while (i < tempPaths.size()) {
						if (idx == tempPaths.get(i).get(tempPaths.get(i).size() - 1)) {
							
							List<Integer> temp = new LinkedList<Integer>(paths.get(i));
							temp.add(k);
							paths.add(temp);
						}
						i += 1;
					}
					indexes.add(k);
				}
			}
		}
		return paths;
	}
	
	/**
	 * Function that returns the set of all complete paths in a tree using the adjacency matrix of the tree.
	 * 
	 * @param A
	 * 
	 * @return None
	 */
	public static List<List<Integer>> setOfAllCompletePaths(int[][] A) {
			
			List<List<Integer>> paths = pathfinder(A);
			
			List<List<Integer>> completePaths = new LinkedList<List<Integer>>();
			
			for (List<Integer> path : paths) {
				int vertex = path.get(path.size() - 1);
				
				int sum = IntStream.of(A[vertex]).sum();
				
				if (sum == 0) { completePaths.add(path); }
			}
			
			return completePaths;
	}
	
	/**
	 * Function that defines and initializes a map of the representatives of a set of roles for a given tree.
	 * 
	 * @return representatives
	 */
	public static Map<String, Character> defineRepresentatives(String lhs_expr, String rhs_expr) {
		// Get roles from inputted expression:
		List<String> lhs_exprRoles = owlUtils.getSignatureFromExpr(lhs_expr).getValue1();
	    List<String> rhs_exprRoles = owlUtils.getSignatureFromExpr(rhs_expr).getValue1();
	    
	    // Construct the list of roles from the expressions (union of the signature of roles):
	    List<String> exprRoles = new LinkedList<String>(lhs_exprRoles);
	    for (String rhs_exprRole : rhs_exprRoles) { if (!exprRoles.contains(rhs_exprRole)) { exprRoles.add(rhs_exprRole); } }
		
	    // Define the representatives map:
		Map<String, Character> representatives = new HashMap<String, Character>();
		
		// Fill in the representatives map:
		for (String inputString : exprRoles) { representatives = Functions.getRepresentativeCharFromString(inputString, representatives); }
		
		// Return the representatives:
		return representatives; 
	}
	
	/**
	 * Function that takes a character from a string and makes it a representative to be used as an edge label in the method.
	 *  
	 * @param inputString
	 * @param representatives
	 * 
	 * @return representatives
	 */
	public static Map<String, Character> getRepresentativeCharFromString(String inputString, Map<String, Character> representatives) {
		
		if (representatives.containsKey(inputString)) { return representatives; }
		
		for (int i=0 ; i < inputString.length() ; i++) {
			char currentChar = inputString.charAt(i);
			
			if (!representatives.containsValue(currentChar)) { representatives.put(inputString, currentChar); return representatives; }
		}
		
		return representatives;
	}
	
	/**
	 * Function that returns a random element from a set:
	 * 
	 * @param set
	 * 
	 * @return element
	 */
	public static <T> T getByRandomClass(Set<T> set) {
	    if (set == null || set.isEmpty()) {
	        throw new IllegalArgumentException("The Set cannot be empty.");
	    }
	    int randomIndex = new Random().nextInt(set.size());
	    int i = 0;
	    for (T element : set) {
	        if (i == randomIndex) {
	            return element;
	        }
	        i++;
	    }
	    throw new IllegalStateException("Something went wrong while picking a random element.");
	}
	
	/**
	 * Function that clears the console.
	 * 
	 */
	public final static void clearConsole()
	{ 
		    System.out.print("\033[H\033[2J");   
		    System.out.flush();   
	}
	
	/**
	 * Function that clears the screen.
	 * 
	 */
	@SuppressWarnings("deprecation")
	public static void clrscr(){
	    //Clears Screen in java
	    try {
	        if (System.getProperty("os.name").contains("Windows"))
	            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	        else
	            Runtime.getRuntime().exec("clear");
	    } catch (IOException | InterruptedException ex) {}
	}
	
	/**
	  * Advanced trim function for strings.
	  * 
	  * @param value
	  * @return
	  */
	 public static String cleanExpr(String value) {
		 
		 return value.replace("\n", "").replace("\r", "").trim().replaceAll(" +", " ");
	}
	 
	 /**
	  * Function that rounds a number (ceil)
	  * @param value
	  * @param places
	  * @return
	  */
	 public static double round(double value, int places) {
		 if (places < 0) throw new IllegalArgumentException();
		 BigDecimal bd = BigDecimal.valueOf(value);
		 bd = bd.setScale(places, RoundingMode.HALF_UP);
		 return bd.doubleValue();
	 }
	 
	 /**
	  * Function that returns the median of a list of integers.
	  * 
	  * @param inputList
	  * @return
	  */
	 public static double median(List<Integer> inputList) {
		 double median = 0;
		Collections.sort(inputList);
		if (inputList.size() % 2 == 0) {
			median = (((double) inputList.get((inputList.size() - 1) / 2)) + ((double) inputList.get((inputList.size() - 1) / 2 + 1))) / 2;
		}
		else {
			median = (double) inputList.get((inputList.size() - 1) / 2);
		}
		
		return median;
	 }
}











