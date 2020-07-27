package dao;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @ClassName: DButil
 * @Description: 管理数据库的连接
 * @Author: Ma Yuanyuan
 */


// 这是一个单例类, 帮我们管理数据库的连接
public class DButil {

    private static String URL = "jdbc:mysql://127.0.0.1:3306/github_crawler?characterEncoding=utf8&useSSL=true" ;
    private static String USERNAME = "root";
    private static String PASSWORD = "";

    private static volatile DataSource dataSource = null;

    //线程安全
    private static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DButil.class) {
                if (dataSource == null) {
                    dataSource = new MysqlDataSource();
                    // 向下转型
                    MysqlDataSource mysqlDataSource = (MysqlDataSource)dataSource;
                    mysqlDataSource.setURL(URL);
                    mysqlDataSource.setUser(USERNAME);
                    mysqlDataSource.setPassword(PASSWORD);
                }
            }
        }
        return dataSource;
    }

    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Connection connection, PreparedStatement statement,
                             ResultSet resultSet) {

        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (statement != null) {
                    statement.close();
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                    connection.close();
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
