package crawler;

import dao.Project;
import dao.ProjectDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @ClassName: ThreadCrawler
 * @Description: 多线程提高效率
 * @Author: Ma Yuanyuan
 */
public class ThreadCrawler extends Crawler {
    public static void main(String[] args) throws IOException {
        //使用多线程的方式，是访问Github API 编程并行访问
        ThreadCrawler Crawler = new ThreadCrawler();
        //获取首页内容
        String html = Crawler.getPage("https://github.com/akullpp/awesome-java/blob/master/README.md");
        //分析项目列表
        List<Project> projects = Crawler.parseProjectList(html);
        long startTime = System.currentTimeMillis();
        //遍历项目列表，使用多线程的方式，线程池
        List<Future<?>> taskResults = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        for (Project project : projects) {
            Future<?> taskResult = executorService.submit(new CrawlerTask(project,Crawler));
            taskResults.add(taskResult);
        }
        //等待所有线程池中的任务执行结束，在进行下一步操作
        for (Future<?> taskResult:taskResults){
            //调用get方法就会阻塞，阻塞到任务执行完毕，get才会返回
            try {
                taskResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //代码执行到这里，说明所有任务都执行完毕,结束线程池
        //及时“裁员”
        executorService.shutdown();
        long finishTime = System.currentTimeMillis();
        System.out.println("调用API的时间" + (finishTime - startTime)+ "ms");
        //保存到数据库
        ProjectDao projectDao = new ProjectDao();
        for(Project project: projects){
            projectDao.save(project);
        }

    }
    static class CrawlerTask implements Runnable{
        private Project project;
        private ThreadCrawler threadCrawler;

        public CrawlerTask(Project project, ThreadCrawler threadCrawler) {
            this.project = project;
            this.threadCrawler = threadCrawler;
        }

        @Override
        public void run() {
            //1.调用API获取项目数据
            try {
                System.out.println("crawing " + project.getName() + "...");
                String repoName = threadCrawler.getRepoName(project.getUrl());
                String jsonString = threadCrawler.getRepoInfo(repoName);
                //2.解析项目数据
                threadCrawler.parseRepoInfo(jsonString,project);
                System.out.println("crawing " + project.getName() + " done!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
