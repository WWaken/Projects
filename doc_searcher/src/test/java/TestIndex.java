import common.DocInfo;
import index.Index;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName: TestIndex
 * @Description: java类作用描述
 * @Author: Ma Yuanyuan
 */
public class TestIndex {
    public static void main(String[] args) throws IOException {
        Index index = new Index();
        index.build("F:/raw_data.txt");//全小写
        List<Index.Weight> invertedList = index.getInverted("arraylist");
        for(Index.Weight weight : invertedList){
            System.out.println(weight.docId);
            System.out.println(weight.word);
            System.out.println(weight.weight);

            DocInfo docInfo = index.getDocInfo(weight.docId);
            System.out.println(docInfo.getTitle());
            System.out.println("===============");
        }
    }
}
