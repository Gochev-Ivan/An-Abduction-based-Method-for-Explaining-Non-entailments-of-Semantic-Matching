package com.xai.methods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.javatuples.Pair;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import Trees.DTNode;
import Trees.DescriptionTree;
import Utils.Functions;

public class XeNON {
	
	public DescriptionTree T1;
	public DescriptionTree T2;
	
	/**
	 * Constructor for class {@link XeNON}
	 * 
	 * @param T1
	 * @param T2
	 * 
	 * @return 
	 */
	public XeNON(DescriptionTree T1, DescriptionTree T2) {
		this.T1 = T1;
		this.T2 = T2;
	}
	
	/**
	 * R SubClassOf N(v0)/~ x N(w0)/~.
	 * 
	 * @param v
	 * @param w
	 * @param N_equivClasses_1
	 * @param N_equivClasses_2
	 * 
	 * @return R
	 */
	public Set<Pair<Set<Integer>, Set<Integer>>> R(DescriptionTree T1, DescriptionTree T2, int v, int w, Set<Set<Integer>> N_equivClasses_1, Set<Set<Integer>> N_equivClasses_2) {
		
		Set<Pair<Set<Integer>, Set<Integer>>> R_vw = new HashSet<Pair<Set<Integer>, Set<Integer>>>(); 
		
		// for all [x] in N(v)/~
		for (Set<Integer> x_equivClass : N_equivClasses_1) {
			
			// for all [y] in N(w)/~
			for (Set<Integer> y_equivClass : N_equivClasses_2) {
				
				// x in [x], y in [y]
				int x = -1;
				int y = -1;
				for (Integer a : x_equivClass) { x = a; break; }
				for (Integer b : y_equivClass) { y = b; break; }
				
				// <v, x> in E1, <w, y> in E2 
				Pair<Integer, Integer> e1 = new Pair<Integer, Integer>(v, x);
				Pair<Integer, Integer> e2 = new Pair<Integer, Integer>(w, y);
				
				// iff xi(<v, x>) == xi(<w, y>) => <[x], [y]> in R
				if (T1.E.get(e1) == T2.E.get(e2)) { 
					Pair<Set<Integer>, Set<Integer>> pairOfEquivalenceClasses = new Pair<Set<Integer>, Set<Integer>>(x_equivClass, y_equivClass); 
					R_vw.add(pairOfEquivalenceClasses); 
				}
			}
		}
		// R
		return R_vw;
	}
	
	/**
	 * Util function for finding all permutations of length k.
	 * 
	 * @param p
	 * @param i
	 * @param k
	 * @param permutations
	 * 
	 * @return 
	 */
	public void nPkUtil(List<Integer> p, int i, int k, LinkedList<List<Integer>> permutations) {
		if (i == k) {
			List<Integer> subListToAdd = new LinkedList<Integer>(p.subList(0, k));
			permutations.add(subListToAdd);
			return ;
		}
		
		for (int j = i ; j < p.size() ; j++) {
			Collections.swap(p, i, j);
			this.nPkUtil(p, i + 1, k, permutations);
			Collections.swap(p, i, j);
		}
	}
	
	/**
	 * Function for finding all permutations of length k.
	 * 
	 * @param p
	 * @param i
	 * @param k
	 * 
	 * @return permutations
	 */
	public LinkedList<List<Integer>> nPk(List<Integer> p, int i, int k) {
		LinkedList<List<Integer>> permutations = new LinkedList<List<Integer>>();
		this.nPkUtil(p, i, k, permutations);
		return permutations;
	}
	
	/**
	 * Map [x] -> [y].
	 * 
	 * @param r
	 * 
	 * @return [x] -> [y]
	 */
	public List<List<Pair<Integer, Integer>>> mapEquivalenceClasses(Pair<Set<Integer>, Set<Integer>> r) {
		
		List<List<Pair<Integer, Integer>>> mappings = new LinkedList<List<Pair<Integer, Integer>>>();
		
		List<Integer> x_equivClass = new LinkedList<Integer>(r.getValue0());  // [x]
		List<Integer> y_equivClass = new LinkedList<Integer>(r.getValue1());  // [y]
		
		if (x_equivClass.size() <= y_equivClass.size()) {
			
			List<List<Integer>> result = this.nPk(new LinkedList<Integer>(y_equivClass), 0, x_equivClass.size());
			
			for (List<Integer> comb : result) {
				
				List<Pair<Integer, Integer>> combPair = new LinkedList<Pair<Integer, Integer>>();
				
				for (int i = 0; i < comb.size(); i++) { combPair.add(new Pair<Integer, Integer>(x_equivClass.get(i), comb.get(i))); }
				
				mappings.add(combPair);
			}
			
		} else {
			List<List<Integer>> result = this.nPk(new LinkedList<Integer>(x_equivClass), 0, y_equivClass.size());
			
			for (List<Integer> comb : result) {
				
				List<Pair<Integer, Integer>> combPair = new LinkedList<Pair<Integer, Integer>>();
				
				for (int i = 0; i < comb.size(); i++) { combPair.add(new Pair<Integer, Integer>(comb.get(i), y_equivClass.get(i))); }
				
				mappings.add(combPair);
			}
		}
		
		 return mappings;
	}
	
	/**
	 * Function for computing the Cartesian product of n sets.
	 * 
	 * @param sets
	 * 
	 * @return S1 x S2 x ... Sn
	 */
	public static List<List<List<Pair<Integer, Integer>>>> getCartesianProduct(List<List<List<Pair<Integer, Integer>>>> sets) {
	    List<List<List<Pair<Integer, Integer>>>> result = new ArrayList<>();
	    getCartesianProductHelper(sets, 0, new ArrayList<>(), result);
	    return result;
	}
	
