package top.pin90.common.unti;

import java.util.Map;
import java.util.Set;

public class MyBeanUtils {
    public static void copyProperties(Object source, Object target){
        copyProperties(source, target,false);
    }
    @SuppressWarnings("unchecked")
    public static void copyProperties(Object source, Object target, boolean saveNull){
        if (target instanceof Map) {
            final Map<String, Object> map = MyBeanWrap.wrap(source).toMap(saveNull);
            ((Map<String, Object>) target).putAll(map);
            return;
        }
        if(source instanceof Map){
            Map<String, Object> map= (Map<String, Object>) source;
            final MyBeanWrap targetWrap = MyBeanWrap.wrap(target);
            map.forEach(targetWrap::set);
        }
        final MyBeanWrap sourceWrap = MyBeanWrap.wrap(source);
        final MyBeanWrap targetWrap = MyBeanWrap.wrap(target);
        final Set<String> names = sourceWrap.getAllPropertyNames();
        for(String name : names){
            targetWrap.set(name,sourceWrap.getPropertyValue(name));
        }
    }
    public static Map<String,Object> beanToMap(Object source,boolean saveNull){
        final MyBeanWrap wrap = MyBeanWrap.wrap(source);
        return wrap.toMap(saveNull);
    }
    public static Map<String,Object> beanToMap(Object source){
        return beanToMap(source,false);
    }
}
