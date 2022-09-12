package utils;

import beans.Customer;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class JDBCUtils {

    public static Connection getConnection() throws Exception{
        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");

        Properties properties = new Properties();
        properties.load(stream);

        String user = properties.getProperty("mysql.user");
        String password = properties.getProperty("mysql.password");
        String url = properties.getProperty("mysql.url");
        String driverClass = properties.getProperty("mysql.driverClass");

        Class.forName(driverClass);

        return DriverManager.getConnection(url, user, password);
    }

    public static void closeResource(Connection connection, PreparedStatement preparedStatement) {
        try {
            if (connection != null) connection.close();
            if (preparedStatement != null) preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeResource(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (connection != null) connection.close();
            if (preparedStatement != null) preparedStatement.close();
            if (resultSet != null) resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //通用的增删改,查不行
    public static void update(String sql, Object ...args) { //"q".equals(temp) 可以避免空指针异常
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; ++i) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            /*
            preparedStatement.execute();
            这个方法如果执行的查询操作，有返回结果，则返回true
            增删改操作，则返回false
             */
            preparedStatement.executeUpdate();//返回影响的行数 int类型
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(connection, preparedStatement);
        }
    }

    //针对Customer表的通用查询操作
    public static Customer queryForCustomers(String sql, Object ...args) throws Exception{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();

            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; ++i) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = preparedStatement.getMetaData(); //获取结果集的元数据(修饰现有数据的数据)
            //获取结果集中的列数
            int columnCount = metaData.getColumnCount();
            if (resultSet.next()) {
                Customer customer = new Customer();
                //处理结果集一行数据中的每一个列
                for (int i = 0; i < columnCount; ++i) {
                    //获取列值
                    Object value = resultSet.getObject(i + 1);
                    //获取每个列的列名 1.getColumnName方法是获取列的列名 2.getColumnLabel()来获取列的别名
                    String name = metaData.getColumnLabel(i + 1);
                    //给customer对象指定的name属性，赋值为value，通过反射
                    Field field = Customer.class.getDeclaredField(name); //如果数据库字段名和类中属性不一致会出错
                    //可以在sql中使用别名(类的字段名)来避免错误
                    field.setAccessible(true); //这个属性有可能是私有的，设为true
                    field.set(customer, value);
                }
                return customer;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(connection, preparedStatement, resultSet);
        }
        return null;
    }

    //针对不同表的通用查询操作 获取一个对象
    public static <T> T getInstance(Class<T> clazz, String sql, Object ...args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();

            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; ++i) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = preparedStatement.getMetaData();
            int columnCount = metaData.getColumnCount();
            if (resultSet.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; ++i) {
                    //获取列值
                    Object value = resultSet.getObject(i + 1);
                    String name = metaData.getColumnLabel(i + 1);
                    Field field = clazz.getDeclaredField(name);
                    field.setAccessible(true);
                    field.set(t, value);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(connection, preparedStatement, resultSet);
        }
        return null;
    }

    //针对不同表的通用查询操作 获取多个对象
    public static <T> List<T> getForList(Class<T> clazz, String sql, Object ...args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();

            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; ++i) {
                preparedStatement.setObject(i + 1, args[i]);
            }

            ArrayList<T> list = new ArrayList<>();
            resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = preparedStatement.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; ++i) {
                    Object value = resultSet.getObject(i + 1);
                    String name = metaData.getColumnLabel(i + 1);
                    Field field = clazz.getDeclaredField(name);
                    field.setAccessible(true);
                    field.set(t, value);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(connection, preparedStatement, resultSet);
        }
        return null;
    }
}