	/**
	 * Helper function for computing the Cartesian product of n sets.
	 * 
	 * @param sets
	 * @param index
	 * @param current
	 * @param result
	 * 
	 * @return 
	 */
	private static void getCartesianProductHelper(List<List<List<Pair<Integer, Integer>>>> sets, int index, List<List<Pair<Integer, Integer>>> current, 
			List<List<List<Pair<Integer, Integer>>>> result) {
	    if (index == sets.size()) {
	        result.add(new ArrayList<>(current));
	        return;
	    }
	    List<List<Pair<Integer, Integer>>> currentSet = sets.get(index);
	    for (List<Pair<Integer, Integer>> element: currentSet) {
	        current.add(element);
	        getCartesianProductHelper(sets, index+1, current, result);
	        current.remove(current.size() - 1);
	    }
	}
	
	/**
	 * Method for computing mappings from R(v, w) between nodes v in T1 and w in T2 (Main theory).
	 * 
	 * @param v
	 * @param w
	 * 
	 * @return mappings
	 */
	public List<List<Pair<Integer, Integer>>> getMappingsOfEquivalenceClasses(int v, int w) {
		// Find N(v) and N(w):
		Set<Integer> N1 = this.T1.N(v);
		Set<Integer> N2 = this.T2.N(w);
		
		// Find N(v)/~ and N(w)/~:
		Set<Set<Integer>> N_equivClasses_1 = this.T1.N_equivClasses(N1, v);
		Set<Set<Integer>> N_equivClasses_2 = this.T2.N_equivClasses(N2, w);
		
		// Find relation R SubClassOf N(v)/~ x N(w)/~:
		Set<Pair<Set<Integer>, Set<Integer>>> R_vw = R(T1, T2, v, w, N_equivClasses_1, N_equivClasses_2);
		
		// Define the set of injective mappings M_i for all <[x], [y]> in R
		List<List<List<Pair<Integer, Integer>>>> M_i = new LinkedList<List<List<Pair<Integer, Integer>>>>(); 
		
		// Map nodes for <[x], [y]> in R:
		for (Pair<Set<Integer>, Set<Integer>> r : R_vw) { List<List<Pair<Integer, Integer>>> i = mapEquivalenceClasses(r); M_i.add(i); }
		
		// Construct current mappings:
		List<List<Pair<Integer, Integer>>> mappingsEQC = new LinkedList<List<Pair<Integer, Integer>>>();
	    for (List<List<Pair<Integer, Integer>>> m1 : getCartesianProduct(M_i)) {
	    	List<Pair<Integer, Integer>> temp = new LinkedList<Pair<Integer,Integer>>();
			
	    	for (List<Pair<Integer, Integer>> m2 : m1) { temp.addAll(m2); }
	    	
	    	mappingsEQC.add(temp);
	    }
		
	    return mappingsEQC;
	}
	
	/**
	 * Method for constructing the solutions tree for subtree isomorphisms between two description trees T1 and T2.
	 * 
	 * @param T1
	 * @param T2
	 * 
	 * @return isomorphisms
	 */
	public Map<Integer, List<Pair<DTNode, DTNode>>> subtreeIsomorphisms() {
		Map<Integer, List<Pair<DTNode, DTNode>>> isomorphisms = new HashMap<Integer, List<Pair<DTNode, DTNode>>>();
		
		Map<Integer, List<Pair<Integer, Integer>>> ST_nodes = new HashMap<Integer, List<Pair<Integer, Integer>>>();
		List<Pair<Integer, Integer>> startMappings = new LinkedList<Pair<Integer, Integer>>();
		startMappings.add(new Pair<Integer, Integer>(0, 0));
		ST_nodes.put(0, startMappings);
		List<Pair<Integer, Integer>> ST_edges = new LinkedList<Pair<Integer, Integer>>();
		
		Queue<Integer> nodesToEvaluate = new PriorityQueue<Integer>();
		nodesToEvaluate.add(0);
		int nodeIdx = 1;
		
		while (!nodesToEvaluate.isEmpty()) {
			
			// Get the current node from the solutions tree:
			int n = nodesToEvaluate.poll();
			
			// Get from the current node in the solutions tree the mappings to evaluate: 
			List<Pair<Integer, Integer>> mappingsToEvaluate = ST_nodes.get(n);
			
			List<List<List<Pair<Integer, Integer>>>> unique_combination_mappings = new LinkedList<List<List<Pair<Integer, Integer>>>>();
			
			for (Pair<Integer, Integer> pair : mappingsToEvaluate) {
				// Get the vertices for which we need to find R(v, w):
				int v = pair.getValue0();
				int w = pair.getValue1();
				
				// Find R(v, w) and map equivalence classes derived from nodes v and w: 
				List<List<Pair<Integer, Integer>>> currentMappingsEQC = getMappingsOfEquivalenceClasses(v, w);
				
				unique_combination_mappings.add(currentMappingsEQC);
				
			} // end for pair : mappingsToEvaluate
			
			List<List<List<Pair<Integer, Integer>>>> unique_combination_mappings_product = getCartesianProduct(unique_combination_mappings);
			
			for (List<List<Pair<Integer, Integer>>> x : unique_combination_mappings_product) {
				List<Pair<Integer, Integer>> new_ST_node = new LinkedList<Pair<Integer, Integer>>();
				for (List<Pair<Integer, Integer>> y : x) {
					if (y.isEmpty()) { continue; }
					new_ST_node.addAll(y);
				}
				
				if (new_ST_node.isEmpty()) { continue; }
				
				// Add new node with mappings in the solution tree:
				ST_nodes.put(nodeIdx, new_ST_node);
				
				// Add the new edge between the parent and child nodes:
				ST_edges.add(new Pair<Integer, Integer>(n, nodeIdx));
				
				// Add the index of the next node to evaluate:
				nodesToEvaluate.add(nodeIdx);
				
				// Increment the node index:
				nodeIdx += 1;
			} // end for x : unique_combination_mappings_product
		} // end while
		
		// Return all complete paths from the solutions tree containing subtree isomorphisms:
		List<List<Integer>> ST_paths = Functions.setOfAllCompletePaths(Functions.adjacencyMatrix(ST_nodes, ST_edges));
		
		// Get subtree isomorphisms:
		int numberOfSolution = 1;
		for (List<Integer> path : ST_paths) {
			List<Pair<DTNode, DTNode>> currentIsomorphicMapping = new LinkedList<Pair<DTNode, DTNode>>();
			for (Integer solutionsNode : path) {
				List<Pair<Integer, Integer>> nodeMapping = ST_nodes.get(solutionsNode);
				for (Pair<Integer, Integer> pairNodes : nodeMapping) {
					currentIsomorphicMapping.add(new Pair<DTNode, DTNode>(this.T1.V.get(pairNodes.getValue0()), this.T2.V.get(pairNodes.getValue1())));
				}
			}
			isomorphisms.put(numberOfSolution, currentIsomorphicMapping);
			numberOfSolution += 1;
		}
		
		return isomorphisms;
	}
	
