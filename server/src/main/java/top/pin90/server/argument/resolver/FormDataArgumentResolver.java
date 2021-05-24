package top.pin90.server.argument.resolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.util.Strings;
import org.bson.types.ObjectId;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import top.pin90.common.annotation.FormData;
import top.pin90.common.annotation.Token;

import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FormDataArgumentResolver implements HandlerMethodArgumentResolver {
    private final Set<Class<?>> exclude;
    public static final Map<String, JSONObject> jsonCache = new ConcurrentHashMap<>();
    public static final Map<String, Flux<DataBuffer>> jsonFluxCache = new ConcurrentHashMap<>();

    public FormDataArgumentResolver() {
        exclude = new HashSet<>(5);
        exclude.add(RequestHeader.class);
        exclude.add(RequestParam.class);
        exclude.add(PathVariable.class);
        exclude.add(RequestBody.class);
        exclude.add(ModelAttribute.class);
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
    public Mono<Object> resolveArgument(MethodParameter parameter,
                                        BindingContext bindingContext,
                                        ServerWebExchange exchange) {
        final Class<?> parameterType = parameter.getParameterType();

        String pName = parameter.getParameterName();
        final FormData parameterAnnotation = parameter.getParameterAnnotation(FormData.class);
        if (parameterAnnotation != null && !Strings.isBlank(parameterAnnotation.value()))
            pName = parameterAnnotation.value();
        String name = pName;
        final Mono<MultiValueMap<String, String>> formDataMono = exchange.getFormData();
        Mono<?> jsonBody;
        if (parameterAnnotation == null) {
            String id = exchange.getRequest().getId();
            Flux<DataBuffer> bufferFlux = jsonFluxCache.computeIfAbsent(id, k -> exchange.getRequest().getBody().cache());
            jsonBody = bufferFlux
                    .map(d -> d.toString(StandardCharsets.UTF_8))
                    .map(s -> {
                        return jsonCache.computeIfAbsent(id, k -> JSON.parseObject(s));
                    })
                    .onErrorContinue((e, o) -> {
                        e.printStackTrace();
                    })
                    .map(jsonObject -> {
                        Object o = jsonObject.get(name);
                        return o;
                    })
                    .next();
        } else {
            jsonBody = Mono.empty();
        }
        return formDataMono
                .publishOn(Schedulers.parallel())
                .map(map -> {
                    System.out.println("formDataMono map:\t" + Thread.currentThread().getName());

                    final List<String> strings = map.get(name);

                    if (strings != null && !strings.isEmpty()) {
                        return strings.get(0);
                    }
                    return "";
                })
                .flatMap(value -> {
                    if (value.isEmpty()) {
                        return jsonBody;
                    }
                    return Mono.just(value);
                })
                .map(value -> {
                    if (value instanceof String) {
                        Object o = stringToObject((String) value, parameterType);
                        return o;
                    }
                    else if(value instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) value;
                        Object o = jsonObject.toJavaObject(parameterType);
                        return o;
                    }
                    else{
                        return value;
                    }
                })
                .onErrorContinue((e,o)->{
                    e.printStackTrace();
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

