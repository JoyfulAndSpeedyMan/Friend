package top.pin90.server;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class TestTime {
    @Test
    public void test(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Calendar instance = Calendar.getInstance();
        Date time = instance.getTime();
        System.out.println(format.format(time));
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
    public void testFormat(){
        Instant date = Instant.now();//代替date
        System.out.println("instant:"+date);
        LocalDate date2 = LocalDate.now();
        System.out.println("LocalDate:"+date2);
        LocalDateTime date3 = LocalDateTime.now();//代替calendar
        System.out.println("LocalDateTime:"+date3);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");//代替simpleDateFormat
        System.out.println("DateTimeFormatter:"+dtf.format(date3));
    }
}
