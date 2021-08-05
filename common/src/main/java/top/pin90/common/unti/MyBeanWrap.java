package top.pin90.common.unti;

import org.springframework.beans.BeanWrapper;

import java.util.Map;
import java.util.Set;

/**
 * 提供对Map和Bean的访问操作，支持对象导航图
 */
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