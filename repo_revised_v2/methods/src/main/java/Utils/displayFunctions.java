package Utils;

import java.util.Set;

import Trees.DescriptionTree;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

public class displayFunctions {
	
	/**
	 * Function that displays the hypotheses.
	 * 
	 * @param H
	 * @return 
	 * 
	 */
	public static void displayHypotheses(Set<Set<String>> H, int numberOfHypothesesToDisplay) {
		
		int counter = 0;
		
		System.out.println("====================================================================================================");
		for (Set<String> element : H) {
			
			if (counter == numberOfHypothesesToDisplay) {
				break;
			}
			
			counter++;
			System.out.println("H" + counter + " = ");
			for (String el : element) {
				System.out.println("\t" + el);
			}
			System.out.println("----------------------------------------------------------------------------------------------------");
		}
		System.out.println("====================================================================================================");
	}
	
	/**
	 * Function that displays the hypotheses.
	 * 
	 * @param H
	 * @return 
	 * 
	 */
	public static void displayHypotheses(Set<Set<String>> H) {
		
		int counter = 0;
		
		System.out.println("====================================================================================================");
		for (Set<String> element : H) {
			
			counter++;
			System.out.println("H" + counter + " = ");
			for (String el : element) {
				System.out.println("\t" + el);
			}
			System.out.println("----------------------------------------------------------------------------------------------------");
		}
		System.out.println("====================================================================================================");
	}
	
	/**
	 * Function that displays the hypotheses.
	 * 
	 * @param H
	 * @return 
	 * 
	 */
	public static void displayHypotheses(Set<Set<String>> H, Map<String, Character> representatives) {
		
		int counter = 0;
		
		System.out.println("====================================================================================================");
		for (Set<String> element : H) {
			
			counter++;
			System.out.println("H" + counter + " = ");
			for (String el : element) {
				for (Entry<String, Character> representative : representatives.entrySet()) {
					String replaceWith = "\\(" + representative.getKey() + " some";
			    	String toReplace = "\\(" + representative.getValue() + " some";
					el = el.replaceAll(toReplace, replaceWith);
				}
				System.out.println("\t" + el);
			}
			System.out.println("----------------------------------------------------------------------------------------------------");
		}
		System.out.println("====================================================================================================");
	}
	
	/**
	 * Function that graphically display a description tree.
	 * 
	 * @param T
	 */
	public static void displayTreeGraph(DescriptionTree T) {
		boolean[] flag = new boolean[T.V.size()];
	    Arrays.fill(flag, true);
		T.printNTree(0, flag, 0, false);
	}
}
