// Copyright (C) 2015  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package ti;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * A processor to extract terms from HTML documents.
 */
public class HtmlProcessor implements DocumentProcessor
{

	// P2
	HashSet<String> stopWords = new HashSet<String>();

	/**
	 * Creates a new HTML processor.
	 *
	 * @param pathToStopWords the path to the file with stopwords, or {@code null} if stopwords are not filtered.
	 * @throws IOException if an error occurs while reading stopwords.
	 */
	public HtmlProcessor(File pathToStopWords) throws IOException
	{
		// P2
		// Load stopwords
//		try (BufferedReader br = new BufferedReader(new FileReader(pathToStopWords))) {
//		    String line;
//		    while ((line = br.readLine()) != null) {
//		       // process the line.
//		    	System.out.println(line);
//		    	stopWords.add(line);
//		    }
//		}
		
		this.stopWords = new HashSet();
		List<String> words = Files.readAllLines(pathToStopWords.toPath());
		this.stopWords.addAll(words);
	}

	/**
	 * {@inheritDoc}
	 */
	public Tuple<String, String> parse(String html)
	{
		// P2
		// Parse document
		Document doc;
		doc = Jsoup.parse(html);
		String title = doc.title();
		Element body = doc.body();
		String bodyString = "";
		if(title == null){
			title = "";
		} 
		if(body != null){
			bodyString = body.text();
		}
		System.out.println();
		System.out.println("title -->"+ title);
		System.out.println("body --> "+ bodyString);
		return new Tuple<String, String>(title, bodyString);

		
		//return null; // Return title and body separately
	}

	/**
	 * Process the given text (tokenize, normalize, filter stopwords and stemize) and return the list of terms to index.
	 *
	 * @param text the text to process.
	 * @return the list of index terms.
	 */
	public ArrayList<String> processText(String text)
	{
		ArrayList<String> terms = new ArrayList<>();

		// P2
		// Tokenizing, normalizing, stopwords, stemming, etc. 
		ArrayList<String> tokens = tokenize(text);
		for(String term: tokens) {
			String normalizedTerm = normalize(term);
			if(!isStopWord(normalizedTerm)) {
				terms.add(stem(normalizedTerm));
			}
			
		}

		return terms;
	}

	/**
	 * Tokenize the given text.
	 *
	 * @param text the text to tokenize.
	 * @return the list of tokens.
	 */
	protected ArrayList<String> tokenize(String text)
	{
		ArrayList<String> tokens = new ArrayList<>();

		// P2

		return tokens;
	}

	/**
	 * Normalize the given term.
	 *
	 * @param text the term to normalize.
	 * @return the normalized term.
	 */
	protected String normalize(String text)
	{
		String normalized = null;

		// P2

		return normalized;
	}

	/**
	 * Checks whether the given term is a stopword.
	 *
	 * @param term the term to check.
	 * @return {@code true} if the term is a stopword and {@code false} otherwise.
	 */
	protected boolean isStopWord(String term)
	{
		boolean isTopWord = false;

		// P2
		isTopWord = (stopWords.contains(term))? true : false;

		return isTopWord;
	}

	/**
	 * Stem the given term.
	 *
	 * @param term the term to stem.
	 * @return the stem of the term.
	 */
	protected String stem(String term)
	{
		String stem = null;

		// P2
		Stemmer stemer = new Stemmer();
		stemer.add(term.toCharArray(), term.length());
		stemer.stem();
		stem = stemer.toString();
		
		return stem;
	}

}
