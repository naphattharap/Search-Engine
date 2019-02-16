package ti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String stem = null;
		
		String term = "singers";
		// P2
		Stemmer stemer = new Stemmer();
		stemer.add(term.toCharArray(), term.length());
		stemer.stem();
		
		System.out.println(stemer.toString());
		
		
		ArrayList<String> tokens = new ArrayList<>();

		// P2
		String  text = "What is the weather in Barcelona on 15 Feb. 2019?";
		List<String> listTokens = Arrays.asList(text.toLowerCase().replaceAll("[^a-z0-9']", " ").split("\\s+"));
		for(String token: listTokens) {
			tokens.add(token);
		}
		System.out.println(tokens);
	}

}
