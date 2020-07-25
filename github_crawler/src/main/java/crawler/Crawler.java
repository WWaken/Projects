package crawler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dao.Project;
import dao.ProjectDao;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @ClassName: Crawler
 * @Description: 爬虫
 * @Author: Ma Yuanyuan
 */
public class Crawler {
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Gson gson = new GsonBuilder().create();
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
        long startTime = System.currentTimeMillis();
        //获取入口页面
        String html = crawler.getPage("https://github.com/akullpp/awesome-java/blob/master/README.md");
        //System.out.println(respBody);
        long finishTime = System.currentTimeMillis();
        System.out.println("获取入口页面时间为："+ (finishTime-startTime)+"ms");
        //解析入口页面，获取项目列表
        List<Project> projects = crawler.parseProjectList(html);
        //System.out.println(projects);
        System.out.println("解析项目列表时间：" +(System.currentTimeMillis() - finishTime)+"ms");
        finishTime = System.currentTimeMillis();

        //遍历项目列表，调用github API获取项目信息
        for(int i = 0; i < projects.size();i++){
            try {
                Project project = projects.get(i);
                String repoName = crawler.getRepoName(project.getUrl());
                String jsonString = crawler.getRepoInfo(repoName);
                System.out.println(jsonString);
                //解析每个仓库获取到JSON数据，得到需要的信息
                crawler.parseRepoInfo(jsonString,project);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("解析所有项目的时间：" + (System.currentTimeMillis() -finishTime)+"ms");
        finishTime = System.currentTimeMillis();

       // 把project保存到数据库中
        ProjectDao projectDao = new ProjectDao();
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            projectDao.save(project);
        }
        System.out.println("存储数据库的时间：" + (System.currentTimeMillis() -finishTime)+"ms");
        finishTime = System.currentTimeMillis();
        System.out.println("整个项目总时间为" + (finishTime - startTime) + "ms");
    }

    public String getPage(String url) throws IOException {
        //1.创建一个OkHttpClient对象
        okHttpClient = new OkHttpClient();
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
        //把字符串形式的html转换成一个树形结构document
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

    //调用Github API获取指定仓库的信息
    //repoName形如 用户名/具体仓库名
    public String getRepoInfo(String repoName) throws IOException {
        String userName = "WWaken";
        String password = "m1549200803";
        // 进行身份认证, 把用户名密码加密之后, 得到一个字符串, 把这个字符串放到 HTTP header 中.
        // 此处只是针对用户名密码进行了 base64 加密. 严格意义上说, 没啥卵用, 可以很容易解密.
        // 总是好过直接传输明文
        // credential 形如: Basic SEd0ejIyMjI6dHoyMjIyMjIyMjI=
        String credential = Credentials.basic(userName, password);

        String url = "https://api.github.com/repos/" + repoName;
        // OkHttpClient 对象前面已经创建过了, 不需要重复创建.
        // 请求对象, Call 对象, 响应对象, 还是需要重新创建的
        Request request = new Request.Builder().url(url).header("Authorization", credential).build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        if (!response.isSuccessful()) {
            System.out.println("访问 Github API 失败! url = " + url);
            return null;
        }
        return response.body().string();
    }

    // 这个方法的功能, 就是把项目的 url 提取出其中的仓库名字和作者名字
    // https://github.com/doov-io/doov => doov-io/doov
    public String getRepoName(String url) {
        int lastOne = url.lastIndexOf("/");
        int lastTwo = url.lastIndexOf("/", lastOne - 1);
        if (lastOne == -1 || lastTwo == -1) {
            System.out.println("当前 URL 不是一个标准的项目 url! url:" + url);
            return null;
        }
        return url.substring(lastTwo + 1);
    }

    //通过这个方法来获取到仓库的相关信息
    //第一个参数表示Github API获取到的结果
    //第二个参数表示解析出来的相关信息的数，保存到project对象中
    //使用Gson这个库来进行解析
    public void parseRepoInfo(String jsonString, Project project) {
        Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
        HashMap<String, Object> hashMap = gson.fromJson(jsonString, type);
        // hashMap 中的 key 的名字都是源于 Github API 的返回值.
        Double starCount = (Double)hashMap.get("stargazers_count");
        project.setStarCount(starCount.intValue());
        Double forkCount = (Double)hashMap.get("forks_count");
        project.setForkCount(forkCount.intValue());
        Double openedIssueCount = (Double)hashMap.get("open_issues_count");
        project.setOpenedIssueCount(openedIssueCount.intValue());
    }
}
