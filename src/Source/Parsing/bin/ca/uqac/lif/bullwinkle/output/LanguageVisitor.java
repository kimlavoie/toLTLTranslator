package Source.Parsing.bin.ca.uqac.lif.bullwinkle.output;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.bullwinkle.ParseNode;
import ca.uqac.lif.bullwinkle.output.OutputFormatVisitor;

public class LanguageVisitor implements OutputFormatVisitor {

	String token = null, value = null;
	String new_Token = null, ltl_Token = null;
	String tokenWithSpace = null;
	int visitCount = 0; //to avoid considering the first "<exp>"
	boolean for_statement = false; //for the ':' in "for all" and "for some" statements

	Stack<String> parStack; //for the balance of parenthesis
	StringBuilder sb; //to form the LTL-FO output string
	Map<String, String> map; //for the new language - LTL-FO equivalency
	
	Pattern xPathRegex = Pattern.compile("^/[/\\w\\]\\[\"=]+");
	
	public LanguageVisitor() {
		super();
		sb = new StringBuilder();
		map = new LinkedHashMap<String, String>();//to get the corresponding symbols of terminals
		parStack = new Stack<String>();
	}
	
	public void fillMap() {
		
		// open the file that contains the equivalences between new language and LTL-FO+ symbols
		// and fill the LinkedHashMap with them
		try {
			BufferedReader reader = new BufferedReader(new FileReader("data/newToLTL-FO.txt"));
			while (true) {
				new_Token = reader.readLine();
				if (new_Token == null) 
					break;
				ltl_Token = reader.readLine();
				map.put(new_Token, ltl_Token);
			}
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		 
	}
	
	

	public void visit(ParseNode node) {		
		
		visitCount++;
		List<ParseNode> list = null; //to get the node's children
		list = node.getChildren();
		
		token = node.getToken();
		if (list.isEmpty()) { //if node is a leaf
			
			if (token.equals("FOR") || token.equals("MAYBE") || token.equals("NEXT")) 
			{
				tokenWithSpace = token;
				return;
			}
			
			if (tokenWithSpace != null) //previous token was "FOR", "MAYBE" or "NEXT"
			{
				token = tokenWithSpace + " " + token;
				tokenWithSpace = null;
			}	
			
			//get the value of the token in LTL-FO+
			if (map.containsKey(token)) 
			{
				for (String key : map.keySet()) //iterate over each map key
				{
				    if (key.equals(token)) //if a key matches the node's token
				    {
				    	value = map.get(key); //get the value linked to the key
				    	break;
				    }
				}
				if (!value.equals("#")) //else append nothing
				{ 
					sb.append(value + " ");
				}
			}
			else 
			{
				if (!token.equals("(")) //'(' are already displayed at each new "<exp>"
				{
					if (token.equals(")")) 
					{
						parStack.pop();
					}
					sb.append(token + " ");
				}
				
			}
			if (for_statement) //if the program is writing a "for all" or "for some" statement
			{ 
				Matcher m = xPathRegex.matcher(token);
				if (m.matches()) //if the token is an xPath, a colon must follow
				{ 
					sb.append(": ");
					for_statement = false;
				}
			}
			
		}
		else if (token.equals("<exp>")) //at each new expression, begin with '('
		{ 
			if (visitCount > 1) //to avoid putting a '(' at the very beginning of the string
			{ 
				sb.append("(");
				parStack.push(token);
			}
			for_statement = false;
		}
		else if (token.equals("<for_all>") || token.equals("<exists>")) 
		{
			for_statement = true;
		}
	}

	public void closeParenthesis() 
	{
		while(!parStack.isEmpty()) //append the remaining closing parenthesis
		{ 
			parStack.pop();
		    sb.append(")");
		}
	}
	
	public String toOutputString() 
	{
		return sb.toString(); //the LTL-FO output string
	}

	
	public void pop() {
		// ?

	}
}
