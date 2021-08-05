package top.pin90.common.dao;

import top.pin90.common.unti.MyBeanWrap;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CachedFileBsonManager implements BsonManager {
    public final static Pattern placeholder = Pattern.compile("#\\{(.*?)}");

    private String basePath;
    private Map<String, String> cached = new ConcurrentHashMap<>();
    public CachedFileBsonManager(String basePath) {
        this.basePath = basePath;
    }


    @Override
    public String getBson(String path) {
        if (cached.containsKey(path))
            return cached.get(path);
        URL resource = this.getClass().getClassLoader().getResource(getPath(path));
        try {
            String path1 = resource.getPath();
            if(path1.startsWith("/"))
                path1=path1.substring(1);
            byte[] bytes = Files.readAllBytes(Paths.get(path1));
            String s = new String(bytes, StandardCharsets.UTF_8);
            cached.put(path, s);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("找不到" + path);
        }
    }

    @Override
    public String getBson(String path, Object params) {
        if(params==null)
            throw new NullPointerException();
        MyBeanWrap wrap = MyBeanWrap.wrap(params);
        String bson = getBson(path);
        Matcher matcher = placeholder.matcher(bson);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()){
            String name = matcher.group(1);
            Object o = wrap.get(name);
            matcher.appendReplacement(sb,o.toString());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    public String getBsonWithVar(String path, Object... param) {
        String bson = getBson(path);
        Matcher matcher = placeholder.matcher(bson);
        StringBuffer sb = new StringBuffer();
        for (Object o : param) {
            if(!matcher.find())
                break;
            matcher.appendReplacement(sb,o.toString());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    public String getPath(String path) {
        return basePath + "/" + path + ".json";
    }
}
