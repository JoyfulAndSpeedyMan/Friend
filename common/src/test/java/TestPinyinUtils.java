import org.junit.jupiter.api.Test;
import top.pin90.common.po.user.User;
import top.pin90.common.unti.pinyin.PinyinUtils;

import java.util.ArrayList;
import java.util.Map;

public class TestPinyinUtils {
    @Test
    public void testGroup(){
        ArrayList<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setNickname("hh");
        User user2 = new User();
        user2.setNickname("于公成");
        User user3 = new User();
        user3.setNickname("由于工程");
        User user4 = new User();
        user4.setNickname("与工程");
        User user5 = new User();
        user5.setNickname("王为");
        User user6 = new User();
        user6.setNickname("hh");
        User user7 = new User();
        user7.setNickname("\uD83D\uDE01\uD83D\uDE01");
        User user8 = new User();
        user8.setNickname("刘汝祥");
        User user9 = new User();
        user9.setNickname("阿宝");
        User user10 = new User();
        user10.setNickname("阿亮");
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
        users.add(user5);
        users.add(user6);
        users.add(user7);
        users.add(user8);
        users.add(user9);
        users.add(user10);
        Map<Character, ArrayList<User>> userMap = PinyinUtils.spellGroup(users, "nickname");
        userMap.forEach((k,v)->{
            System.out.printf(k+"=");
            for(int i=0;i<v.size();i++){
                User user = v.get(i);
                if(i==0)
                    System.out.printf("[");
                System.out.printf(user.getNickname());
                if(i!=v.size()-1)
                    System.out.printf(",");
                if(i==v.size()-1)
                    System.out.printf("]");
            }
            System.out.println();
        });
    }
}
