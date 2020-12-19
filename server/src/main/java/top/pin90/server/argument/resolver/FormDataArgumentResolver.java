package top.pin90.server.argument.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.pin90.common.annotation.FormData;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public class FormDataArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        final FormData parameterAnnotation = parameter.getParameterAnnotation(FormData.class);
        final FormData methodAnnotation = parameter.getMethodAnnotation(FormData.class);
        if (parameterAnnotation == null && methodAnnotation == null)
            return false;
        return true;
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
        final Method method = parameter.getMethod();
        final Parameter[] parameters = method.getParameters();
        final Mono<MultiValueMap<String, String>> formDataMono = exchange.getFormData();
        final FormData parameterAnnotation = parameter.getParameterAnnotation(FormData.class);
        final FormData methodAnnotation = parameter.getMethodAnnotation(FormData.class);
        return formDataMono
                .map(map -> {
                    String name = parameter.getParameterName();
                    if (parameterAnnotation != null && !parameterAnnotation.value().isEmpty())
                        name = parameterAnnotation.value();

                    final List<String> strings = map.get(name);
                    final Class<?> parameterType = parameter.getParameterType();
                    if (strings != null && !strings.isEmpty()) {
                        final String value = strings.get(0);
                        return parameterType.cast(value);
                    }
                    return "";
                });
    }
}