	/**
	 * Method for finding the difference between the cardinalities of vertex sets of T1(v) and T2(w).
	 * 
	 * @param T1
	 * @param T2
	 * 
	 * @return diff
	 */
	public int vertex_cardinality_difference(int v, int w, DescriptionTree T1, DescriptionTree T2) {
		
		int diff = 0;
		
		LinkedList<LinkedList<Integer>> p1 = T1.DFSCompletePaths(v);
		LinkedList<LinkedList<Integer>> p2 = T2.DFSCompletePaths(w);
		
		Set<Integer> V_T1_v = new HashSet<Integer>();
		for (LinkedList<Integer> path : p1) { V_T1_v.addAll(path); }
		
		Set<Integer> V_T2_w = new HashSet<Integer>();
		for (LinkedList<Integer> path : p2) { V_T2_w.addAll(path); }
		
		diff = Math.abs(V_T1_v.size() - V_T2_w.size());
		
		return diff;
	}
	
	/**
	 * Method for finding the weighted jaccard index between subtrees T1(v) and T2(w).
	 * 
	 * @param T1
	 * @param T2
	 * 
	 * @return Jw
	 */
	public float weighted_Jaccard_index(int v, int w, DescriptionTree T1, DescriptionTree T2) {
		
//		float Jw_val = 10000;
		float Jw_val = 0;
		
		System.out.println("v = " + v);
		System.out.println("w = " + w);
		
		LinkedList<LinkedList<Integer>> p1 = T1.DFSCompletePaths(v);
		LinkedList<LinkedList<Integer>> p2 = T2.DFSCompletePaths(w);
		
		Set<Integer> V_T1_v = new HashSet<Integer>();
		for (LinkedList<Integer> path : p1) { V_T1_v.addAll(path); }
		
		System.out.println("V_T1_v = " + V_T1_v);
		
		Set<Integer> V_T2_w = new HashSet<Integer>();
		for (LinkedList<Integer> path : p2) { V_T2_w.addAll(path); }
		
		System.out.println("V_T2_w = " + V_T2_w);
		
		Set<Character> Sigma_T1 = new HashSet<Character>();
		
		for (Entry<Pair<Integer, Integer>, Character> entry : T1.E.entrySet()) {
			if (V_T1_v.contains(entry.getKey().getValue0()) && V_T1_v.contains(entry.getKey().getValue1())) { Sigma_T1.add(entry.getValue()); }
		}
		
		Set<Character> Sigma_T2 = new HashSet<Character>();
		for (Entry<Pair<Integer, Integer>, Character> entry : T2.E.entrySet()) {
			if (V_T2_w.contains(entry.getKey().getValue0()) && V_T2_w.contains(entry.getKey().getValue1())) { Sigma_T2.add(entry.getValue()); }
		}
		
//		Set<Character> Sigma_T12 = new HashSet<Character>();
//		for (Character el : Sigma_T1) { Sigma_T12.add(el); }
//		for (Character el : Sigma_T2) { Sigma_T12.add(el); }
		Set<Character> Sigma_T12 = new HashSet<Character>();
		for (Entry<Pair<Integer, Integer>, Character> entry : T1.E.entrySet()) { Sigma_T12.add(entry.getValue()); }
		for (Entry<Pair<Integer, Integer>, Character> entry : T2.E.entrySet()) { Sigma_T12.add(entry.getValue()); }
		
		Map<Character, Integer> r1_vec = new HashMap<Character, Integer>();
		
		Map<Pair<Integer, Integer>, Boolean> visited1 = new HashMap<Pair<Integer, Integer>, Boolean>();
		for (Pair<Integer, Integer> keyPair : T1.E.keySet()) { visited1.put(keyPair, false); }
		
		for (LinkedList<Integer> path : p1) {
			for (int i = 0 ; i < path.size() - 1 ; i++) {
				int n1 = path.get(i);
				int n2 = path.get(i + 1);
				char role = T1.E.get(new Pair<Integer, Integer>(n1, n2));
				
				if (visited1.get(new Pair<Integer, Integer>(n1, n2)) == true) { continue; }
				else { visited1.put(new Pair<Integer, Integer>(n1, n2), true); }
				
				if (r1_vec.containsKey(role)) {
					r1_vec.put(role, r1_vec.get(role) + 1);
				} else {
					r1_vec.put(role, 1);
				}
			}
		}
		
		for (Character role : Sigma_T12) { if (!r1_vec.containsKey(role)) { r1_vec.put(role, 0); } }
		
		Map<Character, Integer> r2_vec = new HashMap<Character, Integer>();
		
		Map<Pair<Integer, Integer>, Boolean> visited2 = new HashMap<Pair<Integer, Integer>, Boolean>();
		for (Pair<Integer, Integer> keyPair : T2.E.keySet()) { visited2.put(keyPair, false); }
		
		for (LinkedList<Integer> path : p2) {
			for (int i = 0 ; i < path.size() - 1 ; i++) {
				int n1 = path.get(i);
				int n2 = path.get(i + 1);
				char role = T2.E.get(new Pair<Integer, Integer>(n1, n2));
				
				if (visited2.get(new Pair<Integer, Integer>(n1, n2)) == true) { continue; }
				else { visited2.put(new Pair<Integer, Integer>(n1, n2), true); }
				
				if (r2_vec.containsKey(role)) {
					r2_vec.put(role, r2_vec.get(role) + 1);
				} else {
					r2_vec.put(role, 1);
				}
			}
		}
		
		for (Character role : Sigma_T12) { if (!r2_vec.containsKey(role)) { r2_vec.put(role, 0); } }
		
		int sum_max = 0;
		int sum_min = 0;
		for (Character role : Sigma_T12) {
			
			int xi = r1_vec.get(role);
			int yi = r2_vec.get(role);
			
			int min_val = 0;
			int max_val = 0;
			if (xi >= yi) {
				min_val = yi;
				max_val = xi;
			} else {
				min_val = xi;
				max_val = yi;
			}
			
			sum_max += max_val;
			sum_min += min_val;
		}
		
		if (sum_max == 0) {
//			Jw_val = 10000;
			Jw_val = 0;
		} else {
			Jw_val = ((float) sum_min) / ((float) sum_max);
		}
		
		System.out.println("Sigma_T1" + Sigma_T1);
		System.out.println("Sigma_T2" + Sigma_T2);
		System.out.println("Sigma_T12" + Sigma_T12);
		System.out.println("r1_vec" + r1_vec);
		System.out.println("r2_vec" + r2_vec);
		System.out.println("Jw_val = " + Jw_val);
		
		return Jw_val;
	}
	
