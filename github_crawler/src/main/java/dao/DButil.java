package dao;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @ClassName: DButil
 * @Description: 管理数据库的连接
 * @Author: Ma Yuanyuan
 */
public class DButil {
    private static String URL = "jdbc:mysql://127.0.0.1:3306/github_crawler?characterEncoding=utf8&useSSL=true";
    private static String USERNAME = "root";
    private static String PASSWORD = "";

    //获取数据库的实例
    private static volatile DataSource dataSource = null;

    private static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DButil.class) {
                if (dataSource == null) {
                    dataSource = new MysqlDataSource();
                    MysqlDataSource mysqlDataSource = (MysqlDataSource) dataSource;
                    mysqlDataSource.setURL(URL);
                    mysqlDataSource.setUser(USERNAME);
                    mysqlDataSource.setPassword(PASSWORD);

                }
            }
        }
        return dataSource;
    }
    public static Connection getConnection(){
        try{
            return (Connection) getDataSource().getConnection();
        }catch(SQLException e){
            e.printStackTrace();

        }
        return null;
    }
}
