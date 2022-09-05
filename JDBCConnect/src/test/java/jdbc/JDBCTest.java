package jdbc;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCTest {

    @Before
    public void test() {
    }

    //数据库连接方式一
    @Test
    public void test01() throws SQLException {
        Driver driver = new com.mysql.cj.jdbc.Driver(); //mysql实现的驱动类

        String url = "jdbc:mysql://localhost:3306/jdbc_learn";
        Properties info = new Properties(); //将用户名和密码封装到这个里面
        info.setProperty("user", "root");
        info.setProperty("password", "12345678");

        Connection connection = driver.connect(url, info);

        System.out.println(connection);
    }

    //方式二
    @Test
    public void test02() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, SQLException {
        //1.获取Driver实现类对象，使用反射
        Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();

        //2.提供要连的数据库
        String url = "jdbc:mysql://localhost:3306/jdbc_learn";
        //3.提供连接需要的用户名和密码
        Properties info = new Properties(); //将用户名和密码封装到这个里面
        info.setProperty("user", "root");
        info.setProperty("password", "12345678");

        //4.获得连接
        Connection connect = driver.connect(url, info);
        System.out.println(connect);
    }

    //方式三 使用DriverManager替换Driver
    @Test
    public void test03() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, SQLException {
        //1.获得Driver实现类对象
        Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();
        //2.提供连接的基本信息
        String url = "jdbc:mysql://localhost:3306/jdbc_learn";
        String user = "root";
        String password = "12345678";
        //3.注册驱动
        DriverManager.registerDriver(driver);
        //4.获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }

    //方式四
    @Test
    public void test04() throws Exception{
        //1.提供连接的基本信息
        String url = "jdbc:mysql://localhost:3306/jdbc_learn";
        String user = "root";
        String password = "12345678";

        //jar包下有其驱动的路径，导进来的时候就帮我们读这个地址了，所以下面可以省略，但是不要省，因为其他数据库可能没。

        //2.加载Driver类，里面静态代码块帮我们做了注册到驱动管理类中
        //在mysql的Driver实现类中，声明了静态代码块中操作
        Class.forName("com.mysql.cj.jdbc.Driver");
//        Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();
//        //3.注册驱动
//        DriverManager.registerDriver(driver);
        //4.获取连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }

    //方式五 将数据库连接的信息声明在配置文件中
    @Test
    public void test05() throws Exception {
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
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }
}