	public Set<Pair<Set<Integer>, Set<Integer>>> R(int v, int w, DescriptionTree T1, DescriptionTree T2) {
		// Find N(v) and N(w):
		Set<Integer> N1 = this.T1.N(v);
		Set<Integer> N2 = this.T2.N(w);
		
		// Find N(v)/~ and N(w)/~:
		Set<Set<Integer>> N_equivClasses_1 = this.T1.N_equivClasses(N1, v);
		Set<Set<Integer>> N_equivClasses_2 = this.T2.N_equivClasses(N2, w);
		
		// Find relation R SubClassOf N(v)/~ x N(w)/~:
		Set<Pair<Set<Integer>, Set<Integer>>> R_vw = R(T1, T2, v, w, N_equivClasses_1, N_equivClasses_2);
		
		return R_vw;
	}
	
	public List<List<Object>> getCartesianProductUsingStreams(List<List<Pair<Pair<Integer, Integer>, Float>>> sets) {
	    return cartesianProduct(sets,0).collect(Collectors.toList());
	}

	public Stream<List<Object>> cartesianProduct(List<List<Pair<Pair<Integer, Integer>, Float>>> sets, int index) {
	    if (index == sets.size()) {
	        List<Object> emptyList = new ArrayList<>();
	        return Stream.of(emptyList);
	    }
	    List<Pair<Pair<Integer, Integer>, Float>> currentSet = sets.get(index);
	    return currentSet.stream().flatMap(element -> cartesianProduct(sets, index+1)
	      .map(list -> {
	          List<Object> newList = new ArrayList<>(list);
	          newList.add(0, element);
	          return newList;
	      }));
	}
	
	
	public List<List<Pair<Integer, Integer>>> weightedJaccard_mappingsToEvaluate(Map<Pair<Integer, Integer>, Float> Jw) {
		
		List<Pair<Pair<Integer, Integer>, Float>> Jw_list = new LinkedList<Pair<Pair<Integer, Integer>, Float>>();
		
		for (Entry<Pair<Integer, Integer>, Float> entry : Jw.entrySet()) { Jw_list.add(new Pair<Pair<Integer, Integer>, Float>(entry.getKey(), entry.getValue())); }
		
		for (int i = 0 ; i < Jw_list.size() ; i++) {
			for (int j = i + 1 ; j < Jw_list.size() ; j++) {
				if (Jw_list.get(i).getValue1() < Jw_list.get(j).getValue1()) {
					Collections.swap(Jw_list, i, j);
				}
			}
		}
		
		System.out.println("Jw_list = " + Jw_list);
		
		List<List<Pair<Integer, Integer>>> heuristicMappingsToEvaluate = new LinkedList<List<Pair<Integer, Integer>>>();
		
		Map<Integer, List<Pair<Pair<Integer, Integer>, Float>>> f1 = new HashMap<Integer, List<Pair<Pair<Integer, Integer>, Float>>>();
		
		for (Pair<Pair<Integer, Integer>, Float> element : Jw_list) {
			if (!f1.containsKey(element.getValue0().getValue0())) {
				List<Pair<Pair<Integer, Integer>, Float>> temp = new LinkedList<Pair<Pair<Integer, Integer>, Float>>();
				temp.add(element);
				f1.put(element.getValue0().getValue0(), temp);
			}
			else {
				f1.get(element.getValue0().getValue0()).add(element); 
			}
		}
		// =========================================================================================================================
		
		Map<Integer, List<Pair<Pair<Integer, Integer>, Float>>> f2 = new HashMap<Integer, List<Pair<Pair<Integer, Integer>, Float>>>();
		for (Pair<Pair<Integer, Integer>, Float> element : Jw_list) {
			if (!f2.containsKey(element.getValue0().getValue1())) {
				List<Pair<Pair<Integer, Integer>, Float>> temp = new LinkedList<Pair<Pair<Integer, Integer>, Float>>();
				temp.add(element);
				f2.put(element.getValue0().getValue1(), temp);
			}
			else {
				f2.get(element.getValue0().getValue1()).add(element); 
			}
		}
		
		List<List<Pair<Pair<Integer, Integer>, Float>>> list_for_CP = new LinkedList<List<Pair<Pair<Integer,Integer>,Float>>>();
		
		System.out.println("f1 == f2 : " + f1.size() + " / " + f2.size());
		
		if (f1.size() < f2.size()) {
			for (Entry<Integer, List<Pair<Pair<Integer, Integer>, Float>>> entry : f1.entrySet()) {
				List<Pair<Pair<Integer, Integer>, Float>> val = entry.getValue();
				
				list_for_CP.add(val);
			}
		} 
		else {
			for (Entry<Integer, List<Pair<Pair<Integer, Integer>, Float>>> entry : f2.entrySet()) {
				List<Pair<Pair<Integer, Integer>, Float>> val = entry.getValue();
				
				list_for_CP.add(val);
				
			}
		}
		System.out.println("+++++++++++");
		System.out.println("list_for_CP = " + list_for_CP);
		
		List<List<Object>> CP = getCartesianProductUsingStreams(list_for_CP);
		
		System.out.println("+++++++++++");
		System.out.println("f1 = ");
		Functions.printMap(f1);
		System.out.println();
		System.out.println("f2 = ");
		Functions.printMap(f2);
		System.out.println("+++++++++++");
		
		List<List<Pair<Pair<Integer, Integer>, Float>>> final_CP = new LinkedList<List<Pair<Pair<Integer, Integer>, Float>>>();
		
		for (List<Object> el1 : CP) {
			
			boolean to_add = true;
			
			List<Integer> duplicates_x = new LinkedList<>();
			List<Integer> duplicates_y = new LinkedList<>();
			
			List<Pair<Pair<Integer, Integer>, Float>> temp = new LinkedList<Pair<Pair<Integer, Integer>, Float>>();
			
			for (Object el2 : el1) {
				@SuppressWarnings("unchecked")
				Pair<Pair<Integer, Integer>, Float> element = (Pair<Pair<Integer, Integer>, Float>) el2;
				
				Integer node_x = element.getValue0().getValue0();
				Integer node_y = element.getValue0().getValue1();
				
				if (!duplicates_x.contains(node_x)) { duplicates_x.add(node_x); } else { to_add = false; }
				if (!duplicates_y.contains(node_y)) { duplicates_y.add(node_y); } else { to_add = false; }
				
				if (to_add == false) {
					break;
				}
				
				temp.add(element);
				
			}
//			System.out.println("---" + to_add);
//			System.out.println(temp);
//			System.out.println(el1);
			if (to_add == true) { final_CP.add(temp); }
		}
		
		float max_sum = -1;
		
		for (List<Pair<Pair<Integer, Integer>, Float>> x : final_CP) {
			float sum_jw = 0;
			
			for (Pair<Pair<Integer, Integer>, Float> y : x) {
				sum_jw += y.getValue1();
			}
			
			if (sum_jw > max_sum) {
				max_sum = sum_jw;
			}
		}
		
		for (List<Pair<Pair<Integer, Integer>, Float>> x : final_CP) {
			float sum_jw = 0;
			
			for (Pair<Pair<Integer, Integer>, Float> y : x) {
				sum_jw += y.getValue1();
			}
			
			if (sum_jw == max_sum) {
				
				List<Pair<Integer, Integer>> temp = new LinkedList<Pair<Integer, Integer>>();
				
				for (Pair<Pair<Integer, Integer>, Float> y : x) {
					temp.add(y.getValue0());
				}
				
				heuristicMappingsToEvaluate.add(temp);
			}
		}
		
		System.out.println("CP = ");
		Functions.printList(CP);
		System.out.println("---");
		System.out.println("final_CP = ");
		Functions.printList(final_CP);
		
		System.out.println("CP.size = " + CP.size());
		System.out.println("final_CP.size = " + final_CP.size());
		System.out.println("max_sum = " + max_sum);
		
		System.out.println("heuristicMappingsToEvaluate = " + heuristicMappingsToEvaluate);
		
		return heuristicMappingsToEvaluate;
	}
	
