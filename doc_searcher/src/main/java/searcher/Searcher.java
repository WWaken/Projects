package searcher;

import common.DocInfo;
import index.Index;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @ClassName: Searcher
 * @Description: 核心搜索过程
 * @Author: Ma Yuanyuan
 */

class WeightComparator implements Comparator<Index.Weight>{
    @Override
    public int compare(Index.Weight o1, Index.Weight o2) {
        //如果o1 < o2,返回小于0的数字,升序
        //如果相等，返回0；
        //如果o1 > o2,返回大于0 的数字，降序
        return o2.weight - o1.weight;
    }
}
public class Searcher {
    private Index index = new Index();

    public Searcher() throws IOException {
        index.build("F:/raw_data.txt");
    }

    public List<Result> search(String query){
        //分词：针对查询词进行分词
        List<Term> terms = ToAnalysis.parse(query).getTerms();
        //触发：针对查询词的分词结果，去依次查找倒排索引，得到docId
        //这个结果中保存每个分词结果得到的倒排拉链的整体结果
        ArrayList<Index.Weight> allTokenResult = new ArrayList<>();
        for(Term term : terms){
            //word是全小写，索引中的内容也是小写
            String word = term.getName();
            //得到倒排拉链
            List<Index.Weight> invertedList = index.getInverted(word);
            if(invertedList == null){
                //说明用户输入的词，在所有文档中都不存在
                continue;
            }
            allTokenResult.addAll(invertedList);
        }
        //排序：按照权重进行降序排序
        allTokenResult.sort(new WeightComparator());
        //包装结果：根据刚才查找到的docId在正排中查找DocInfo，包装成Result对象
        ArrayList<Result> results = new ArrayList<>();
        for(Index.Weight weight: allTokenResult){
            //根据weight中包含的docId找到对应的DocInfo对象
            DocInfo docInfo = index.getDocInfo(weight.docId);
            Result result = new Result();
            result.setTitle(docInfo.getTitle());
            result.setShowUrl(docInfo.getUrl());
            result.setClickUrl(docInfo.getUrl());
            //从正文中摘取一段摘要信息，根据当前的词找到词在正文中的位置
            //在把这个词周围的文本都获取到，得到一个片段
            result.setDesc(GenDesc(docInfo.getContent(),weight.word));
            results.add(result);
        }
        return  results;
    }
    //根据当前的词，提取正文中的一部分内容作为描述
    private String GenDesc(String content, String word) {
        //查找word在content中出现的位置
        //word中的内容是全小写了，content中大小写都有
        int firstPos = content.toLowerCase().indexOf(word);
        if(firstPos == -1){
            //某个词只在标题中出现，正文中没有出现,或者根本就没有，暂时不考虑
            return "";
        }
        //从 firstPos向前找60个字符，作为描述开始，如果不足60，从正文头开始
        //从firstPos向后找100个字符，作为描述结束，如果不足160，就到结尾
        int descBeg = firstPos < 60 ? 0 : firstPos - 60;
        if(descBeg + 160 > content.length()){
            return content.substring(descBeg);
        }
        return content.substring(descBeg,descBeg + 160)+ "...";
    }
}
