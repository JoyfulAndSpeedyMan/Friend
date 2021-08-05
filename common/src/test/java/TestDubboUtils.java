import org.junit.Test;
import top.pin90.common.pojo.info.ServerInfo;
import top.pin90.common.unti.DubboUtils;

public class TestDubboUtils {
    @Test
    public void parseHost(){
        String s="dubbo://192.168.140.2:20880/top.pin.friend.chatserverapi.service.MessageService?anyhost=true&application=provider&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&interface=top.pin.friend.chatserverapi.service.MessageService&metadata-type=remote&methods=test&organization=dubbox&owner=programmer&pid=4180&release=2.7.8&side=provider&timestamp=1611897162364";

        ServerInfo serverInfo = DubboUtils.parseInfo(s);
        System.out.println(serverInfo);
    }
}
