package index;

import common.DocInfo;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private void buildInverted(DocInfo docInfo) {
        class WordCnt{
            public int titleCount;
            public int contentCount;

            public WordCnt(int titleCount, int contentCount) {
                this.titleCount = titleCount;
                this.contentCount = contentCount;
            }
        }
        HashMap<String,WordCnt> wordCntHashMap = new HashMap<>();

        //针对DocInfo中的title和content进行分词，再根据分词结果构造出weight对象，更新到 倒排索引
        //1、针对标题分词
        List<Term> titleTerms = ToAnalysis.parse(docInfo.getTitle()).getTerms();
        //2.遍历分词结果，统计标题中每个词出现的次数
        for(Term term : titleTerms){
            //word已被转为小写
            String word = term.getName();
            WordCnt wordCnt = wordCntHashMap.get(word);
            if(wordCnt == null){
                wordCntHashMap.put(word,new WordCnt(1,0));
            }else{
                wordCnt.titleCount++;
            }
        }
        //3.针对正文分词
        List<Term> contentTerms = ToAnalysis.parse(docInfo.getContent()).getTerms();
        //4.遍历分词结果，统计正文中每个词出现的次数
        for(Term term : contentTerms) {
            String word = term.getName();
            WordCnt wordCnt = wordCntHashMap.get(word);
            if (wordCnt == null) {
                wordCntHashMap.put(word, new WordCnt(0, 1));
            } else {
                wordCnt.contentCount++;
            }
        }
        //5.遍历hashmap，依次构建weight对象并更新倒排索引的映射关系
        for(HashMap.Entry<String, WordCnt> entry : wordCntHashMap.entrySet()){
            Weight weight = new Weight();
            weight.word = entry.getKey();
            weight.docId = docInfo.getDocId();
            //公式
            weight.weight = entry.getValue().titleCount * 10 + entry.getValue().contentCount;
            //将weight加入到倒排索引中，倒排索引是一个hashmap，value就是weight构成的ArrayList
            //西安根据这个词，找到hashmap中对应的ArrayList
            ArrayList<Weight> invertedList = invertedIndex.get(entry.getKey());
            if(invertedList == null){
                //当前键值对不存在，要新加入一个键值对
                invertedList = new ArrayList<>();
                invertedIndex.put(entry.getKey(),invertedList);
            }
            //invertedIndex已经是一个合法的ArrayList了，可以直接把weight直接加入即可
            invertedList.add(weight);
        }
    }

    private DocInfo buildForward(String line) {
        //把这一行按照 \3 切分，分出来的三个部分就是文档标题 URL 正文
        String[] tokens = line.split("\3");
        if (tokens.length != 3) {
            //文件格式有问题，处理方式为打印日志
            System.out.println("文件格式出现问题" + line);
            return null;
        }
        //把新的docInfo插入到数组末尾，假设数组有0个元素，新元素的下标就是0
        DocInfo docInfo = new DocInfo();
        //id是正排索引下标
        docInfo.setDocId(forwardIndex.size());
        docInfo.setTitle(tokens[0]);
        docInfo.setUrl(tokens[1]);
        docInfo.setContent(tokens[2]);
        forwardIndex.add(docInfo);
        return docInfo;
    }

}
