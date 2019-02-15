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
	{/*
	  index: This gives access to vocabulary, documents, invertedIndex, directedIndex
	  docProcessor: use this to process the text of the query (parsing)
	  */
		//Tuple<String, String> parsedText = docProcessor.parse(queryText);
		Tuple<String, String> parsedText = docProcessor.parse(queryText);
		//System.out.println("parsedText: "+parsedText.toString());
		//ArrayList<String> processedText = docProcessor.processText(queryText);
		// P1
		// Extract the terms from the query
		ArrayList<String> terms = docProcessor.processText(queryText);

		
		// Calculate the query vector
		ArrayList<Tuple<Integer, Double>> queryVector = computeVector(terms, index);
		// Calculate the document similarity
		ArrayList<Tuple<Integer, Double>> scores = computeScores(queryVector, index);
		return scores; // return results
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
		Double sumWeightSq = 0.0;
		// P1
		// HashMap sims keeps [docId] as key and [similarity] as value.
		HashMap sims = new HashMap<Integer, Double>();
		int sizeQueryVector = queryVector.size();
		
		
		// for each term in queryVector:
		for(int  v=0; v < sizeQueryVector; v++) {
			// get term object from vector (termId, weight)
			Tuple<Integer, Double> term = queryVector.get(v);
			Integer termId = term.item1;
			// w_tq
			Double weightTermQuery = term.item2;
			
			//for each tuple(document) in its invertedIndex
			// - here we get all relevant documents with a specific term.
			// - invertedIndex return list of docId and weight
			ArrayList<Tuple<Integer, Double>> termDocuments = index.invertedIndex.get(termId);
			//System.out.println("Number of documents contain termID["+termId+"] is "+termDocuments.size());
			
			// - Calculate weight of the term for each document.
			for(Tuple<Integer, Double> termInDoc: termDocuments) {
				// Multiply  w_td * w_tq	
				Integer docId = termInDoc.item1;
				// w_td
				Double weightTermDoc = termInDoc.item2;
				
				Double weight =  weightTermDoc * weightTermQuery;
				
				sumWeightSq += Math.pow(weightTermQuery, 2.0);
				
				//Accumulate the previous result to sims[docId] 
				//you are calculating here sum of the cosine similarity (numerator)
				
				if(sims.containsKey(docId)) {
					Double prevWeight = (Double)sims.get(docId);
					Double sumWeight = prevWeight + weight;
					// Accumulate the previous result to sims[docId]
					sims.put(docId, sumWeight);
				}else {
					sims.put(docId, weight);
				}
			}
		}
		
		
		//ind.documents.get(docID).item2 += Math.pow(tf * idf, 2.0);
		
		Double norm = Math.sqrt(sumWeightSq);
		
		// find denominator for vector d and q
		// for each tuple in sims:
		
		Iterator it = sims.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry sim = (Map.Entry)it.next();
		        Integer docId = (Integer)sim.getKey();
		        
		        // Divide the similarity by q and d denominator of cosine similarity
		        //Get document by docId to get norm (DocName, Norm)
		        Tuple<String, Double> doc = index.documents.get(docId);
		        Double docNorm = doc.item2;
		        //System.out.println("DocId["+docId+"] has norm["+docNorm+"]");
		        //TODO confirm if docNorm needs to do sqrt or not.
		        Double docWeight = (Double)sim.getValue();
		        //double normTerm = Math.sqrt(Math.pow(docWeight,2.0));
		        Double similarity = docWeight/(docNorm * norm);
		        //Double similarity = 0d;
		        //Add the tuple to results
		       results.add(new Tuple(docId, similarity));
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
		/*
		terms: use this to calculate the tf  ==> 1 + log(freq_ti)
		(freq_ti): how many times t_i appear in the query
		
		index: 	use this to get the "termId" and "iDF" (iDF already in the index, no need to calculate it)
				for each term of the query (through vocabulary hashmap)
	 	*/
		//System.out.println("Terms: "+terms.toString());
		HashSet<String> termSet = new HashSet<String>(terms);

		for (String term : termSet) {
			//Get term ID and iDF
			Tuple<Integer, Double> indexTerm = index.vocabulary.get(term);
			
			Integer termId = indexTerm.item1;
			Double iDF = indexTerm.item2;
			//System.out.println("Term ID: "+termId +" iDF: "+ iDF);
			
			//calculate tf from 1 + log(freq_ti)
			// from slide tf_ij = number of occurrence of term i in document j
			//(freq_ti)  => how many time t_i appears in the query

			
			// TODO confuse how to find the frequence of the term in the document
			// but professor said that "how many times t_i appear in the query"
			//TODO confirm the way to find tf
			
			//int freq = Collections.frequency(terms, term);
			//TODO confirm with processor if it's log based 2 or 10
//			double tf =  1 + Math.log(freq_ti);
			//double tf =  1 + Math.log(freq) / Math.log(2);
			// Compute weight and add posting
            double tf = 1.0 + Math.log(Collections.frequency(terms, term));
			
			//weight_ij = weight assigned to term i in document j
			//tf_ij number of occurrence of term i in document j
			
			
			//Since iDF is calculated so we don't have to calculate here (Math.log(N/ni))
			//- N = number of document in entire collection
			//- int N = index.documents.size();
			//- ni = number of documents with term i.
			//- int ni = index.invertedIndex.get(termId).size();
			
			Double weight = tf * iDF;
			// Add term ID and weight
			//System.out.println("Term ID: "+termId +", Weight: "+weight);
			vector.add(new Tuple(termId, weight));
			
		}

		return vector;
	}
}