	/**
	 * Method for constructing the solutions tree for subtree isomorphisms between two description trees T1 and T2.
	 * 
	 * @param T1
	 * @param T2
	 * 
	 * @return isomorphisms
	 */
	public Map<Integer, List<Pair<DTNode, DTNode>>> heuristicsSubtreeIsomorphisms() {
		Map<Integer, List<Pair<DTNode, DTNode>>> isomorphisms = new HashMap<Integer, List<Pair<DTNode, DTNode>>>();
		
		Map<Integer, List<Pair<Integer, Integer>>> ST_nodes = new HashMap<Integer, List<Pair<Integer, Integer>>>();
		List<Pair<Integer, Integer>> startMappings = new LinkedList<Pair<Integer, Integer>>();
		startMappings.add(new Pair<Integer, Integer>(0, 0));
		ST_nodes.put(0, startMappings);
		List<Pair<Integer, Integer>> ST_edges = new LinkedList<Pair<Integer, Integer>>();
		
		Queue<Integer> nodesToEvaluate = new PriorityQueue<Integer>();
		nodesToEvaluate.add(0);
		int nodeIdx = 1;
		
		while (!nodesToEvaluate.isEmpty()) {
			
			System.out.println("----------- NEXT ITERATION -----------");
			
//			if (ST_nodes.size() > 5000) {
//				break;
//			}
			
			// Get the current node from the solutions tree:
			int n = nodesToEvaluate.poll();
			
			// Get from the current node in the solutions tree the mappings to evaluate: 
			List<Pair<Integer, Integer>> mappingsToEvaluate = ST_nodes.get(n);
			
			// ================================= CALCULATE HEURISTICS HERE AND PICK THE HIGHEST ONE TO PROPAGATE: =================================
			
			List<List<List<Pair<Integer, Integer>>>> unique_combination_mappings = new LinkedList<List<List<Pair<Integer, Integer>>>>();
			
			List<List<List<Pair<Integer, Integer>>>> heuristicsMappings = new LinkedList<List<List<Pair<Integer, Integer>>>>();
			
			for (Pair<Integer, Integer> pair : mappingsToEvaluate) {
				
				// Get the vertices for which we need to find R(v, w):
				int v = pair.getValue0();
				int w = pair.getValue1();
				
				// h1:
				int h1 = vertex_cardinality_difference(v, w, T1, T2);
				
				// h2:
				float h2 = weighted_Jaccard_index(v, w, T1, T2);
				
				// R:
				Set<Pair<Set<Integer>, Set<Integer>>> R_vw = R(v, w, T1, T2);
				
				if (R_vw.isEmpty()) { continue; }
				
//				System.out.println(v + " / " + w);
//				System.out.println("|T1(v)| - |T2(w)| = " + h1);
//				System.out.println("Jw(T1(v), T2(w)) = " + h2);
//				System.out.println("R = " + R_vw);
				
				
				
				for (Pair<Set<Integer>, Set<Integer>> pairEQC : R_vw) {
					
					Set<Integer> x_eqc = pairEQC.getValue0();
					Set<Integer> y_eqc = pairEQC.getValue1();
					
					System.out.println("///////////");
					
					System.out.println("[x] = " + x_eqc);
					System.out.println("[y] = " + y_eqc);
					
					Map<Pair<Integer, Integer>, Float> Jw = new HashMap<Pair<Integer, Integer>, Float>();
					
					for (int node_x : x_eqc) {
						
						for (int node_y : y_eqc) {
							
							Jw.put(new Pair<Integer, Integer>(node_x, node_y), weighted_Jaccard_index(node_x, node_y, T1, T2));
							
						} // END for node_y : y_eqc
						
					} // END for node_x : x_eqc
					
					// HERE FIND THE NEW MAPPINGS TO EVALUATE (BY THE HEURISTICS) FOR EACH PAIR OF EQUIVALENCE CLASSES.
					
					// TODO: FINISH FROM HERE (THE weightedJaccard_mappingsToEvaluate(Jw) is done, just TEST IT!!!) Continue now after that function to find the mappings...
					// FOR EACH PAIR OBTAINED RESULTS FROM weightedJaccard_mappingsToEvaluate(Jw) add them to one list same as mappingsToEvaluate and these will be the 
					// new node for the Solutions Tree.
					
					// TODO: TAKE INTO ACCOUNT 10000 AND 0.0 AS THE SAME. IN BOTH CASES THEY SHOULD BE ADDED IN THE FINAL COMBINATIONS. THEY ARE OF EQUAL VALUE.
					
					heuristicsMappings.add(weightedJaccard_mappingsToEvaluate(Jw));

				} // END for pairEQC : R_vw
				
				
				
				// =============================================================================
				
//				System.out.println(v + " / " + w + " / " + heuristicsMappings);
				
				// =============================================================================
				
				
				float h = h1 + (1 / h2);
				
			} // END for pair : mappingsToEvaluate (with heuristics)
			
//			System.out.println("---");
//			System.out.println("mappingsToEvaluate = " + mappingsToEvaluate);
//			System.out.println("heuristicsMappings " + heuristicsMappings);
//			System.out.println("---");
			
			// ====================================================================================================================================
			
			List<List<List<Pair<Integer, Integer>>>> unique_combination_mappings_product = getCartesianProduct(heuristicsMappings);
			
//			System.out.println("unique_combination_mappings_product = ");
//			for (List<List<Pair<Integer, Integer>>> ele : unique_combination_mappings_product) {
//				System.out.println(ele);
//			}
			
			for (List<List<Pair<Integer, Integer>>> x : unique_combination_mappings_product) {
				
				List<Pair<Integer, Integer>> new_ST_node = new LinkedList<Pair<Integer, Integer>>();
				
				for (List<Pair<Integer, Integer>> y : x) {
					
					if (y.isEmpty()) { continue; }
					
					new_ST_node.addAll(y);
					
				}
				
				if (new_ST_node.isEmpty()) { continue; }
				
				// Add new node with mappings in the solution tree:
				ST_nodes.put(nodeIdx, new_ST_node);
				
				// Add the new edge between the parent and child nodes:
				ST_edges.add(new Pair<Integer, Integer>(n, nodeIdx));
				
				// Add the index of the next node to evaluate:
				nodesToEvaluate.add(nodeIdx);
				
				// Increment the node index:
				nodeIdx += 1;
				
			} // end for x : unique_combination_mappings_product
			
		} // end while
		
//		System.out.println("# ST_nodes = " + ST_nodes.size());
//		System.out.println("# ST_edges = " + ST_edges.size());
		
		System.out.println("ST_nodes");
		Functions.printMap(ST_nodes);
		
		// Return all complete paths from the solutions tree containing subtree isomorphisms:
		List<List<Integer>> ST_paths = Functions.setOfAllCompletePaths(Functions.adjacencyMatrix(ST_nodes, ST_edges));
		
		// Get subtree isomorphisms:
		int numberOfSolution = 1;
		for (List<Integer> path : ST_paths) {
			List<Pair<DTNode, DTNode>> currentIsomorphicMapping = new LinkedList<Pair<DTNode, DTNode>>();
			for (Integer solutionsNode : path) {
				List<Pair<Integer, Integer>> nodeMapping = ST_nodes.get(solutionsNode);
				for (Pair<Integer, Integer> pairNodes : nodeMapping) {
					currentIsomorphicMapping.add(new Pair<DTNode, DTNode>(this.T1.V.get(pairNodes.getValue0()), this.T2.V.get(pairNodes.getValue1())));
				}
			}
			isomorphisms.put(numberOfSolution, currentIsomorphicMapping);
			numberOfSolution += 1;
		}
		
		// Output:
//		System.out.println("---");
//		System.out.println("ST_nodes = ");
//		Functions.printMap(ST_nodes);
////		System.out.println("---");
////		System.out.println("ST_edges = ");
////		Functions.printList(ST_edges);
//		System.out.println("---");
//		System.out.println("ST_paths = " + ST_paths.size());
//		Functions.printList(ST_paths);
		System.out.println("---");
		Functions.printIsomorphisms(isomorphisms);
		System.out.println("---");
//		System.out.println("isomorphisms = " + isomorphisms);
//		System.out.println("---");
		
		return isomorphisms;
	}
	
