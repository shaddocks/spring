package dao.impl;

import dao.StudentDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class StudentDaoImpl implements StudentDao {

    @PostConstruct
    private void destroy() {
        System.out.println("destroy");
    }
}
