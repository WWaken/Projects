package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ProjectDao
 * @Description: 负责针对project进行数据库操作
 * @Author: Ma Yuanyuan
 */
public class ProjectDao {
    public void save(Project project){
        //通过sava方法吧一个project对象保存到数据库zhong
        //1.获取到数据库连接
        Connection connection = DButil.getConnection();
        //2.构造PrepareStatement对象拼接SQL语句
        PreparedStatement statement = null;
        String sql = "insert into project_table values(?,?,?,?,?,?,?)";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,project.getName());
            statement.setString(2,project.getUrl());
            statement.setString(3,project.getDecription());
            statement.setInt(4,project.getStarCount());
            statement.setInt(5,project.getForkCount());
            statement.setInt(6,project.getOpenedIssueCount());
            //预期插入的日期形式为20200725，可以根据当前系统时间，可以搭配SimpleDateFormat
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            statement.setString(7,simpleDateFormat.format(System.currentTimeMillis()));
        //3.执行SQL语句，完成数据库插入操作
            int ret = statement.executeUpdate();
            if(ret != 1){
                System.out.println("数据执行插入操作失败");
                return;
            }
            System.out.println("插入成功");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            DButil.close(connection,statement, null);
        }
    }
    public List<Project> selectProByDate(String date){
        List<Project> projects = new ArrayList<>();
        //获取到数据库连接
        Connection connection = DButil.getConnection();
        //拼装sql语句
        String sql = "select name,url,starCount,forkCount,openedIssueCount " +
                "from project_table where date = ? order by starCount desc";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,date);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Project project = new Project();
                project.setName(resultSet.getString("name"));
                project.setUrl(resultSet.getString("url"));
                project.setStarCount(resultSet.getInt("starCount"));
                project.setForkCount(resultSet.getInt("forkCount"));
                project.setOpenedIssueCount(resultSet.getInt("openedIssueCount"));
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DButil.close(connection,preparedStatement,resultSet);
        }
        return projects;
    }
}
