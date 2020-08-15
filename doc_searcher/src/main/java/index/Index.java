package index;

import common.DocInfo;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @ClassName: Index
 * @Description: 索引
 * @Author: Ma Yuanyuan
 */
public class Index {
    //权重
    static public class Weight{
        //10*标题中出现的次数+正文中出现的次数
        public String word;
        public int docId;
        public int weight;
    }
    //直接把docId 作为数组下标，正排索引
    private ArrayList<DocInfo> forwardIndex = new ArrayList<>();
    //倒排索引，根据词来找到一组docId
    private HashMap<String, ArrayList<Weight>> invertedIndex = new HashMap<>();

    public DocInfo getDocInfo(int docId){
        return forwardIndex.get(docId);
    }

    public ArrayList<Weight> getInverted(String term){
        return invertedIndex.get(term);
    }
    //构建索引，把raw_data.txt文件内容读取出来，加载到内存上的数据结构中
    public void build(String inputPath) throws IOException {
        long start = System.currentTimeMillis();
        System.out.println("build start");
        //1.打开文件，按行读取文件内容
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(inputPath)));
        //2.读取到每一行
        String line = "";
        while((line = bufferedReader.readLine()) != null){
            //3.按照'\3'来切分，结果可以用来构成一个DocInfo对象，并加入正派索引
            DocInfo docInfo = buildForward(line);
            //4.构造倒排，把DocInfo对象中的内容，进一步处理，构造出倒排索引
            buildInverted(docInfo);
            System.out.println("Build" + docInfo.getTitle() + "done!");
        }
        bufferedReader.close();

        long finish = System.currentTimeMillis();
        System.out.println("build finish");
        System.out.println("花了" + (finish - start) + "ms");
    }

    private DocInfo buildForward(String line) {
        //把这一行按照\3切分，分出来的三个部分就是文档标题 URL 正文
        String[] tokens = line.split("\3");
        if (tokens.length != 3) {
            //文件格式有问题，处理方式为打印日志
            System.out.println("文件格式出现问题" + line);
            return null;
        }

        
        DocInfo docInfo = new DocInfo();
        //id是正排索引下标
        docInfo.setDocId(forwardIndex.size());
    }
}
