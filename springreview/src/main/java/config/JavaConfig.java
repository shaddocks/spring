package config;

import beans.Student;
import beans.Teacher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(value = {"controller", "service", "dao"})
@PropertySource("classpath:db.properties")
public class JavaConfig {

    @Bean
    public Student student() {
        return new Student("12", "12", 1, "12");
    }

    @Bean
    public Teacher teacher(Student student) {
        return new Teacher(student);
    }
}
