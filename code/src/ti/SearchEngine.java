// Copyright (C) 2015  Julián Urbano <urbano.julian@gmail.com>
// Distributed under the terms of the MIT License.

package ti;

import java.io.File;

/**
 * This class is the main entry point to run the search engine.
 * It contains the {@link #main} method.
 */
public class SearchEngine
{
    /**
     * Run the indexing process with the given command-line arguments.
     *
     * @param args the raw command-line arguments.
     * @throws Exception if an error occurs during the process.
     */
    protected static void doIndex(String[] args) throws Exception
    {
        if (args.length < 3 || args.length > 4) {
            SearchEngine.printUsage();
            System.exit(1);
        }
        File pathToIndex = new File(args[1]);
        File pathToCollection = new File(args[2]);
        File pathToStopWords = args.length == 4 ? new File(args[3]) : null;

        // Check console arguments
        if (pathToIndex.exists() && pathToIndex.isFile()) {
            System.err.println("The index path must be a directory.");
            System.exit(1);
        }
        if (!pathToCollection.exists() || pathToCollection.isFile()) {
            System.err.println("Invalid path to document collection.");
            System.exit(1);
        }
        if (pathToStopWords != null && (!pathToStopWords.exists() || !pathToStopWords.isFile())) {
            System.err.println("Invalid path to list of stop words.");
            System.exit(1);
        }

        //Running by $ index "/Volumes/Work/UPF/Class_WEB/lab3-searchengine/2011-index" "/Volumes/Work/UPF/Class_WEB/lab3-searchengine/2011-documents"
        // Build index
//        File stopWords = new File(pathToStopWords);
        DocumentProcessor docProcessor = new HtmlProcessor(pathToStopWords); // P3
        //DocumentProcessor docProcessor = new SimpleProcessor(); 
        Indexer indexer = new Indexer(pathToIndex, pathToCollection, docProcessor);
        indexer.run();
    }

    /**
     * Run the retrieval process in batch mode with the given command-line arguments.
     *
     * @param args the raw command-line arguments.
     * @throws Exception if an error occurs during the process.
     */
    protected static void doBatch(String[] args) throws Exception
    {
        if (args.length != 3) {
            SearchEngine.printUsage();
            System.exit(1);
        }
        File pathToIndex = new File(args[1]);
        File pathToQueries = new File(args[2]);

        // Check console arguments
        if (!pathToIndex.exists() || pathToIndex.isFile()) {
            System.err.println("Index directory does not exist.");
            System.exit(1);
        }
        if (!pathToQueries.exists() || !pathToQueries.isFile()) {
            System.err.println("Query file does not exist.");
            System.exit(1);
        }

        // Read index
        System.err.print("Loading index...");
        Index ind = new Index(pathToIndex.getPath());
        ind.load();
        System.err.println("done. Statistics:");
        ind.printStatistics();

        // Instantiate retriever and run
        //DocumentProcessor docProcessor = new SimpleProcessor(); // P3
        // Stop word path
        File stopWords = new File("/Volumes/Work/UPF/Class_WEB/lab3-searchengine/code/src/stop-words.txt");
        DocumentProcessor docProcessor = new HtmlProcessor(stopWords); // P3
        //DocumentProcessor docProcessor = new SimpleProcessor();
        RetrievalModel cosine = new Cosine(); // P4
        Batch batch = new Batch(pathToQueries, cosine, ind, docProcessor);
        batch.run();
    }

    /**
     * Run the retrieval process in interactive mode with the given command-line arguments.
     *
     * @param args the raw command-line arguments.
     * @throws Exception if an error occurs during the process.
     */
    protected static void doInteractive(String[] args) throws Exception
    {
        if (args.length != 2) {
            SearchEngine.printUsage();
            System.exit(1);
        }
        File pathToIndex = new File(args[1]);

        // Check console arguments
        if (!pathToIndex.exists() || pathToIndex.isFile()) {
            System.err.println("Index directory does not exist.");
            System.exit(1);
        }

        // Read index
        System.err.print("Loading index...");
        Index ind = new Index(pathToIndex.getPath());
        ind.load();
        System.err.println("done. Statistics:");
        ind.printStatistics();

        // Instantiate retriever and run
        // Stop word path
        File stopWords = new File("/Volumes/Work/UPF/Class_WEB/lab3-searchengine/code/src/stop-words.txt");
        DocumentProcessor docProcessor = new HtmlProcessor(stopWords); // P3
        //DocumentProcessor docProcessor = new SimpleProcessor();
        RetrievalModel cosine = new Cosine(); // P4
        Interactive inter = new Interactive(cosine, ind, docProcessor);
        inter.run();
    }

    public static void main(String[] args) throws Exception
    {
    
        if (args.length < 1) {
            SearchEngine.printUsage();
            System.exit(1);
        }
        switch (args[0].toLowerCase()) {
            case "index":
                SearchEngine.doIndex(args);
                break;
            case "batch":
                SearchEngine.doBatch(args);
                break;
            case "interactive":
                SearchEngine.doInteractive(args);
                break;
            default:
                SearchEngine.printUsage();
                System.exit(1);
        }
    }

    protected static void printUsage()
    {
        System.err.println("Usage: ti.SearchEngine <command> <options>");
        System.err.println();
        System.err.println("where <command> and <options> are one of:");
        System.err.println("  - index <path-to-index> <path-to-collection> [<path-to-stopwords>]");
        System.err.println("  - batch <path-to-index> <path-to-queries>");
        System.err.println("  - interactive <path-to-index>");
    }
}