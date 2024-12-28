package OWLUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxOWLObjectRendererImpl;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;

import com.xai.methods.XeNON;

import Trees.DescriptionTree;
import Utils.displayFunctions;
import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;
import openllet.owlapi.explanation.PelletExplanation;

public class Explanations
{

  public OWLOntology ontology;

  public OWLAxiom axiom;

  /**
   * Constructor for class Explanations
   */
  public Explanations(OWLOntology ontology, OWLAxiom axiom)
  {
      this.ontology = ontology;
      this.axiom = axiom;
  }


  /**
   * Function that checks whether the axiom is entailed or not entailed.
   * @return true / false
   */
  public boolean isEntailed()
  {

      OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(this.ontology);

      reasoner.precomputeInferences(InferenceType.values());
      
      boolean isEntailed = reasoner.isEntailed(this.axiom);
      
      reasoner.dispose();
      
      return isEntailed;

  }
  
  /**
   * Function that checks whether the ontology is consistent.
   * @return true / false
   */
  public boolean isConsistent()
  {

      OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(this.ontology);

      reasoner.precomputeInferences(InferenceType.values());
      
      boolean isConsistent = reasoner.isConsistent();
      
      reasoner.dispose();
      
      return isConsistent;

  }
  
  /**
   * Function that checks whether the ontology is consistent.
   * @return true / false
   */
  public boolean isSatisfiable(OWLClass owlClass)
  {

      OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(this.ontology);

      reasoner.precomputeInferences(InferenceType.values());
      
      boolean isSatisfiable = reasoner.isSatisfiable(owlClass); 
      
      reasoner.dispose();
      
      return isSatisfiable;
      
  }

  /**
   * Function that returns the explanations for an entailment.
   * @return Set of explanations for an axiom
   */
  public Set<Set<OWLAxiom>> getExplanationsEntailment() throws OWLException, IOException {

      OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(this.ontology);

      reasoner.precomputeInferences(InferenceType.values());

      PelletExplanation explanationGenerator = new PelletExplanation(reasoner);

      Set<Set<OWLAxiom>> explanations = explanationGenerator.getEntailmentExplanations(this.axiom);
      
      reasoner.dispose();
      
      return explanations;
  }


  /**
   * Function that returns the explanations for a non-entailment.
   */
  public static Set<Set<String>> getExplanationsNonEntailment(OWLOntology selectedOntology, int numberOfExplanations, String input, String TOA) throws OWLOntologyCreationException
  {

      String splitted_input[] = input.split(" " + TOA + " ");

      String string1 = splitted_input[0];
      String string2 = splitted_input[1];

      DescriptionTree T1 = owlUtils.expr2tree(string1,'v');
      DescriptionTree T2 = owlUtils.expr2tree(string2,'w');

      // Generate Hypotheses:
      XeNON xenon = new XeNON(T1, T2);

      @SuppressWarnings("rawtypes")
      Map i = xenon.subtreeIsomorphisms();

      @SuppressWarnings("unchecked")
      Set<Set<String>> Hypotheses = xenon.constructHypotheses(i, TOA);

      return Hypotheses;

  }
  
