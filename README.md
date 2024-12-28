# An-Abduction-based-Method-for-Explaining-Non-entailments-of-Semantic-Matching
This is the repository for the "An Abduction-based Method for Explaining Non-entailments of Semantic Matching" journal paper, submitted at the Semantic Web Journal.

This repository contains the folder/files with the code implementation of the method for the "An Abduction-based Method for Explaining Non-entailments of Semantic Matching" journal paper, submitted at the Semantic Web Journal.

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

Running the code:

The repository contains a JAR file that can be used to run the method. To run the JAR file, do the following:

1. Open the command prompt (cmd)
2. Navigate to the folder in which the JAR file is (e.g. cd C:\Users\Desktop...)
3. Once you are in the correct folder execute the command: java -jar method.jar

The main function will run and provide a basic UI in the command prompt to work with the method or run the synthetic and real data experiments.
