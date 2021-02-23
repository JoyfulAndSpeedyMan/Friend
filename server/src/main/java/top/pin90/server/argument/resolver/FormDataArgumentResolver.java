package top.pin90.server.argument.resolver;

import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.core.MethodParameter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.pin90.common.annotation.FormData;
import top.pin90.common.annotation.Token;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FormDataArgumentResolver implements HandlerMethodArgumentResolver {
    private final Set<Class<?>> exclude;

    public FormDataArgumentResolver() {
        exclude = new HashSet<>(5);
        exclude.add(RequestHeader.class);
        exclude.add(RequestParam.class);
        exclude.add(PathVariable.class);
        exclude.add(Token.class);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        final HashSet<Annotation> annotations = new HashSet<>(Arrays.asList(parameter.getParameterAnnotations()));
        for (Class<?> aclass : exclude) {
            if (annotations.contains(aclass))
                return false;
        }
        return true;
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        System.out.println("resolveArgument:\t" + Thread.currentThread().getName());
        final Mono<MultiValueMap<String, String>> formDataMono = exchange.getFormData();
        final FormData parameterAnnotation = parameter.getParameterAnnotation(FormData.class);

        return formDataMono
                .publishOn(Schedulers.parallel())
                .map(map -> {
                    System.out.println("formDataMono map:\t" + Thread.currentThread().getName());

                    String name = parameter.getParameterName();
                    if (parameterAnnotation != null && !Strings.isBlank(parameterAnnotation.value()))
                        name = parameterAnnotation.value();

                    final List<String> strings = map.get(name);
                    final Class<?> parameterType = parameter.getParameterType();
                    Object resultValue;
                    if (strings != null && !strings.isEmpty()) {
                        final String value = strings.get(0);
                        resultValue = stringToObject(value, parameterType);
                    } else {
                        resultValue = stringToObject("", parameterType);
                    }
                    return resultValue;
                });
    }

    private Object stringToObject(String value, Class<?> aclass) {
        if (aclass.isAssignableFrom(ObjectId.class))
            return new ObjectId(value);
        if (aclass.isAssignableFrom(String.class))
            return value;
        if (isInteger(aclass))
            return Integer.parseInt(value);
        if (isFloat(aclass))
            return Float.parseFloat(value);
        if (isDouble(aclass))
            return Double.parseDouble(value);
        return value;
    }

    private boolean isInteger(Class<?> aclass) {
        if (aclass == Integer.class || aclass == int.class)
            return true;
        return false;
    }

    private boolean isFloat(Class<?> aclass) {
        if (aclass == Float.class || aclass == float.class)
            return true;
        return false;
    }

    private boolean isDouble(Class<?> aclass) {
        if (aclass == Double.class || aclass == double.class)
            return true;
        return false;
    }

}