  /**
   * Function that returns the explanations for a non-entailment.
   */
  public Set<Set<String>> getExplanationsNonEntailment(OWLOntology selectedOntology, String input, String TOA) throws OWLOntologyCreationException
  {	  
	  ManchesterOWLSyntaxOWLObjectRendererImpl rend = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	  // Prepare the input for the method 
	  // (The method is implemented to work with characters as roles and not full strings, thus we map each string of a role in the expressions to a unique character).
	  String splitted_input[] = input.split(" " + TOA + " ");
	  String LHS_conceptDef = splitted_input[0];
	  String RHS_conceptDef = splitted_input[1];
	  
	  List<String> LHS_Nr = owlUtils.getSignatureFromExpr(LHS_conceptDef).getValue1();
	  List<String> RHS_Nr = owlUtils.getSignatureFromExpr(RHS_conceptDef).getValue1();
	  
	  Map<String, Character> rolesMap = new HashMap<String, Character>();
	  String listOfTreeRoles = "abcdefghijklmnopqrstuvyxwz";
	  int listOfTreeRolesCounter = 0;
	  
	  for (String lhsRole : LHS_Nr) { 
		  if (!rolesMap.containsKey(lhsRole)) { 
			  rolesMap.put(lhsRole, listOfTreeRoles.charAt(listOfTreeRolesCounter)); 
			  listOfTreeRolesCounter++; 
		  }
	  }
	  
	  for (String rhsRole : RHS_Nr) {
		  if (!rolesMap.containsKey(rhsRole)) { 
			  rolesMap.put(rhsRole, listOfTreeRoles.charAt(listOfTreeRolesCounter)); 
			  listOfTreeRolesCounter++; 
		  }
	  }
	  
	  String LHS_conceptDefToInput = LHS_conceptDef;
	  String RHS_conceptDefToInput = RHS_conceptDef;
	  for (Entry<String, Character> entry : rolesMap.entrySet()) {
		  LHS_conceptDefToInput = LHS_conceptDefToInput.replace(entry.getKey(), entry.getValue() + "");
		  RHS_conceptDefToInput = RHS_conceptDefToInput.replace(entry.getKey(), entry.getValue() + "");
	  }
	  
	  String nonEntailmentToInput = LHS_conceptDefToInput + " " + TOA + " " + RHS_conceptDefToInput;
	  
      String splitted_input_1[] = nonEntailmentToInput.split(" " + TOA + " ");
      String string1 = splitted_input_1[0];
      String string2 = splitted_input_1[1];

      DescriptionTree T1 = owlUtils.expr2tree(string1,'v');
      DescriptionTree T2 = owlUtils.expr2tree(string2,'w');
      
      // Initialize the method:
      XeNON xenon = new XeNON(T1, T2);
      
      // Compute subtree isomorphisms T1 -> T2:
      @SuppressWarnings("rawtypes")
      Map i = xenon.subtreeIsomorphisms();
      
      // Construct hypotheses:
      @SuppressWarnings("unchecked")
      Set<Set<String>> Hypotheses = xenon.constructHypotheses(i, TOA);
      
      Map<Character, String> rolesMapInverted = new HashMap<Character, String>();
      
      for (Entry<String, Character> entry : rolesMap.entrySet()) { rolesMapInverted.put(entry.getValue(), entry.getKey()); }
      
      Set<Set<String>> filteredHypotheses = new HashSet<Set<String>>();
      
      for (Set<String> hypothesis : Hypotheses) {
    	  Set<String> tempH = new HashSet<String>();
    	  for (String hypothesisAxiom : hypothesis) {
    		  String tempReplace = hypothesisAxiom;
    		  for (Entry<Character, String> entry : rolesMapInverted.entrySet()) {
    			  tempReplace = tempReplace.replace("(" + entry.getKey() + " some", "(" + entry.getValue() + " some");
    		  }
    		  String splitted_input_2[] = nonEntailmentToInput.split(" " + TOA + " ");
    	      String lhsTempReplace = splitted_input_2[0];
    	      String rhsTempReplace = splitted_input_2[1];
    	      
    	      if (!lhsTempReplace.equals(rhsTempReplace)) {
    	    	  tempH.add(tempReplace);
    	      }
    	  }
    	  if (!tempH.isEmpty()) {
    		  filteredHypotheses.add(tempH);
    	  }
      }
      
      Set<String> trivialSolution = new HashSet<String>();
      trivialSolution.add(input);
      filteredHypotheses.add(trivialSolution);
      Set<String> trivialSolutionAxiom = new HashSet<String>();
      trivialSolutionAxiom.add(rend.render(this.axiom));
      filteredHypotheses.add(trivialSolutionAxiom);
      
      return filteredHypotheses;
  }
  
  /**
   * Function that builds a string to display the explanations for an entailment.
 * @return 
   * 
   * @throws OWLException
   * @throws IOException
   */
  public StringBuilder buildExplanationsEntailment(String iriToClear) throws OWLException, IOException {
	  ManchesterOWLSyntaxOWLObjectRendererImpl rend = new ManchesterOWLSyntaxOWLObjectRendererImpl();
	  
	  try
      {
          Set<Set<OWLAxiom>> explanationsSet = getExplanationsEntailment();

          // Build the text for explanations
          StringBuilder explanationsText = new StringBuilder();
          for (Set<OWLAxiom> explanation : explanationsSet) {
              explanationsText.append("Explanation:\n");
              for (OWLAxiom ax : explanation) {
//                  String cleanedAxiomString = ax.toString().replaceAll("http.*?//", "");
//            	  String cleanedAxiomString = ax.toString().replaceAll(iriToClear, "");
                  explanationsText.append(rend.render(ax)).append("\n");
              }
              explanationsText.append("\n"); // Add newline for better separation
          }

          System.out.println(explanationsText);
          return explanationsText;
      }
      catch (Exception exception)
      {
          // Generate and display explanations
          Set<Set<OWLAxiom>> explanationsSet = getExplanationsEntailment();

          // Build the text for explanations
          StringBuilder explanationsText = new StringBuilder();
          for (Set<OWLAxiom> explanation : explanationsSet) {
              explanationsText.append("Explanation:\n");
              for (OWLAxiom ax : explanation) {
//                  String cleanedAxiomString = ax.toString().replaceAll("http.*?//", "");
//            	  String cleanedAxiomString = ax.toString().replaceAll(iriToClear, "");
//                  explanationsText.append(cleanedAxiomString).append("\n");
            	  explanationsText.append(rend.render(ax)).append("\n");
              }
              explanationsText.append("\n"); // Add newline for better separation
          }

          System.out.println(explanationsText);
          return explanationsText;
      }
	  
  }
  
  /**
   * Function that returns the set of explanations for an entailment.
   * 
   * @return
   * @throws OWLException
   * @throws IOException
   */
  public Set<Set<OWLAxiom>> returnExplanationsEntailment() throws OWLException, IOException {
	  try
      {
          Set<Set<OWLAxiom>> explanationsSet = getExplanationsEntailment();

          return explanationsSet;
      }
      catch (Exception exception)
      {
    	  Set<Set<OWLAxiom>> explanationsSet = getExplanationsEntailment();

          return explanationsSet;
      }
  }
}
