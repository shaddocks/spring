package jdbc;

import org.junit.Test;
import utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;

/*
使用PreparedStatement实现批量插入
update和delete本身具有批量操作的效果
此时的批量插入指的是insert
*/
public class InsertTest {

    /*
    create table goods(
    id INT PRIMARY KEY AUTO_INCREMENT
    NAME VARCHAR(25)
    )
    1.statement for循环插入
     */

    //2.PreparedStatement
    @Test
    public void test01() throws Exception{
        Connection connection = JDBCUtils.getConnection();
        String sql = "insert into goods(name) values(?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < 2000; ++i) {
            preparedStatement.setObject(1, "name_" + i);
            preparedStatement.execute();
        }
        JDBCUtils.closeResource(connection, preparedStatement);
    }

    //3.addBatch() executeBatch() clearBatch()
    //mysql默认关闭批处理的，需要一个参数让mysql开启 ?rewriteBatchedStatements=true 写在配置文件的URL后面 8.0是?useSSL=true&...
    @Test
    public void test02() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        //设置不允许自动提交数据
        connection.setAutoCommit(false);
        String sql = "insert into goods(name) values(?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < 20000; ++i) {
            preparedStatement.setObject(1, "name_" + i);
            //1.攒sql
            preparedStatement.addBatch();

            if (i % 500 == 0) {
                //2.执行
                preparedStatement.executeBatch();
                //3.清空batch
                preparedStatement.clearBatch();
            }
            if (i == 19999) {
                preparedStatement.executeBatch();
            }
        }
        //提交数据
        connection.commit();
        JDBCUtils.closeResource(connection, preparedStatement);
    }
    //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
}
