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

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JwtTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JwtTokenHeader.class);
    }

    @Override
    public Optional<Object> resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String token = webRequest.getHeader("Authorization");
        log.debug("token: {}", token);
        if(token != null){
            var tokenRaw = token.substring(7);
            log.debug("tokenRaw: {}", tokenRaw);
            Map<String, Object> decodedToken = decodeToken(tokenRaw);
            Class<?> tokenClass = parameter.getParameterAnnotation(JwtTokenHeader.class).value();
            return convertTokenToObject(decodedToken, tokenClass);
        }else{
            log.warn("Token is null, direct call to endpoint without token, likely not via the gateway and using dev mode");
        }
        return Optional.empty();
    }

    private Map<String, Object> decodeToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaims().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().as(Object.class)));
    }

    private Optional<Object> convertTokenToObject(Map<String, Object> token, Class<?> tokenClass) {
        var clazz = new ObjectMapper().convertValue(token, tokenClass);
        return Optional.of(clazz);
    }
}
