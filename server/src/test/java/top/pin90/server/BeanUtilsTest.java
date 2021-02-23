package top.pin90.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import top.pin90.common.unti.MyBeanUtils;
import top.pin90.common.unti.MyBeanWrap;
import top.pin90.common.po.user.User;

import java.util.Map;
import java.util.TreeMap;

public class BeanUtilsTest {
    User user;
    Map map;
    @BeforeEach
    public void init(){
        user = User.builder()
                .phone("45555")
                .nickname("灰太狼")
                .build();
        map = new TreeMap<>();
    }

    @Test
    public void testSpringBeanUtils(){
        BeanUtils.copyProperties(user,map);
        System.out.println(user);
        System.out.println(map);
    }
    @Test
    public void testMyBeanUtils(){
    }
    @Test
    public void testBeanWrap(){
        final MyBeanWrap wuser = MyBeanWrap.wrap(user);
        final MyBeanWrap wmap = MyBeanWrap.wrap(map);
        Object nickname = wuser.get("nickname");
        wmap.set("nickname",nickname);
        System.out.println(user);
        System.out.println(map);
    }
    @Test
    public void testCopyProperties(){
        MyBeanUtils.copyProperties(user,map);
        System.out.println(user);
        System.out.println(map);
    }
}
