package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import searcher.Result;
import searcher.Searcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName: DocSearcherServlet
 * @Description: java类作用描述
 * @Author: Ma Yuanyuan
 */
public class DocSearcherServlet extends HttpServlet {
    private Searcher searcher = new Searcher();
    private Gson gson = new GsonBuilder().create();

    public DocSearcherServlet() throws IOException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        //从请求中获取查询词内容
        String query = req.getParameter("query");
        if(query == null || query.equals("")){
            resp.setStatus(404);
            resp.getWriter().write("query参数非法");
            return;
        }
        //根据query获取到结果
        List<Result> results = searcher.search(query);
        String respString = gson.toJson(results);
        resp.getWriter().write(respString);
    }
}