	/**
	 * Method for constructing the hypotheses from the subtree isomorphisms between two description trees T1 and T2.
	 * 
	 * @param isomorphisms 
	 * 
	 * @return H
	 */
	public Set<Set<String>> constructHypotheses(Map<Integer, List<Pair<DTNode, DTNode>>> isomorphisms, String typeOfAxiom) throws OWLOntologyCreationException {
		Set<Set<String>> H = new HashSet<Set<String>>(); 
		
		Set<String> V1 = this.T1.inv_V.keySet();
		Set<String> V2 = this.T2.inv_V.keySet();
		
		for (Entry<Integer, List<Pair<DTNode, DTNode>>> entry : isomorphisms.entrySet()) {
			List<Pair<DTNode, DTNode>> isomorphicMappings = entry.getValue();
			
			Set<String> h = new HashSet<String>();
			
			// Get the nodes to contract from T1 and T2:
			Pair<Set<Integer>, Set<Integer>> pairNodesToContract = findNodesToContract(isomorphicMappings, V1, V2);
			List<Integer> nodesToContractT1 = new LinkedList<Integer>(pairNodesToContract.getValue0());
			List<Integer> nodesToContractT2 = new LinkedList<Integer>(pairNodesToContract.getValue1());
			
			Collections.sort(nodesToContractT1, Collections.reverseOrder());
			Collections.sort(nodesToContractT2, Collections.reverseOrder());
			
			Map<Integer, List<String>> L1 = contractNodesAndModifyLabels(nodesToContractT1, T1);
			Map<Integer, List<String>> L2 = contractNodesAndModifyLabels(nodesToContractT2, T2);
			
			for (Pair<DTNode, DTNode> mapping : isomorphicMappings) {
				// For each mapping (v_i, w_i), read the labels of v_i and w_i and construct the OWLAxiom v_i SubClassOf w_i. 
				// We can just read the nodes in the isomorphic mappings, since they contain the contracted nodes (class expressions) in them.
				DTNode v = mapping.getValue0();
				DTNode w = mapping.getValue1();
				
				// Get index of v and index of w:
				int vIdx = this.T1.inv_V.get(v.name);
				int wIdx = this.T2.inv_V.get(w.name);
				
				// Construct axiom as string:
				String clsInput = String.join(" and ", L1.get(vIdx)) + " " + typeOfAxiom + " " + String.join(" and ", L2.get(wIdx));
				
				// Add the axiom to the hypothesis:
				h.add(clsInput);
			}
			
			// Add the hypothesis in the set of hypotheses:
			H.add(h);
		}
		
		return H;
	}
	
