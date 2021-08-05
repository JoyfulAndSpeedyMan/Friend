package top.pin90.server;

import org.junit.jupiter.api.Test;

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

    @Test
    public void OSTest(){
        String name=	"os.name"; // 操作系统的名称
        String arch=	"os.arch"; // 操作系统的架构
        String version=	"os.version"; // 操作系统的版本号
        System.out.println(System.getProperty(name));
        System.out.println(System.getProperty(arch));

        System.out.println(System.getProperty(version));

    }
    @Test
    public void intTest(){
        int a=300;
        Integer b=300;
        Integer c=300;
        System.out.println(a==b);
        System.out.println(b==c);


    }
}
