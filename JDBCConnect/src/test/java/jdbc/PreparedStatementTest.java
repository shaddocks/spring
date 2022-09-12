package jdbc;

import beans.Customer;
import org.junit.Before;
import org.junit.Test;
import utils.JDBCUtils;

import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

//使用PreparedStatement代替Statement实现数据库的增删改查操作
public class PreparedStatementTest {

    Connection connection;

    @Before
    public void test() throws Exception{
        //1.读取配置文件的基本信息
        InputStream stream = JDBCTest.class.getClassLoader().getResourceAsStream("jdbc.properties");

        Properties properties = new Properties();
        properties.load(stream);

        String user = properties.getProperty("mysql.user");
        String password = properties.getProperty("mysql.password");
        String url = properties.getProperty("mysql.url");
        String driverClass = properties.getProperty("mysql.driverClass");

        //2.加载驱动
        Class.forName(driverClass);

        //3.获取连接
        connection = DriverManager.getConnection(url, user, password);
    }

    //添加记录
    @Test
    public void test01(){
        String sql = "insert into customers(name, email, birth) values (?, ?, ?)"; //占位符

        try (//1.预编译sql语句，返回实例
             PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            //2.填充占位符 数据库中是从1开始的
            preparedStatement.setString(1, "哪吒");
            preparedStatement.setString(2, "nezha@gmail.com");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            preparedStatement.setDate(3, java.sql.Date.valueOf(dateFormat.format(new Date())));

            //3.执行操作
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //7.资源的关闭
            //preparedStatement.close();
        }
    }

    @Test
    public void test02() throws Exception{ //加上try...catch语句
        //1.获取数据库连接
        connection = JDBCUtils.getConnection();
        //2.预编译sql语句，返回PreparedStatement的实例
        String sql = "update customers set name = ? where id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        //3.填充占位符
        preparedStatement.setString(1, "贝多芬2");
        preparedStatement.setInt(2, 18);
        //4.执行
        preparedStatement.execute();
        //5.资源关闭
        JDBCUtils.closeResource(connection, preparedStatement);
    }

    //通用的增删改
    @Test
    public void test03() {
        String sql = "delete from customers where id = ?";
        JDBCUtils.update(sql, 20);
    }

    @Test
    public void test04() {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            String sql = "select id, name, email, birth from customers where id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 1);

            //返回结果集
            resultSet = preparedStatement.executeQuery();
            //处理结果集
            while (resultSet.next()) {//判断结果集的下一条是否有数据，有则返回true，并指针下移，如果返回false，指针不会下移
                //获取当前数据的各个字段值
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                java.sql.Date birth = resultSet.getDate(4);

                //将数据封装为一个对象
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtils.closeResource(connection, preparedStatement, resultSet);
        }
    }

    //查询测试
    @Test
    public void test05() throws Exception {
        String sql = "select id, name, birth, email from customers where id = ?";
        Customer customer = JDBCUtils.queryForCustomers(sql, 13);
        System.out.println(customer);
    }

    //通用查询测试 单个
    @Test
    public void test06() {
        String sql = "select id, name, birth, email from customers where id = ?";
        Customer instance = JDBCUtils.getInstance(Customer.class, sql, 13);
        System.out.println(instance);
    }

    //通用查询测试 多个
    @Test
    public void test07() {
        String sql = "select id, name, birth, email from customers where id < ?";
        List<Customer> list = JDBCUtils.getForList(Customer.class, sql, 12);
        assert list != null;
        list.forEach(System.out::println);
    }
}
