package dao;

import utils.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

//封装来针对数据表的通用操作
public abstract class BaseDao {
    //更新
    public int update(Connection connection, String sql, Object ...args) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; ++i) {
                preparedStatement.setObject(i + 1, args[i]);
            }
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, preparedStatement);
        }
        return 0;
    }
    //查询
    public <T> List<T> getForList(Connection connection, Class<T> clazz, String sql, Object ...args) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
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
            JDBCUtils.closeResource(null, preparedStatement, resultSet);
        }
        return null;
    }
    //查询如 count(*)
    public Object getValue(Connection connection, String sql, Object ...args) throws Exception{
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; ++i) {
            preparedStatement.setObject(i + 1, args[i]);
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getObject(1);
        }
        JDBCUtils.closeResource(null, preparedStatement, resultSet);
        return null;
    }
}
