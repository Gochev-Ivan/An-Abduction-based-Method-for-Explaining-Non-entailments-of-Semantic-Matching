package Trees;

import java.util.LinkedList;

public class DTNode {
	/* 
	 * Attributes:
	 * 	Class Attributes : None
	 * 	Instance attributes : name: str, label: list(str)
	 * 
	 * Methods:
	 * 
	 */
	
	public String name;
	public LinkedList<String> label;
	
	public DTNode(String name, LinkedList<String> label) {
		this.name = name;
		this.label = label;
	}
}
