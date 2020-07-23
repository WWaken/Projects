package crawler;

import dao.Project;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @ClassName: Crawler
 * @Description: 爬虫
 * @Author: Ma Yuanyuan
 */
public class Crawler {
    private HashSet<String> urlBlackList = new HashSet<>();

    {
        urlBlackList.add("https://github.com/events");
        urlBlackList.add("https://github.community");
        urlBlackList.add("https://github.com/about");
        urlBlackList.add("https://github.com/pricing");
        urlBlackList.add("https://github.com/contact");
    }

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler();
        String html = crawler.getPage("https://github.com/akullpp/awesome-java/blob/master/README.md");
        //System.out.println(respBody);
        List<Project> projects = crawler.parseProjectList(html);
        System.out.println(projects);
    }
    public String getPage(String url) throws IOException {
        //1.创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.创建一个Request对象
        //实例化一个对象有很多方式，可以直接new，也可以使用某个静态的工厂方法来创建实例
        //Builder中提供的url方法可以设定当前请求的url
        Request request = new Request.Builder().url(url).build();
        //3.创建一个Call对象（这个对象负责进行一次网络访问操作）
        Call call = okHttpClient.newCall(request);
        //4.发送请求给服务器,获取到response对象
        Response response = call.execute();
        //5.判断response是否成功
        if (!response.isSuccessful()) {
            System.out.println("请求失败！");
            return null;
        } else {
            return response.body().string();
        }
    }
    public List<Project> parseProjectList(String html){
        ArrayList<Project> result = new ArrayList<>();
        //1.创建Document对象
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("li");
        for(Element li:elements){
            //获取a标签
            Elements alllink = li.getElementsByTag("a");
            if(alllink.size() == 0){
                //当前li标签没有包含a标签，忽略
                continue;
            }
            //一个项目中只有一个a标签
            Element link = alllink.get(0);
            //输出a标签中的内容
//            System.out.println(link.text());
//            System.out.println(link.attr("href"));
//            System.out.println(li.text());
//            System.out.println("=======");
            //如果当前这个项目的url不是以https://github.com开头的，就直接丢弃掉
            String url = link.attr("href");
            if(!url.startsWith("https://github.com")){
                continue;
            }
            if(urlBlackList.contains(url)){
                continue;
            }
            Project project = new Project();
            project.setName(link.text());
            project.setUrl(link.attr("href"));
            project.setDecription(li.text());
            result.add(project);
        }
        return result;
    }
}