	/**
	 * Function that constructs the axioms from hypotheses and adds them in a separate ontology.
	 * 
	 * @param 
	 * 
	 * @return 
	 * @throws OWLOntologyCreationException 
	 */
	public void constructOntologiesFromHypotheses() throws OWLOntologyCreationException {
		// TODO:
		
////		// Create manager for ontology:
//		OWLOntologyManager manager = owlUtils.manager;
////		
////		// Get the data factory:
//		OWLDataFactory dataFactory = owlUtils.createDataFactory();
////		
////		// Create IRI of ontology:
//		int i = 0;
//		IRI ontologyIRI = IRI.create("http://create-hypothesis" + i + "#");
////		// Create an empty ontology:
//		OWLOntology ontology = manager.createOntology(ontologyIRI);
////		
//		// Get class and property names:
//		Pair<List<String>, List<String>> classAndPropertyNames = getClassAndPropertyNames(clsInput);
//		List<String> classNames = classAndPropertyNames.getValue0();
//		List<String> propertyNames = classAndPropertyNames.getValue1();
//		
//		List<String> visitedDeclaredClassNames = new LinkedList<String>();
//		List<String> visitedDeclaredPropertyNames = new LinkedList<String>();
//		
//		// Declare class names:
//		for (String className : classNames) {
//			if (!visitedDeclaredClassNames.contains(className)) {
//				OWLClass A = dataFactory.getOWLEntity(EntityType.CLASS, IRI.create(className));
//				OWLDeclarationAxiom declareA = dataFactory.getOWLDeclarationAxiom(A);
//				manager.addAxiom(ontology, declareA);
//				visitedDeclaredClassNames.add(className);
//			}
//		}
//		
//		// Declare property names:
//		for (String propertyName : propertyNames) {
//			if (!visitedDeclaredPropertyNames.contains(propertyName)) {
//				OWLObjectProperty property = dataFactory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(propertyName));
//				OWLDeclarationAxiom declareProperty = dataFactory.getOWLDeclarationAxiom(property);
//				manager.addAxiom(ontology, declareProperty);
//				visitedDeclaredPropertyNames.add(propertyName);
//			}
//		}
//		
//		// Construct OWLAxiom:
//		OWLAxiom exp = owlUtils.parseClassExpression(ontology, manager, dataFactory, clsInput);
//		
//		try {
//			for (OWLAxiom axiom : h) {
//				manager.addAxiom(ontology, axiom);
//				
//				String new_path = "C:\\Users\\Ivan\\Desktop\\JavaCodeHypotheses\\onto" + i + ".owl";
//				
//				manager.saveOntology(ontology, new FunctionalSyntaxDocumentFormat(), IRI.create((new File(new_path).toURI())));
//			}
//		} catch (Exception e) { e.printStackTrace(); }
	}
	
