package top.pin90.server.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import top.pin90.common.unti.MyBeanWrap;

import java.util.Map;
@Component
public class BeanToMapConvertor implements Converter<Object, Map<String,Object>> {

    @Override
    public Map<String, Object> convert(Object source) {
        final MyBeanWrap wrap = MyBeanWrap.wrap(source);
        return wrap.toMap();
    }
}
