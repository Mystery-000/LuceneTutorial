package lucene.file.search.controller;

import lucene.file.search.model.FileModel;
import lucene.file.search.service.IKAnalyzer6x;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Created by  huochao2  on 2018/11/19
 */
public class SearchFileServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws
            ServletException, IOException {
        String indexPathDir = request.getServletContext().getRealPath("indexdir");
        //接收查询字符串
        String query = request.getParameter("query");
        //编码格式转换
        query = new String(query.getBytes("iso8859-1"),"UTF-8");
        if(query.equals("") || query == null) {
            System.out.println("参数错误！");
            request.getRequestDispatcher("error.jsp").forward(request,response);
        }else {
            ArrayList<FileModel> histList = getTopDoc(query,indexPathDir,100);
            System.out.println("共搜到：" + histList.size() + "条数据！");
            request.setAttribute("histList",histList);
            request.setAttribute("queryback",query);
            request.getRequestDispatcher("result.jsp").forward(request,response);
        }
        System.out.println("SearchFileServlet!");
        }
    protected void doPost(HttpServletRequest request,HttpServletResponse response)
        throws  ServletException,IOException {
        doGet(request,response);
    }
    public static ArrayList<FileModel> getTopDoc(String key,String indexPathStr,int N) {
        ArrayList<FileModel> histList = new ArrayList<FileModel>();
        String[] fileds = {"title","content"};
        Path indexPath = Paths.get(indexPathStr);
        Directory directory;
        try {
            directory = FSDirectory.open(indexPath);
            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            Analyzer analyzer = new IKAnalyzer6x(true);
            MultiFieldQueryParser parser = new MultiFieldQueryParser(fileds,analyzer);
            //查询字符串
            Query query = parser.parse(key);
            TopDocs topDocs = indexSearcher.search(query,N);
            //定制高亮标签
            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"colo " +
                    "r:red;\">","</span>");
            QueryScorer scoreTitle = new QueryScorer(query,fileds[0]);
            Highlighter hlqTitle = new Highlighter(formatter,scoreTitle);
            QueryScorer scoreContent = new QueryScorer(query,fileds[1]);
            Highlighter hlqContent = new Highlighter(formatter,scoreContent);
            TopDocs hits = indexSearcher.search(query,100);
            for (ScoreDoc sd : topDocs.scoreDocs) {
                Document document = indexSearcher.doc(sd.doc);
                String title = document.get("title");
                String content = document.get("content");
                TokenStream tokenStream = TokenSources.getAnyTokenStream(indexSearcher.getIndexReader(),
                        sd.doc,fileds[0],new IKAnalyzer6x()); //获取tokenStream
                Fragmenter fragmenter = new SimpleSpanFragmenter(scoreTitle);
                hlqTitle.setTextFragmenter(fragmenter);
                String hl_title = hlqTitle.getBestFragment(tokenStream,title);
                //获取高亮片段，可以对其数量进行限制
                tokenStream = TokenSources.getAnyTokenStream(indexSearcher.getIndexReader(),
                        sd.doc,fileds[1],new IKAnalyzer6x());
                fragmenter = new SimpleSpanFragmenter(scoreContent);
                hlqContent.setTextFragmenter(fragmenter);
                String hl_content = hlqContent.getBestFragment(tokenStream,content);
                FileModel fileModel = new FileModel(hl_title!=null ? hl_title:title,
                        hl_content!=null ? hl_content:content);
                histList.add(fileModel);
            }
            directory.close();
            indexReader.close();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (ParseException e) {
            e.printStackTrace();
        }catch (InvalidTokenOffsetsException e) {
            e.printStackTrace();
        }
        return histList;
    }
}
