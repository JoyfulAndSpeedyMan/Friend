package top.pin90.common.unti;

import java.util.Formatter;
import java.util.Random;

public class NumFormat {
    private static Random rand=new Random();

    public static String numCode6(Integer num){
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);
        formatter.format("%06d",num);
        return stringBuilder.toString();
    }
    public static String numCode4(Integer num){
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);
        formatter.format("%04d",num);
        return stringBuilder.toString();
    }
    public static String randCode4(){
        final int code = rand.nextInt(10000);
        return numCode4(code);
    }
    public static String randCode6(){
        final int code = rand.nextInt(1000000);
        return numCode6(code);
    }

}
