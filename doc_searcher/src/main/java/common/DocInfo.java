package common;

/**
 * @ClassName: DocInfo
 * @Description: 一个文档对象（HTML对象)
 * @Author: Ma Yuanyuan
 */
public class DocInfo {
    //文档唯一的身份标识
    private int docId;
    //该文档的标题，用文件名来表示
    private String title;
    //该文档对应的线上文档的url，根据本地文档去拼接
    private String url;
    //该文档的正文，把html标签去掉，留下的内容
    private String content;

    @Override
    public String toString() {
        return "DocInfo{" +
                "docId=" + docId +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
