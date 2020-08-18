import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.util.List;

/**
 * @ClassName: TestAnsj
 * @Description: 分词
 * @Author: Ma Yuanyuan
 */
public class TestAnsj {
    public static void main(String[] args) {
        String str = "码云指数从5个角度对开源项目进行分析，以直观数值的形式来展示对一个开源项目各方位的指标度量， \n" +
                "可以为你在进行开源项目筛选时提供有价值的参考";
        //parse直接分完了，getTerms是得到分词结果
        List<Term> terms = ToAnalysis.parse(str).getTerms();
        for (Term term : terms) {
            System.out.println(term.getName());
        }
    }
}
