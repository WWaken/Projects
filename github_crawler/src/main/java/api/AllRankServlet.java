package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Project;
import dao.ProjectDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName: AllRankServlet
 * @Description: servlet
 * @Author: Ma Yuanyuan
 */

//基于多态的语法实现
public class AllRankServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        //解析请求获取其中的日期参数
        String date = req.getParameter("date");
        if(date == null ||date.equals("")){
            resp.setStatus(404);
            resp.getWriter().write("date参数错误");
            return;
        }
        //从数据库中查找指定日期的数据
        ProjectDao projectDao = new ProjectDao();
        List<Project> projects = projectDao.selectProByDate(date);
        //把数据整理成json格式并返回给客户端（Gson）
        String respString = gson.toJson(projects);
        resp.getWriter().write(respString);
    }
}
