import searcher.Result;
import searcher.Searcher;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName: TestSearcher
 * @Description: java类作用描述
 * @Author: Ma Yuanyuan
 */
public class TestSearcher {
    public static void main(String[] args) throws IOException {
        Searcher searcher = new Searcher();
        List<Result> results = searcher.search("arraylist");
        for(Result result :results){
            System.out.println(result);
            System.out.println("====================");
        }
    }
}
