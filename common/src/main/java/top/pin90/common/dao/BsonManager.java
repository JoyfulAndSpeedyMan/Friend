package top.pin90.common.dao;

public interface BsonManager {
    public String getBson(String path);
    public String getBson(String path,Object param);
    public String getBsonWithVar(String path, Object ...param);

}
