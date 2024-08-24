# An-Abduction-based-Method-for-Explaining-Non-entailments-of-Semantic-Matching
This is the repository for the "Explaining EL Non-entailments via Subtree Isomorphisms" journal paper, submitted at the Semantic Web Journal.

This repository contains the files/folders with the code implementation of the method for the "Explaining EL Non-entailments via Subtree Isomorphisms" journal paper, submitted at the Semantic Web Journal.:

To open the code, download the zip file and open it in a new Maven Project. Build the project.

Dependencies used (found in the pom.xml file of the Maven Project):

<dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
  
  	<dependency>
  	  <groupId>org.jgrapht</groupId>
	  <artifactId>jgrapht-core</artifactId>
	  <version>1.5.1</version>
  	</dependency>
  	
  	<!-- https://mvnrepository.com/artifact/org.graphstream/gs-core -->
	<dependency>
        <groupId>org.graphstream</groupId>
        <artifactId>gs-core</artifactId>
        <version>1.3</version>
    </dependency>  
          
    <dependency>
        <groupId>org.graphstream</groupId>
        <artifactId>gs-algo</artifactId>
        <version>1.3</version>
    </dependency>
    
    <dependency>
        <groupId>org.graphstream</groupId>
        <artifactId>gs-ui</artifactId>
        <version>1.3</version>
    </dependency>
	
  	
  <!-- https://mvnrepository.com/artifact/net.sourceforge.owlapi/owlapi-distribution -->
	<dependency>
    	<groupId>net.sourceforge.owlapi</groupId>
    	<artifactId>owlapi-distribution</artifactId>
    	<version>5.5.0</version>
	</dependency>
	
	<dependency>
   	 	<groupId>net.sourceforge.owlapi</groupId>
    	<artifactId>org.semanticweb.hermit</artifactId>
    	<version>1.4.3.517</version>
	</dependency>
	
	<dependency>
        <groupId>com.github.galigator.openllet</groupId>
        <artifactId>openllet-owlapi</artifactId>
        <version>2.6.5</version>
    </dependency>
    
    <dependency>
        <groupId>com.github.ansell.pellet</groupId>
        <artifactId>pellet-owlapiv3</artifactId>
        <version>2.3.6-ansell</version>
    </dependency>

    <dependency>
        <groupId>com.github.galigator.openllet</groupId>
        <artifactId>openllet-jena</artifactId>
        <version>2.6.5</version>
    </dependency>
	
	<!-- owlexplanation goes here -->

  	<dependency>
	    <groupId>org.javatuples</groupId>
	    <artifactId>javatuples</artifactId>
	    <version>1.2</version>
	</dependency>
	
  </dependencies>

Protege:

The experiments are performed w.r.t. ontologies, saved as .owl files. To directly read the generated ontologies, please download Protege (https://protege.stanford.edu/).

Running the simulations:

To run the experiments open the workbench folder in a new Maven Project. Next, open the Experiments.java class. Build the project. Provided that all the necessary dependencies are installed, the algorithm will ask for: a path of the folder in which you wish to save the results, the name of the txt file in which you want to save the result, and the simulation parameters (a, b, c, d) which are integer values. Please provide the inputs and click Enter. After, the algorithm will perform the experiments and save the results in that folder.

To run the working example from the paper, open the WorkingExample.java class and run it. The results will be shown in the output. (Note: there are intermediate displays of values. The final part contains the results of the method (hypotheses)).
