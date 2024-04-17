package com.fluidnotions.jwttokenparameterannotation;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JwtTokenHeader.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String token = webRequest.getHeader("Authorization");
        log.debug("token: {}", token);
        Class<?> tokenClass = parameter.getParameterAnnotation(JwtTokenHeader.class).value();
        if(token != null){
            var tokenRaw = token.substring(7);
            log.debug("tokenRaw: {}", tokenRaw);
            Map<String, Object> decodedToken = decodeToken(tokenRaw);
            return convertTokenToObject(decodedToken, tokenClass);
        }
        return getEmptyClassInstance(tokenClass);
    }

    private Map<String, Object> decodeToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaims().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().as(Object.class)));
    }

    private Object convertTokenToObject(Map<String, Object> token, Class<?> tokenClass) {
        return new ObjectMapper().convertValue(token, tokenClass);
    }

    private Object getEmptyClassInstance(Class<?> tokenClass) {
        Object instance = null;
        if (tokenClass.isRecord()) {
            try {
                RecordComponent[] components = tokenClass.getRecordComponents();
                Object[] nulls = new Object[components.length];
                Constructor<?> constructor = tokenClass.getConstructor(Arrays.stream(components).map(RecordComponent::getType).toArray(Class[]::new));
                instance = constructor.newInstance(nulls);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                log.error("Error creating empty instance of record class", e);
            }
        }
        else {
            try {
                instance = tokenClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                log.error("Error creating empty instance of class", e);
            }
        }
        return instance;
    }
}
