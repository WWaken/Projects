package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @ClassName: Testgson
 * @Description: Gson测试
 * @Author: Ma Yuanyuan
 */
public class Testgson {
    public static void main(String[] args) {

        //创建Gson对象
        Gson gson = new GsonBuilder().create();

//        //2.把键值对数据转换成JSON格式的字符串
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("name", "lilei");
//        hashMap.put("ll","jjj");
//        String str = gson.toJson(hashMap);
//        System.out.println(str);


    }
}
