import java.io.File;
import java.io.IOException;

import Source.Parsing.bin.ca.uqac.lif.bullwinkle.output.LanguageVisitor;
import ca.uqac.lif.bullwinkle.BnfParser;
import ca.uqac.lif.bullwinkle.ParseNode;
import ca.uqac.lif.bullwinkle.BnfParser.InvalidGrammarException;
import ca.uqac.lif.bullwinkle.BnfParser.ParseException;
import ca.uqac.lif.util.FileReadWrite;

public class Translator {

	public static void main(String[] args) {
		LanguageVisitor visitor = new LanguageVisitor();
		String ltl_string = "";
		String inputFilePath = null;//"data/inputString.txt "; /data/codeToParse.txt
		String outputFilePath = null;//"data/outputFile.txt"
		String grammarPath = "data/grammar.bnf";
		
		inputFilePath = args[0];
		outputFilePath = args[1];
		
		visitor.fillMap();
	  	try
	    {		  
	  	 
	      BnfParser parser = new BnfParser(new File(grammarPath));
	      ParseNode node1 = parser.parse(FileReadWrite.readFile(inputFilePath));
	      
	      node1.prefixAccept(visitor);
	      visitor.closeParenthesis();
	      ltl_string = visitor.toOutputString();
	      FileReadWrite.writeToFile(outputFilePath, ltl_string);
	      System.out.println(ltl_string);
	    } catch (IOException e){
	    	e.printStackTrace();
	    } catch(InvalidGrammarException e) {
	  		e.printStackTrace();
	  	} catch (ParseException e) {
			e.printStackTrace();
		} 

	}

}
