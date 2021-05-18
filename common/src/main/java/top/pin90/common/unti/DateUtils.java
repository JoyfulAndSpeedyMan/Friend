package top.pin90.common.unti;

import java.time.Instant;
import java.util.Map;

public class DateUtils {
    public static <T> T process(T object) {
        return process(object,"createTime","updateTime");
    }
    public static <T> T process(T object, String... fields) {
        for (String field : fields) {
            process(object, field);
        }
        return object;
    }

    public static <T> T process(T object, String field) {
        int i = field.indexOf('.');
        boolean isLast = i == -1 && i != field.length() - 1;
        String name = isLast ? field : field.substring(0, i);
        String lastName = i != field.length() - 1 ? field : field.substring(i + 1);
        if (object instanceof Map) {
            Map map = (Map) object;
            Object o = map.get(name);
            if (o == null)
                return object;
            if (isLast) {
                if (o instanceof String) {
                    Instant parse = Instant.parse((String) o);
                    map.put(name, parse);
                }
            } else {
                process(o, lastName);
            }
        } else {
            MyBeanWrap wrap = MyBeanWrap.wrap(object);
            Object o = wrap.get(name);
            if (o == null)
                return object;
            if (isLast) {
                if (o instanceof String) {
                    Instant parse = Instant.parse((String) o);
                    wrap.set(name, parse);
                }
            } else {
                process(o, lastName);
            }
        }
        return object;
    }

}
