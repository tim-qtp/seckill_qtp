package com.seckill.monitor.demo;
import org.apache.calcite.avatica.AvaticaConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DruidSQlTest {

    public static void main(String[] args) throws Exception {
        //数据库连接地址
        String url = "jdbc:avatica:remote:url=http://8.141.90.31:8888/druid/v2/sql/avatica/";

        //获取连接对象

        Connection connection = (AvaticaConnection)DriverManager.getConnection(url);
        //Statement
        Statement statement = connection.createStatement();

        //SQL语句
        String sql = "SELECT * FROM logsitems";

        //执行查询
        ResultSet resultSet = statement.executeQuery(sql);

        //解析结果集
        while (resultSet.next()) {
            System.out.println("查询到的结果是：" + resultSet.getString("uri"));
        }

        //关闭资源
        resultSet.close();
        statement.close();
        connection.close();
    }
}