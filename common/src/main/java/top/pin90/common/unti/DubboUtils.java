package top.pin90.common.unti;

import top.pin90.common.pojo.info.ServerInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

public class DubboUtils {


    public static List<ServerInfo> parseInfo(List<String> list) {
        return list.stream()
                .map(DubboUtils::parseInfo)
                .collect(Collectors.toList());
    }

    public static ServerInfo parseInfo(String s) {
        String demo = "dubbo://192.168.140.2:20880/top.pin.friend.chatserverapi.service.MessageService?anyhost=true&application=provider&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&interface=top.pin.friend.chatserverapi.service.MessageService&metadata-type=remote&methods=test&organization=dubbox&owner=programmer&pid=4180&release=2.7.8&side=provider&timestamp=1611897162364";
        try {
            s=URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        int i = s.indexOf("://");
        if (i == -1)
            return null;
        if (i + 3 == s.length())
            return null;
        String s2 = s.substring(i + 3);
        int j = s2.indexOf(':');
        String host = s2.substring(0, j);
        String ls = s2.substring(j);
        int pi = s2.indexOf('/');
        String sport = s2.substring(j + 1, pi);
        int port = Integer.parseInt(sport);

        return new ServerInfo(host, port);
    }
}
