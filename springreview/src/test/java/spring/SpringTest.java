package spring;

import beans.Student;
import beans.Teacher;
import config.JavaConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.StudentService;

public class SpringTest {

    ApplicationContext ioc;

    @Before
    public void test() {
        //ioc = new ClassPathXmlApplicationContext("spring02.xml");
        ioc = new AnnotationConfigApplicationContext(JavaConfig.class);
    }

    @Test
    public void test01() {
        Student student = ioc.getBean(Student.class);
        System.out.println(student);
    }

    @Test
    public void test02() {
        Teacher teacher = ioc.getBean("teacher", Teacher.class);
        System.out.println(teacher);
    }

    @Test
    public void test03() {
        StudentService service = ioc.getBean(StudentService.class);
        System.out.println(service);
    }

    @Test
    public void test04() {
        Teacher teacher = ioc.getBean(Teacher.class);
        System.out.println(teacher);
    }
}
