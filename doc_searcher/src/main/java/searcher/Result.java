package searcher;

/**
 * @ClassName: Result
 * @Description: 搜索结果
 * @Author: Ma Yuanyuan
 */
//表示一条搜索结果，根据DocInfo得到
public class Result {
    private String title;
    //在当前场景中url假设为一样的
    private String showUrl;
    private String clickUrl;
    //描述，网页内容的摘要内容，是查询词的一部分
    private String desc;
    @Override
    public String toString() {
        return "Result{" +
                "title='" + title + '\'' +
                ", showUrl='" + showUrl + '\'' +
                ", clickUrl='" + clickUrl + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShowUrl() {
        return showUrl;
    }

    public void setShowUrl(String showUrl) {
        this.showUrl = showUrl;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public void setClickUrl(String clickUrl) {
        this.clickUrl = clickUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
