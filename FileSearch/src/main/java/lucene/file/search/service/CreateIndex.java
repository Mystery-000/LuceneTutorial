package lucene.file.search.service;

import lucene.file.search.model.FileModel;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by  huochao2  on 2018/11/16
 */
public class CreateIndex {
    public static List<FileModel> extractFile()throws IOException {
        ArrayList<FileModel> list = new ArrayList<>();
        File fileDir = new File("WebContent/files");
        File [] allFiles = fileDir.listFiles();
        for (File f:allFiles) {
            FileModel fileModel = new FileModel(f.getName(), ParserExtraction(f));
            list.add(fileModel);
        }
        return  list;
    }
    public static String ParserExtraction(File file) {
        String fileContent = "";  //接受文档内容
        BodyContentHandler bodyContentHandler = new BodyContentHandler();
        Parser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            ParseContext parseContext = new ParseContext();
            ((AutoDetectParser) parser).parse(fileInputStream,bodyContentHandler,metadata,parseContext);
            fileContent = bodyContentHandler.toString();
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (SAXException e) {
            e.printStackTrace();
        }catch (TikaException e) {
            e.printStackTrace();
        }
        return  fileContent;
    }

    public static void main(String[] args) throws IOException {
        //IK分词器对象
        Analyzer analyzer = new IKAnalyzer6x(true);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory directory = null;
        IndexWriter indexWriter = null;
        Path indexPath = Paths.get("WebContent/indexdir");
        FieldType fieldType = new FieldType();
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        fieldType.setStored(true);
        fieldType.setTokenized(true);
        fieldType.setStoreTermVectors(true);
        fieldType.setStoreTermVectorPositions(true);
        fieldType.setStoreTermVectorOffsets(true);
        Date start = new Date();

        if(!Files.isReadable(indexPath)) {
            System.out.println(indexPath.toAbsolutePath()+"不存在或者不可读，请检查！");
            System.exit(1);
        }
        directory = FSDirectory.open(indexPath);
        indexWriter = new IndexWriter(directory,indexWriterConfig);
        ArrayList<FileModel> fileList = (ArrayList<FileModel>)
                extractFile();
        //遍历FileList，创建索引
        for(FileModel f:fileList) {
            Document document = new Document();
            document.add(new Field("title",f.getTitle(),fieldType));
            document.add(new Field("content",f.getContent(),fieldType));
            indexWriter.addDocument(document);
        }
        indexWriter.commit();
        indexWriter.close();
        directory.close();
        Date end = new Date();
        //打印索引耗时
        System.out.println("索引文档完成，共耗时： " + (end.getTime()-start.getTime()) + "毫秒");
    }
}
