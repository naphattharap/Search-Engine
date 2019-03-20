// Copyright (C) 2015  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package ti;

import java.util.*;
import java.lang.Math;

/**
 * Implements retrieval in a vector space with the cosine similarity function and a TFxIDF weight formulation.
 */
public class Cosine implements RetrievalModel
{
	public Cosine()
	{
		// empty
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Tuple<Integer, Double>> runQuery(String queryText, Index index, DocumentProcessor docProcessor)
	{
		// Arguments
	  	// (1) index: This gives access to vocabulary, documents, invertedIndex, directedIndex
	  	// (2) docProcessor: use this to process the text of the query (parsing)
		
		// P1
		
		// Extract the terms from the query.
		// Query text can come from running application in interactive mode (enter text through console) 
		// or reading from topic.xml file when running application in batch mode.
		// Call existing function to get term
		// Example: query = What is the weather in Barcelona? I like Barcelona.
		// Result will be [weather, Barcelona, Barcelona)
		ArrayList<String> terms = docProcessor.processText(queryText);

		// Calculate the query vector
		// We will get array of Tuple like {term1, weight1}, {term2, weight2} here.
		// and the terms are unique.
		ArrayList<Tuple<Integer, Double>> queryVector = computeVector(terms, index);
		
		// Calculate the document similarity
		// We get {doc1, similarity}, {doc2, similarity} from here.
		ArrayList<Tuple<Integer, Double>> results = computeScores(queryVector, index);
		
		// The correctness can be confirmed by running batch mode and save output to 2011.run file
		// then compare result with CorrectOutput file.
		
		/* Testing: To test this, check SearchEngine class if SimpleProcessor is used, not HTMLProcessor.
		 * Run batch mode ex. --> 
		 java ti.SearchEngine batch "/Volumes/Work/UPF/Class_WEB/lab3-searchengine/p1/2011-index" 
		 "/Volumes/Work/UPF/Class_WEB/lab3-searchengine/2011-topics.xml" > 2011_2.run
		 */
		return results;
	}

	/**
	 * Returns the list of documents in the specified index sorted by similarity with the specified query vector.
	 *
	 * @param queryVector the vector with query term weights.
	 * @param index       the index to search in.
	 * @return a list of {@link Tuple}s where the first item is the {@code docID} and the second one the similarity score.
	 */
	protected ArrayList<Tuple<Integer, Double>> computeScores(ArrayList<Tuple<Integer, Double>> queryVector, Index index)
	{
		ArrayList<Tuple<Integer, Double>> results = new ArrayList<>();
		
		
		// Arguments:
		// queryVector: unique value of {term1, weight1} {term2, weight2} from computeVector method.
		// index: This gives access to vocabulary, documents, invertedIndex, directedIndex
		
		// P1
		
		// Declare variable to store value of squared weight to be used for calculate similarity score.
		// This is denominator for |q|
		Double sumWeightSq = 0.0;
		
		// Initialize HashMap that store key and value object
		// while the key is docId and value is similarity.
		HashMap<Integer, Double> sims = new HashMap<Integer, Double>();

		
		// for each unique term in queryVector, we loop through term and calculate document weight for each term
		// if the document ID does exist for the term before, we add up the weight to that doc ID.
		
		for(Tuple<Integer, Double> term: queryVector) {
			// Get term ID from term object.
			Integer termId = term.item1;
			// Get term weight that calculate from queryVector method
			Double weightQuery = term.item2;
			
			// Calculate sum of squared weight query which will be used to calculate denominator |q| later. 
			sumWeightSq += Math.pow(weightQuery, 2.0);
			
			// Get documents that contains the term by calling invertedIndex and define termId as parameter.
			// For each tuple(document) in its invertedIndex
			// - here we get all relevant documents with a specific term.
			// - invertedIndex return list of docId and weight
			ArrayList<Tuple<Integer, Double>> termDocuments = index.invertedIndex.get(termId);
			//System.out.println("Number of documents contain termID["+termId+"] is "+termDocuments.size());
			
			// - Calculate weight of the term for each document.
			for(Tuple<Integer, Double> termInDoc: termDocuments) {
				// Doc ID that the term exist.
				Integer docId = termInDoc.item1;
				// Weight of term in document
				Double weightDoc = termInDoc.item2;
				// Weight of term in query
				Double weight =  weightDoc * weightQuery;
				
				// We check if in similarity HashMap contain the DocID or not.
				// If yes, we add up the current weight with previous weight (sum of cosine similarity)
				// otherwise, we added a new Doc ID and weight
				if(sims.containsKey(docId)) {
					Double prevWeight = (Double)sims.get(docId);
					Double sumWeight = prevWeight + weight;
					// Accumulate the previous result to the same DocID
					sims.put(docId, sumWeight);
				}else {
					// DocID has never exist in HashMap, so add new DocID here.
					sims.put(docId, weight);
				}
			}
		}

		// Computing |q| norm as a denominator of finding final similarity score.
		Double queryNorm = Math.sqrt(sumWeightSq);
		
		// Loop through all items in HashMap to calculate similarity score for all Doc IDs.
		for(Map.Entry<Integer, Double> sim:  sims.entrySet()) {
		    	// Get each object from HashMap
		        //Map.Entry<Integer, Double> sim = (Map.Entry<Integer, Double>)it.next();
		        // Get key (DocID) from object.
		        Integer docId = (Integer)sim.getKey();
		        // Get value (Doc Weight from above calculation) from object.
		        Double docWeight = (Double)sim.getValue();
		        
		        // Get the document by passing DocID to index.documents.get(...) to get norm value (DocName, Norm)
		        // document norm value is calculate from core process, we just use it.
		        Tuple<String, Double> doc = index.documents.get(docId);
		        Double docNorm = doc.item2;
		        
		        // Take formula from presentation that used to calculate similarity.
		        // Divide the similarity by |q| and |d| denominator of cosine similarity
		        Double similarity = docWeight/(docNorm * queryNorm);
		        //Add the tuple to results
		       results.add(new Tuple<Integer, Double>(docId, similarity));
		    }

		// Sort documents by similarity and return the ranking
		Collections.sort(results, new Comparator<Tuple<Integer, Double>>()
		{
			@Override
			public int compare(Tuple<Integer, Double> o1, Tuple<Integer, Double> o2)
			{
				return o2.item2.compareTo(o1.item2);
			}
		});
		return results;
	}

	/**
	 * Compute the vector of weights for the specified list of terms.
	 *
	 * @param terms the list of terms.
	 * @param index the index
	 * @return a list of {@code Tuple}s with the {@code termID} as first item and the weight as second one.
	 */
	protected ArrayList<Tuple<Integer, Double>> computeVector(ArrayList<String> terms, Index index)
	{ 
		ArrayList<Tuple<Integer, Double>> vector = new ArrayList<>();
		// P1
		
		// Arguments:
		// (1) terms: use this to calculate the tf --> double tf = 1.0 + Math.log(Collections.frequency(terms, term));
		// Collections.frequency(terms, term): how many times t_i appear in the query
		
		// (2) index: use this to get the "termId" and "iDF" (iDF already in the index, no need to calculate it)
		// for each term of the query (through vocabulary hashmap)
		
		// Initialize HashSet 
		// HashSet can store only unique value.
		// Then we pass "terms" to HashSet, we will get unique term as a result.
		// Example extracted terms from our sentence --> weather, barcelona, bacelona
		// Result from passing terms to HashSet will be --> weather, barcelona
		HashSet<String> termSet = new HashSet<String>(terms);

		// Loop through all unique terms. (weather, barcelona)
		for (String term : termSet) {
			//Get term ID and iDF
			Tuple<Integer, Double> indexTerm = index.vocabulary.get(term);
			if (indexTerm != null) {
			Integer termId = indexTerm.item1;
			Double iDF = indexTerm.item2;
			//System.out.println("Term ID: "+termId +" iDF: "+ iDF);
			
			//Since iDF is calculated so we don't have to calculate here (Math.log(N/ni))
			//- N = number of document in entire collection (int N = index.documents.size();)
			//- ni = number of documents with term i.
			
			// How many time the term appears in list of terms 
			// can be calculated from Collections.frequency(terms, term)
            double tf = 1.0 + Math.log(Collections.frequency(terms, term));

			// Calculate weight of term 
			Double weight = tf * iDF;

			// Add term ID and weight to our list.
			//System.out.println("Term ID: "+termId +", Weight: "+weight);
			vector.add(new Tuple<Integer, Double>(termId, weight));
			}
		}
		return vector;
	}
}
