package top.pin90.common.unti;

import org.springframework.beans.BeanWrapper;

import java.util.Map;
import java.util.Set;

public interface MyBeanWrap extends BeanWrapper {
    static MyBeanWrap wrap(Object obj) {
        return new MyBeanWrapImpl(obj);
    }
    Object get(String name);
    Object set(String name, Object value);
    Set<String> getAllPropertyNames();
    Map<String, Object> toMap(boolean saveNull);
    Map<String, Object> toMap();
    Object getSource();
}