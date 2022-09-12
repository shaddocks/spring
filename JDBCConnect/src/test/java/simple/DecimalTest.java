package simple;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DecimalTest {

    @Test
    public void test01() {
        BigDecimal decimal1 = new BigDecimal("0.5");
        BigDecimal decimal2 = new BigDecimal("9.5");
        System.out.println(decimal1.add(decimal2));
        System.out.println(decimal2.subtract(decimal1));
        System.out.println(decimal1.multiply(decimal2));
        System.out.println(decimal2.divide(decimal1, RoundingMode.DOWN));
        BigDecimal decimal = decimal1.setScale(5, RoundingMode.DOWN);
        System.out.println(decimal);
        System.out.println(decimal.doubleValue()); //decimal.intValue()
    }

    @Test
    public void test02() {
        double num = 12.3456;
        BigDecimal decimal = new BigDecimal("12.0905");
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        String string = decimalFormat.format(decimal);
        System.out.println(string);
    }
}
