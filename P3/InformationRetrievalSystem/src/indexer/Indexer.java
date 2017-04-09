package indexer;

import news.New;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.apache.lucene.index.DirectoryReader.indexExists;

/**
 * Indexer to index documents in a directory using stop words
 */
public class Indexer {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        new Indexer("data/efe/", "data/palabras_vacias_utf8.txt", "data/indexes/");
    }

    /**
     * Build a new indexer for a documents using stop words
     * @param documentsDirectoryName Path to the directory where the documents that will be indexed are stored
     * @param stopWordsFileName Path to the stop words file
     * @param indexDirectoryName Path where the indexer will be stored
     */
    public Indexer(String documentsDirectoryName, String stopWordsFileName, String indexDirectoryName) throws ParserConfigurationException, IOException, SAXException {
        // Create the analyzer
        SpanishAnalyzer analyzer = new SpanishAnalyzer(Version.LUCENE_43, importStopWords(stopWordsFileName));

        // Read news from a directory
        List<New> news = getNewsFromADirectory(documentsDirectoryName);

        // Create the index writer
        IndexWriter indexWriter = new IndexWriter(FSDirectory.open(new File(indexDirectoryName)), new IndexWriterConfig(Version.LUCENE_43, analyzer));

        // If the index already exists delete it
        if (indexExists(FSDirectory.open(new File(indexDirectoryName)))) {
            indexWriter.deleteAll();
        }

        // Iterate each document
        for (New n : news) {
            // Get document data
            String title = n.getTitle();
            String text = n.getText();

            // Add data to the indexer
            Document doc = new Document();
            doc.add(new Field("title", title, StringField.TYPE_STORED));
            doc.add(new Field("text", text, TextField.TYPE_STORED));
            indexWriter.addDocument(doc);
        }

        // Close the index writer
        indexWriter.close();
    }

    /**
     * Build a CharArraySet with the stop words stored in a file
     * @param stopWordsFilename Path to the stop words file
     * @return CharArraySet with the stop words
     */
    private CharArraySet importStopWords(String stopWordsFilename) throws IOException {
        return new CharArraySet(Version.LUCENE_43, Arrays.asList(StringUtils.split(FileUtils.readFileToString(new File(stopWordsFilename), "UTF-8"))), true);
    }


    /**
     * Get a list of news from a list of SGML files
     * @param documentsDirectoryName Path to the directory where the documents are stored
     * @return List with the news
     */
    private List<New> getNewsFromADirectory(String documentsDirectoryName) throws ParserConfigurationException, IOException, SAXException {
        List<New> news = new ArrayList<>();

        // Get file names
        List<String> fileNames = getSGMLFileNamesFromADirectory(documentsDirectoryName);

        // Iterate each file
        for (String fileName : fileNames) {
            // Modify file content to have a well-formed format
            String content = FileUtils.readFileToString(new File(documentsDirectoryName + fileName), "ISO-8859-1");
            content = content.replace("< ", "  ");
            content = content.replace("&", "");
            content = content.replace("<\n<", "\n<");
            content = content.replace(";<", ";");
            content = "<SGML>" + content + "</SGML>";

            // Get DOM object from the content
            org.w3c.dom.Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(new String(content.getBytes("ISO-8859-1"), "UTF-8").getBytes("UTF-8")));
            document.getDocumentElement().normalize();

            // Get nodes
            NodeList nodes = document.getElementsByTagName("DOC");

            // Iterate each node
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    // Get element title and text
                    Element element = (Element) node;
                    String title = element.getElementsByTagName("TITLE").item(0).getTextContent();
                    String text = element.getElementsByTagName("TEXT").item(0).getTextContent();

                    // Add to the output
                    news.add(new New(title, text));
                }
            }
        }

        return news;
    }

    /**
     * Get SGML file names in a directory
     * @param documentsDirectoryName Path to the directory where the documents are stored
     * @return List of file names
     */
    private List<String> getSGMLFileNamesFromADirectory(String documentsDirectoryName) {
        List<String> fileNameList = new ArrayList<>();

        // Get files in a directory
        File[] filesInDirectory = new File(documentsDirectoryName).listFiles();

        // Iterate each file with SGML format
        for (File file : filesInDirectory) {
            if (file.isFile() && FilenameUtils.getExtension(file.getPath()).toUpperCase().equals("SGML")) {
                fileNameList.add(file.getName());
            }
        }

        return fileNameList;
    }

}
