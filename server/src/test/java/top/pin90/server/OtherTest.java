package top.pin90.server;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtherTest {
    @Test
    public void testPhoneRex() {
        String rex;
        rex = "^(?:\\+?86)?1(?:3\\d{3}|5[^4\\D]\\d{2}|8\\d{3}|7(?:[01356789]\\d{2}|4(?:0\\d|1[0-2]|9\\d))|9[189]\\d{2}|6[567]\\d{2}|4[579]\\d{2})\\d{6}$";
        final Pattern compile = Pattern.compile(rex);
        final Matcher matcher = compile.matcher("17854560896");
        final boolean matches = matcher.matches();
        assert matches;
    }

    @Test
    public void testCalendar() {
        // 使用默认时区和语言环境获得一个日历
        Calendar cal = Calendar.getInstance();
        // 赋值时年月日时分秒常用的6个值，注意月份下标从0开始，所以取月份要+1
        System.out.println("年:" + cal.get(Calendar.YEAR));
        System.out.println("月:" + (cal.get(Calendar.MONTH) + 1));
        System.out.println("日:" + cal.get(Calendar.DAY_OF_MONTH));
        System.out.println("时:" + cal.get(Calendar.HOUR_OF_DAY));
        System.out.println("分:" + cal.get(Calendar.MINUTE));
        System.out.println("秒:" + cal.get(Calendar.SECOND));


        cal.add(Calendar.SECOND,90);

        System.out.println("秒:" + cal.get(Calendar.SECOND));
    }

    @Test
    public void testType(){
        final Class<Integer> integerClass = int.class;

    }
    private boolean isWrapClass(Class clz) {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }
}
