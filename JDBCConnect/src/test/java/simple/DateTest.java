package simple;

import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTest {

    @Test
    public void test01() {
        java.util.Date date = new java.util.Date();
        System.out.println(date);
        System.out.println(date.getTime());

        Date date1 = new Date(date.getTime());
        System.out.println(date1);
        Timestamp timestamp = new Timestamp(date1.getTime());
        System.out.println(timestamp);
    }

    @Test
    public void test02() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.s");
        LocalDate now = LocalDate.now();
    }
}
