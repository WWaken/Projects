package crawler;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * @ClassName: Crawler
 * @Description: 爬虫
 * @Author: Ma Yuanyuan
 */
public class Crawler {
    public static void main(String[] args) throws IOException {
        //1.创建一个OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.创建一个Request对象
        //实例化一个对象有很多方式，可以直接new，也可以使用某个静态的工厂方法来创建实例
        //Builder中提供的url方法可以设定当前请求的url
        Request request = new Request.Builder().url("http://wwww.baidu.com").build();
        //3.创建一个Call对象（这个对象负责进行一次网络访问操作）
        Call call = okHttpClient.newCall(request);
        //4.发送请求给服务器,获取到response对象
        Response response = call.execute();
        //5.判断response是否成功
        if(!response.isSuccessful()){
            System.out.println("请求失败！");
            return;
        }else{
            System.out.println(response.body().string());
        }
    }
}
