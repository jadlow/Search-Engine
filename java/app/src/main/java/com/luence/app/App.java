/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.luence.app;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * refer to <a href="https://www.lucenetutorial.com/lucene-in-5-minutes.html"/>
 */
public class App {

    private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new StringField("isbn", isbn, Field.Store.YES));
        w.addDocument(doc);
    }
//    private static void addDoc(IndexWriter w, String seq_id, String med_id, String title, String pub_type, String ab, String author, String source) throws IOException {
//        Document doc = new Document();
//        doc.add(new StringField("seq_id", seq_id, Field.Store.YES));
//        doc.add(new StringField("med_id", med_id, Field.Store.YES));
//        /*splits text into strings (each string in text is an index*/
//        doc.add(new TextField("title", title, Field.Store.YES));
//        doc.add(new StringField("pub_type", pub_type, Field.Store.YES));
//        doc.add(new TextField("ab", ab, Field.Store.YES));
//        doc.add(new StringField("author", author, Field.Store.YES));
//        /*treats entire string as an index*/
//        doc.add(new StringField("source", source, Field.Store.YES));
//        /*choose minimum size field to contain information and speed up query*/
//        w.addDocument(doc);
//    }

    public static void main(String[] args) throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        Directory index = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

//        try {
//            File file = new File("C:\\Users\\jacob\\Desktop\\CSE272\\CSE272_UCSC_Spring\\HW1\\java\\ohsumed.88-91");
//
//            Scanner input = new Scanner(file);
//            input.useDelimiter(".I|.U|.S|.M|.T|.P|.W|.A");
//            int count = 0;
//            while (input.hasNext()) {
//                String line = input.next();
//                System.out.println(line);
//                break;
//            }
//            input.close();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        String content = Files.readString(new File("C:\\Users\\jacob\\Desktop\\CSE272\\CSE272_UCSC_Spring\\HW1\\java\\ohsumed.88-91").toPath(), StandardCharsets.UTF_8);

        // Split input into individual documents
        String[] documents = content.split("(?m)^\\.I\\s");

        // Initialize ArrayLists for each document field
        ArrayList<String> seq_ids = new ArrayList<>();
        ArrayList<String> med_ids = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> pub_types = new ArrayList<>();
        ArrayList<String> abs = new ArrayList<>();
        ArrayList<String> authors = new ArrayList<>();
        ArrayList<String> sources = new ArrayList<>();

        // Parse each document and store the fields in ArrayLists
        // handle incomplete documents **************************
        for (String doc : documents) {
            String[] fields = doc.split("(?m)^\\.(?!I)");
            if (fields.length < 7) {
                System.out.println(fields[0]);
                continue;
            }
            seq_ids.add(fields[0].trim());
            med_ids.add(fields[1].substring(2).trim());
            titles.add(fields[2].substring(2).trim());
            pub_types.add(fields[3].substring(2).trim());
            abs.add(fields[4].substring(2).trim());
            authors.add(fields[5].substring(2).trim());
            sources.add(fields[6].substring(2).trim());
        }

        // Print the parsed fields for each document
        for (int i = 0; i < 10; i++) {
            System.out.println("Document " + seq_ids.get(i));
            System.out.println("User: " + med_ids.get(i));
            System.out.println("Source: " + titles.get(i));
            System.out.println("Authors: " + pub_types.get(i));
            System.out.println("Abstract: " + abs.get(i));
            System.out.println("Title: " + authors.get(i));
            System.out.println("Text: " + sources.get(i));
            System.out.println();
        }

        try (IndexWriter w = new IndexWriter(index, config)) {
            addDoc(w, "Lucene in Action", "193398817");
            addDoc(w, "Lucene for Dummies", "55320055Z");
            addDoc(w, "Managing Gigabytes", "55063554A");
            addDoc(w, "The Art of Computer Science", "9900333X");
        }

        String querystr = args.length > 0 ? args[0] : "lucene";
        Query q = new QueryParser("title", analyzer).parse(querystr);

        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.getIndexReader().document(docId);
            System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
        }
    }
}
