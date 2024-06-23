package com.fluidnotions.jwtsupport;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtTokenHelper {

    private final static Logger log = LoggerFactory.getLogger("JwtTokenHelper");
    private static ObjectMapper objectMapper;

    static{
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static Object decodeJwtToken(String token){
        return decodeJwtToken(token, null);
    }
    public static Object decodeJwtToken(String token, Class<?> tokenClass) {
        if (token != null) {
            var tokenRaw = token.substring(7);
            log.debug("tokenRaw: {}", tokenRaw);
            Map<String, Object> decodedToken = decodeToken(tokenRaw);
            if (tokenClass != null) {
                return convertTokenToObject(decodedToken, tokenClass);
            }
            return decodedToken;
        }
        if (tokenClass != null) {
            return getEmptyClassInstance(tokenClass);
        }
        return Map.of();
    }

    private static Map<String, Object> decodeToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaims().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().as(Object.class)));
    }

    private static Object convertTokenToObject(Map<String, Object> token, Class<?> tokenClass) {
        return objectMapper.convertValue(token, tokenClass);
    }

    private static Object getEmptyClassInstance(Class<?> tokenClass) {
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