	/**
	 * Function that returns a list of class names and property names for a given input, in the form of a string, of an axiom.
	 * 
	 * @param input
	 * 
	 * @return <classWords, propertyWords>
	 */
	public Pair<List<String>, List<String>> getClassAndPropertyNames(String input) {
		String[] arrayOfString = input.split(" ");
		for (int x = 0 ; x < arrayOfString.length ; x++) { arrayOfString[x] = arrayOfString[x].replaceAll("\\(", "").replaceAll("\\)", ""); }
		
        List<String> classWords = new LinkedList<String>();
        List<String> propertyWords = new LinkedList<String>();
        
		for (int x = 0 ; x < arrayOfString.length ; x++) {
			if (!arrayOfString[x].equals("and") && !arrayOfString[x].equals("some") && (!arrayOfString[x].equals("SubClassOf") || !arrayOfString[x].equals("SupClassOf") || !arrayOfString[x].equals("EquivalentTo") || !arrayOfString[x].equals("<") || !arrayOfString[x].equals(">") || !arrayOfString[x].equals("="))) {
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
	
	/**
	 * Function that for a set of nodes to contract and a description tree it returns a modified version of the node labels.
	 * 
	 * @param nodesToContractT1
	 * @param T
	 * 
	 * @return L
	 */
	public Map<Integer, List<String>> contractNodesAndModifyLabels(List<Integer> nodesToContractT1, DescriptionTree T) {
		Map<Integer, List<String>> L = new HashMap<Integer, List<String>>();
		
		for (Entry<Integer, DTNode> dt_node : T.V.entrySet()) { L.put(dt_node.getKey(), new LinkedList<String>(dt_node.getValue().label)); }
		
		for (Integer child : nodesToContractT1) {
		
			int parent = 0; 
			for (Entry<Pair<Integer, Integer>, Character> x : T.E.entrySet()) { if (x.getKey().getValue1() == child) { parent = x.getKey().getValue0();  } }
			
			String conjunct = "(" + String.join(" and ", L.get(child)) + ")";
			
			L.get(parent).add("(" + T.E.get(new Pair<Integer, Integer>(parent, child)) + " some " + conjunct + ")");
		}
		return L;
	}
	
	/**
	 * Function that for a given isomorphic mapping it finds a set of nodes to contract from T1 and T2.
	 * 
	 * @param isomorphicMappings
	 * @param V1
	 * @param V2
	 * 
	 * @return <nodesToContractT1, nodesToContractT2>
	 */
	public Pair<Set<Integer>, Set<Integer>> findNodesToContract(List<Pair<DTNode, DTNode>> isomorphicMappings, Set<String> V1, Set<String> V2) {
		// Find all common nodes in the isomorphic mapping with T1 and T2:
		Set<String> commonNodesT1 = new HashSet<String>();
		Set<String> commonNodesT2 = new HashSet<String>();
		for (Pair<DTNode, DTNode> i : isomorphicMappings) { commonNodesT1.add(i.getValue0().name); commonNodesT2.add(i.getValue1().name); }
		
		// Find all nodes to contract from T1 and T2:
		Set<String> nodesToContractT1_temp = new HashSet<String>(V1);
		Set<String> nodesToContractT2_temp = new HashSet<String>(V2);
		nodesToContractT1_temp.removeAll(commonNodesT1);
		nodesToContractT2_temp.removeAll(commonNodesT2);
		Set<Integer> nodesToContractT1 = new HashSet<Integer>();
		Set<Integer> nodesToContractT2 = new HashSet<Integer>();
		for (String node : nodesToContractT1_temp) { nodesToContractT1.add(this.T1.inv_V.get(node)); }
		for (String node : nodesToContractT2_temp) { nodesToContractT2.add(this.T2.inv_V.get(node)); }
		
		return new Pair<Set<Integer>, Set<Integer>>(nodesToContractT1, nodesToContractT2);
	}
	
	/**
	 * Function that returns the explanations for a non-entailment.
	 * 
	 * @param T1
	 * @param T2
	 * 
	 * @return H
	 * 
	 * @throws OWLOntologyCreationException 
	 */
	@SuppressWarnings("unchecked")
	public Set<Set<String>> explainNonEntailment(String typeOfAxiom) throws OWLOntologyCreationException {
		
		Set<Set<String>> H = new HashSet<Set<String>>();
		
		// Compute subtree isomorphisms phi : T1 -> T2:
		@SuppressWarnings("rawtypes")
		Map i = this.subtreeIsomorphisms();
		
		// Create Hypotheses from subtree isomorphisms:
		H = this.constructHypotheses(i, typeOfAxiom);
		
		// Return Hypotheses:
		return H;
	}
}
