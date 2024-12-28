package Trees;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.IntStream;

import org.javatuples.Pair;

import Utils.Functions;

public class DescriptionTree {
	/**
    Class Attributes : 
        None
        
    Instance Attributes : 
        vertex_notation : String
        tree : HashMap<Integer, LinkedList<Integer>>
        V : HashMap<Integer, String>
        inv_V : HashMap<String, Integer>
        E : HashMap<int[], String>
        height : Integer, default=-1
        
    Methods : 
        addEdge(v, u) 
             - adds an edge between vertices v and u.

        DFSUtilCompletePaths(v, visited, path, paths) 
            - A utilities function that DFSCompletePaths uses to find the set of all complete paths in the tree.

        DFSCompletePaths()
            - A Depth First Search to find the set of all complete paths in the tree. 
	 */
	
	/**
	 * Class attributes of class {@link DescriptionTree}
	 * 
	 */
	public char vertex_notation;
	public Map<Integer, LinkedList<Integer>> tree = new HashMap<Integer, LinkedList<Integer>>();
	public Map<Integer, DTNode> V = new HashMap<Integer, DTNode>();
	public Map<String, Integer> inv_V = new HashMap<String, Integer>();
	public Map<Pair<Integer, Integer>, Character> E = new HashMap<Pair<Integer, Integer>, Character>();
	public int height = -1;
	
	/**
	 * Constructor for class {@link DescriptionTree}
	 * 
	 * @param vertex_notation
	 * 
	 * @return 
	 */
	public DescriptionTree(char vertex_notation) {
		this.vertex_notation = vertex_notation;
	}
	
	// ====================================================================== Tree Functions ======================================================================
	
	/**
	 * Function for adding a new edge in a tree.
	 * 
	 * @param v
	 * @param u
	 * @param edgeLabel
	 * @param label_v
	 * @param label_u
	 * 
	 * @return 
	 */
	public void addEdge(int v, int u, char edgeLabel, LinkedList<String> label_v, LinkedList<String> label_u) {
		if (this.tree.containsKey(v) == false) {
			LinkedList<Integer> x = new LinkedList<Integer>();
			x.add(u);
			this.tree.put(v, x);
		} else {
			this.tree.get(v).add(u);
		}
		
		Pair<Integer, Integer> key_to_check = new Pair<Integer, Integer>(v, u);
		
		if (this.E.containsKey(key_to_check) == false) {
			this.E.put(key_to_check, edgeLabel);
		}
		
		if (V.containsKey(v) == false) {
			String vertexName = Character.toString((char) this.vertex_notation) + v; 
			DTNode n_v = new DTNode(vertexName, label_v);
			
			this.V.put(v, n_v);
			this.inv_V.put(n_v.name, v);
			
		}
		
		if (V.containsKey(u) == false) {
			String vertexName = Character.toString((char) this.vertex_notation) + u; 
			DTNode n_u = new DTNode(vertexName, label_u);
			
			this.V.put(u, n_u);
			this.inv_V.put(n_u.name, u);
			
		}
	}
	
	// ====================================================================== Adjacency Matrices (different versions) ======================================================================
	
