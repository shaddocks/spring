package jdbc;

import beans.Customer;
import org.junit.Test;
import utils.JDBCUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BlobTest {

    //向数据库表中customers中插入BLOB类型的字段
    @Test
    public void test01() throws Exception{
        Connection connection = JDBCUtils.getConnection();
        String sql = "insert into customers(name, email, birth, photo) values (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, "章鱼");
        preparedStatement.setObject(2, "zhangyu@163.com");
        preparedStatement.setObject(3, new java.sql.Date(new java.util.Date().getTime()));

        FileInputStream inputStream = new FileInputStream("/Users/xu/Pictures/壁纸/wallhaven-8ozj3y.jpeg");
        preparedStatement.setBlob(4, inputStream); //文件的话，以流的方式存入

        preparedStatement.execute();

        JDBCUtils.closeResource(connection, preparedStatement);
    }

    //查询Blob类型的字段
    @Test
    public void test02() throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            connection = JDBCUtils.getConnection();
            String sql = "select id, name, email, birth, photo from customers where id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 22);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt(1); //除了索引，还可以使用列的别名
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                java.sql.Date birth = resultSet.getDate(4);

                Customer customer = new Customer(id, name, email, birth);

                //将Blob类型的字段下载下来，以文件的方式保存到本地
                Blob blob = resultSet.getBlob(5);
                inputStream = blob.getBinaryStream();//实际上是二进制流的方式
                outputStream = new FileOutputStream("copy.jpeg");
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            JDBCUtils.closeResource(connection, preparedStatement, resultSet);
        }
    }

}
