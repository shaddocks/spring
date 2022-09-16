package transaction;

import beans.User;
import org.junit.Test;
import utils.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class TransactionTest {

    //例子：设置非自动提交，数据库连接要一样，几个操作在一起，然后提交，如果出错，则在catch中回滚，最后设置为自动提交

    //以下代码演示隔离级别: 注意可以提前设置事务的隔离级别为1，并非4，来查看读未提交
    @Test
    public void testTransactionSelect() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        //取消自动提交数据，保证在同一事务中
        connection.setAutoCommit(false);

        String sql = "select user, password, balance from user_table where user = ?";
        User user = getInstance(connection, User.class, sql, "CC");
        System.out.println(user);
    }

    @Test
    public void testTransactionUpdate() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        System.out.println(connection.getTransactionIsolation());

        //取消自动提交数据，保证在同一事务中
        connection.setAutoCommit(false);
        //设置事务的隔离级别
        //connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        String sql = "update user_table set balance = ? where user = ?";
        update(connection, sql, 5000, "CC");

        Thread.sleep(15000);
        System.out.println("修改结束");
        connection.close();
    }

    public static void update(Connection connection, String sql, Object ...args) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; ++i) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, preparedStatement);
        }
    }

    public static <T> T getInstance(Connection connection, Class<T> clazz, String sql, Object ...args) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
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
            JDBCUtils.closeResource(null, preparedStatement, resultSet);
        }
        return null;
    }
}