	/**
	 * Function for generating a Map which represents Pairs <edge, label>. It imitates an adjacency matrix.
	 * 
	 * @param 
	 * 
	 * @return M
	 */
	public Map<Pair<String, String>, Character> adjacency_matrix_v1() {
		// Define the adjacency matrix A:
		char[][] A = new char[this.V.size()][this.V.size()];
		
		// Initialize the adjacency matrix A (all values to 0):
		for (int i = 0; i < A.length; i++) { for (int j = 0; j < A.length; j++) { A[i][j] = '0'; } }
		
		// Assign the edge values in the matrix:
		for (Entry<Pair<Integer, Integer>, Character> entry : this.E.entrySet()) {
			
			Pair<Integer, Integer> e = entry.getKey();
			char label = entry.getValue();
		    
		    A[e.getValue0()][e.getValue1()] = label;
		}
		
		// Make the matrix a hashmap and imitate a dataframe.
		Map<Pair<String, String>, Character> M = new HashMap<Pair<String, String>, Character>();
		
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A.length; j++) {
				String x = Character.toString((char) this.vertex_notation) + i; 
				String y = Character.toString((char) this.vertex_notation) + j;
				
				Pair<String, String> edgePair = new Pair<String, String>(x, y);
				
				M.put(edgePair, A[i][j]);
			}
		}
		
		Functions.printMap(M);
		
		return M;
	}
	
	/**
	 * Function for generating the adjacency matrix.
	 * 
	 * @param 
	 * 
	 * @return A
	 */
	public char[][] adjacency_matrix_v2() {
		// Define the adjacency matrix A:
		char[][] A = new char[this.V.size()][this.V.size()];
		
		// Initialize the adjacency matrix A (all values to 0):
		for (int i = 0; i < A.length; i++) { for (int j = 0; j < A.length; j++) { A[i][j] = '0'; } }
		
		for (Entry<Pair<Integer, Integer>, Character> entry : this.E.entrySet()) {
			Pair<Integer, Integer> e = entry.getKey();
			char label = entry.getValue();
		    A[e.getValue0()][e.getValue1()] = label;
		}
		return A;
	}
	
	public int[][] adjacencyMatrix() {
		// Define the adjacency matrix A:
		int[][] A = new int[this.V.size()][this.V.size()];
		
		// Initialize the adjacency matrix A (all values to 0):
		for (int i = 0; i < A.length; i++) { for (int j = 0; j < A.length; j++) { A[i][j] = 0; } }
		
		for (Entry<Pair<Integer, Integer>, Character> entry : this.E.entrySet()) {
			Pair<Integer, Integer> e = entry.getKey();
		    
		    A[e.getValue0()][e.getValue1()] = 1;
		}
		
		return A;
	}
	
	// ====================================================================== DFS Algorithm for Finding All Complete Paths ======================================================================
	
	/**
	 * Util function for DFSCompletePaths function.
	 * 
	 * @param v
	 * @param visited
	 * @param path
	 * @param paths
	 * 
	 * @return 
	 */
	public void DFSUtilCompletePaths(int v, Map<Integer, Boolean> visited, LinkedList<Integer> path, LinkedList<LinkedList<Integer>> paths) {
		visited.put(v, true);
		
		path.add(v);
		
		if (this.tree.containsKey(v) == false) {
			LinkedList<Integer> temp_path = new LinkedList<Integer>();
			for (Integer x : path) {
				temp_path.add(x);
			}
			paths.add(temp_path);
		} else {
			for (Integer neighbor : this.tree.get(v)) {
				if (visited.get(neighbor) == false) {
					this.DFSUtilCompletePaths(neighbor, visited, path, paths);
				}
			}
		}
		
		path.removeLast();
		visited.put(v, false);
		
	}
	
	/**
	 * Depth First Search Algorithm for finding all complete paths of a tree.
	 * 
	 * @param v
	 * 
	 * @return paths
	 */
	public LinkedList<LinkedList<Integer>> DFSCompletePaths(Integer v) {
		Map<Integer, Boolean> visited = new HashMap<Integer, Boolean>();
		for (Integer vertex : this.V.keySet()) { visited.put(vertex, false); }
		
		LinkedList<LinkedList<Integer>> paths = new LinkedList<LinkedList<Integer>>();
		
		LinkedList<Integer> pathList = new LinkedList<Integer>();
		
		this.DFSUtilCompletePaths(v, visited, pathList, paths);
		
		LinkedList<Integer> sizesPaths = new LinkedList<Integer>();
		
		for (LinkedList<Integer> x : paths) { sizesPaths.add(x.size()); }
		
		this.height = sizesPaths.get(0);
		
		for (int i = 1; i < sizesPaths.size(); i++) { if (sizesPaths.get(i) > this.height) { this.height = sizesPaths.get(i); } }
		
		return paths;
	}
	
	/**
	 * Function for finding all paths in an adjacency matrix.
	 * 
	 * @param A
	 * 
	 * @return paths
	 */
	public List<List<Integer>> pathfinder(int[][] A) {
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
	
	public List<List<Integer>> setOfAllCompletePaths() {
		
		int[][] A = this.adjacencyMatrix();
		
		List<List<Integer>> paths = this.pathfinder(A);
		
		List<List<Integer>> completePaths = new LinkedList<List<Integer>>();
		
		for (List<Integer> path : paths) {
			int vertex = path.get(path.size() - 1);
			
			int sum = IntStream.of(A[vertex]).sum();
			
			if (sum == 0) { completePaths.add(path); }
		}
		
		return completePaths;
	}
	
	// ====================================================================== Functions for Printing and Displaying Output ======================================================================
	
	/**
	 * Function for displaying the tree in console.
	 * 
	 * @param x
	 * @param flag
	 * @param depth
	 * @param isLast
	 * 
	 * @return 
	 */
	public void printNTree(Integer x, boolean[] flag, int depth, boolean isLast) {
		if (x == null)
	        return;
		
		// Loop to print the depths of the
	    // current node
	    for (int i = 1; i < depth; ++i) {
	         
	        // Condition when the depth
	        // is exploring
	        if (flag[i] == true) {
	            System.out.print("| "
	               + " "
	               + " "
	               + " ");
	        }
	         
	        // Otherwise print
	        // the blank spaces
	        else {
	            System.out.print(" "
	               + " "
	               + " "
	               + " ");
	        }
	    }
	     
	    // Condition when the current
	    // node is the root node
	    if (depth == 0)
//	        System.out.println(Functions.clearStringPrefixes(x.name.toString()));
	    	System.out.print(Character.toString((char) this.vertex_notation) + x + '\n');
	     
	    // Condition when the node is
	    // the last node of
	    // the exploring depth
	    else if (isLast) {
//	        System.out.print("+--- " +  Functions.clearStringPrefixes(x.name.toString()) + '\n');
	    	System.out.print("+--- " + Character.toString((char) this.vertex_notation) + x + '\n');
	    	
	        // No more children turn it
	        // to the non-exploring depth
	        flag[depth] = false;
	    }
	    else {
//	        System.out.print("+--- " +  Functions.clearStringPrefixes(x.name.toString()) + '\n');
	    	System.out.print("+--- " + Character.toString((char) this.vertex_notation) + x + '\n');
	    }
	    
	    int it = 0;
	    for ( Integer node1 : this.N(x) ) {
	         ++it;
	        
	        // Recursive call for the
	        // children nodes
	        printNTree((Integer) node1, flag, depth + 1,
	            it == (this.N(x).size()) - 1);
	    }
	    flag[depth] = true;
	}
	
	/**
	 * Function for displaying characteristics of the tree.
	 * 
	 * @param 
	 * 
	 * @return 
	 */
	public void displayTree() {
		System.out.println("V = ");
		Functions.printV(this.V);
		System.out.println();
		System.out.println("inv_V = ");
		Functions.printMap(this.inv_V);
		System.out.println();
		System.out.println("E = ");
		Functions.printMap(this.E);
		System.out.println();
		System.out.println("tree = ");
		Functions.printMap(this.tree);
	}
	
	// ====================================================================== Definitions and Formulae for the Method ====================================================================== 
	
	/**
	 * For tree T, T.N(v) := {y in T.V | <x, y> in T.E}.
	 * 
	 * @param v
	 * 
	 * @return N(v)
	 */
	public Set<Integer> N(int v) {
		
		if (this.tree.containsKey(v)) {
			Set<Integer> N = new HashSet<Integer>(this.tree.get(v));
			
			return N;
		} else {
			Set<Integer> N = new HashSet<Integer>();
			
			return N;
		}
	}
	
	/**
	 * For set of neighbors N(v) of node v in T, find N(v)/~ wrt the equivalence relation:
	 * 
	 * @param neighbors N(v)
	 * @param node x
	 * 
	 * @return N(v)/~
	 */
	public Set<Set<Integer>> N_equivClasses(Set<Integer> neighbors, int x) {
		Set<Set<Integer>> N_equiv = new HashSet<Set<Integer>>();
		
		// for all y in N(x)
		for (Integer y : neighbors) {
			
			// [y] in N/~(x)
			Set<Integer> equivalenceClass = new HashSet<Integer>();
			equivalenceClass.add(y);
			
			// for all z in N(x)
			for (Integer z : neighbors) {
				
				// <x, y> in E1, <x, z> in E2 
				Pair<Integer, Integer> e1 = new Pair<Integer, Integer>(x, y);
				Pair<Integer, Integer> e2 = new Pair<Integer, Integer>(x, z);
				
				// iff xi(<x, y>) == xi(<x, z>)   =>    z in [y]
				if (this.E.get(e1) == this.E.get(e2)) { equivalenceClass.add(z); }
			}
			
			// [y] in N(x)/~
			N_equiv.add(equivalenceClass);
		}
		// N(x)/~
		return N_equiv;
	}
	
	/**
	 * For set of neighbors N(v) of node v in T, find N_eq(v) wrt the equivalence relation:
	 * 
	 * @param v
	 * 
	 * @return N(v)
	 */
	public void R(Set<Integer> neighbors) {
		// TODO Auto-generated method stub
		
	}
	
}













