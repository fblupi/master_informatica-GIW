package fblupi.irs;

import news.New;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Search engine to find news that match with an input query
 *
 * @author fblupi
 */
public class SearchEngine {
    /**
     * Search for news that match with an input query
     * @param indexDirectoryName Path where the indexer is stored
     * @param stopWordsFileName Path to the stop words file
     * @param inputQuery Input text to find
     * @return List with news that match with an input query
     */
    public static List<New> search(String indexDirectoryName, String stopWordsFileName, String inputQuery) throws IOException, ParseException {
        List<New> results = new ArrayList<>();

        // Create the analyzer
        SpanishAnalyzer analyzer = new SpanishAnalyzer(Version.LUCENE_43, new CharArraySet(Version.LUCENE_43, Arrays.asList(StringUtils.split(FileUtils.readFileToString(new File(stopWordsFileName), "UTF-8"))), true));

        // Read indexes
        DirectoryReader indexReader = DirectoryReader.open(FSDirectory.open(new File(indexDirectoryName)));

        // Create the index searcher
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        // Parse a simple query that searches for inputQuery
        QueryParser parser = new QueryParser(Version.LUCENE_43, "text", analyzer);
        Query query = parser.parse(inputQuery);

        // Search
        ScoreDoc[] hits = indexSearcher.search(query, null, 1000).scoreDocs;

        // Iterate through the results
        for (ScoreDoc hit: hits) {
            Document hitDoc = indexSearcher.doc(hit.doc);
            String title = hitDoc.get("title");
            String text = hitDoc.get("text");

            results.add(new New(title, text));
        }

        return results;
    }
}
