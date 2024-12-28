package Examples;

import java.util.LinkedList;

import org.javatuples.Pair;

import Trees.DescriptionTree;

public class WorkingExample {
	
	public static Pair<DescriptionTree, DescriptionTree> workingExample() {
		DescriptionTree T1 = new DescriptionTree('v');
		
		LinkedList<String> label_v = new LinkedList<String>();
		label_v.add("Scan");
		LinkedList<String> label_u = new LinkedList<String>();
		label_u.add("X-RayC.T.");

		T1.addEdge(0, 1, 'r', label_v, label_u);
		
		LinkedList<String> label_v1 = new LinkedList<String>();
		label_v1.add("Scan");
		LinkedList<String> label_u1 = new LinkedList<String>();
		label_u1.add("X-RayTube");
		T1.addEdge(0, 2, 's', label_v1, label_u1);
		
		LinkedList<String> label_v2 = new LinkedList<String>();
		label_v2.add("Scan");
		LinkedList<String> label_u2 = new LinkedList<String>();
		label_u2.add("X-Ray");
		T1.addEdge(0, 3, 'p', label_v2, label_u2);
		
		LinkedList<String> label_v3 = new LinkedList<String>();
		label_v3.add("X-RayTube");
		LinkedList<String> label_u3 = new LinkedList<String>();
		label_u3.add("Rotation");
		T1.addEdge(2, 4, 'q', label_v3, label_u3);
		
		DescriptionTree T2 = new DescriptionTree('w');
		
		LinkedList<String> label_w = new LinkedList<String>();
		label_w.add("Scan");
		LinkedList<String> label_q = new LinkedList<String>();
		label_q.add("Imaging");

		T2.addEdge(0, 1, 'r', label_w, label_q);
		
		LinkedList<String> label_w1 = new LinkedList<String>();
		label_w1.add("Scan");
		LinkedList<String> label_q1 = new LinkedList<String>();
		label_q1.add("Part");
		T2.addEdge(0, 2, 's', label_w1, label_q1);
		
		LinkedList<String> label_w2 = new LinkedList<String>();
		label_w2.add("Scan");
		LinkedList<String> label_q2 = new LinkedList<String>();
		label_q2.add("IonizingRadiation");
		T2.addEdge(0, 3, 'p', label_w2, label_q2);
		
		Pair<DescriptionTree, DescriptionTree> DTs = new Pair<DescriptionTree, DescriptionTree>(T1, T2);
		
		return DTs;
	}
}

