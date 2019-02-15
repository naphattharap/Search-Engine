package ti;

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
	}

}
