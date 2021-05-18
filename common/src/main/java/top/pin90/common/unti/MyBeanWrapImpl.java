package top.pin90.common.unti;

import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MyBeanWrapImpl extends BeanWrapperImpl implements MyBeanWrap {
    private final Object obj;
    private final boolean isMap;

    protected MyBeanWrapImpl(Object obj) {
        super(obj);
        this.isMap = obj instanceof Map;
        this.obj = obj;
    }

    @Override
    public Object get(String name) {
        if (isMap) {
            @SuppressWarnings("rawtypes")
            Map om = (Map) obj;
            return getMapValue(om,name);
        }
        return getPropertyValue(name);
    }

    @SuppressWarnings("rawtypes")
    private Object getMapValue(Map map, String name) {
        int i = name.indexOf('.');
        if(i!=-1 && i<name.length()-1){
            String endName = name.substring(i);
            name = name.substring(0,i);
            Object o = map.get(name);
            if(o instanceof Map)
                return getMapValue((Map) o,endName);
            MyBeanWrap wrap = MyBeanWrap.wrap(o);
            return wrap.get(name);
        }
        else {
            return map.get(name);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Object set(String name, Object value) {
        if (isMap) {
            Map om = (Map) obj;
            return om.put(name, value);
        }
        setPropertyValue(name, value);
        return value;
    }

    @Override
    public Set<String> getAllPropertyNames() {
        if (isMap) {
            @SuppressWarnings("rawtypes")
            Map om = (Map) obj;
            @SuppressWarnings("unchecked")
            Set<String> set = om.keySet();
            return set;
        }
        final TreeSet<String> result = new TreeSet<>();
        final PropertyDescriptor[] descriptors = super.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            final String name = descriptor.getName();
            if (noEq(name, "class"))
                result.add(name);
        }
        return result;
    }

    @Override
    public Map<String, Object> toMap(boolean saveNull) {
        if (isMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) obj;
            return map;
        }
        final Map<String, Object> result = new TreeMap<>();
        final PropertyDescriptor[] descriptors = super.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            final String name = descriptor.getName();
            if (noEq(name, "class")) {
                final Object value = getPropertyValue(name);
                if (value != null || saveNull)
                    result.put(name, value);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> toMap() {
        return toMap(false);
    }

    private boolean noEq(String s1, String s2) {
        if (s1.length() != s2.length())
            return true;
        for (int i = 0; i < s1.length(); i++)
            if (s1.charAt(i) != s2.charAt(i))
                return true;
        return false;
    }

    @Override
    public Object getSource() {
        return obj;
    }
}
