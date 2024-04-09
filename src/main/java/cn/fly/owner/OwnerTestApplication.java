package cn.fly.owner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OwnerTestApplication {
    public static final String FILE_DIR = "/Users/gaoshuo/tempIndex";
    public static void main(String[] args) throws IOException {
        OwnerTestApplication ownerTestApplication = new OwnerTestApplication();
        ownerTestApplication.createLuceneIndex(FILE_DIR);
        ownerTestApplication.queryLuceneByTerm(FILE_DIR);
    }

    //根据内容创建索引
    public void createLuceneIndex(String dataDir) throws IOException {

        Analyzer analyzer = new StandardAnalyzer();
        Path indexPath = Paths.get(dataDir);
        Directory directory = FSDirectory.open(indexPath);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        String text = "This is the text to be indexed.";
        Document doc = new Document();

        // fieldname:类似数据库中的字段，text:类似字段类型
        doc.add(new Field("fieldname", text, TextField.TYPE_STORED));

        // 会生成5个文件：*.cfe,*.cfs,*.si,segements_1,write_lock
        iwriter.addDocument(doc);

        iwriter.close();
        directory.close();
    }

    // 根据关键词查询内容
    public void queryLuceneByTerm(String dataDir) throws IOException {
        Path indexPath = Paths.get(dataDir);
        Directory directory = FSDirectory.open(indexPath);
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        // indexed:即为要查询的条件，fieldname:即为索引字段
        Term term = new Term("fieldname","indexed");
        Query query = new TermQuery(term);
        ScoreDoc[] hits = isearcher.search(query, 10).scoreDocs;

        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);

            // 根据索引字段获取对应的内容
            System.out.println(hitDoc.get("fieldname"));
        }
        ireader.close();
        directory.close();
    }
}
