package lucene.file.search.model;

/**
 * Created by  huochao2  on 2018/11/16
 */
public class FileModel {
    private String title; //文件标题
    private String content; //文件内容

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public FileModel() {
    }

    public FileModel(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
