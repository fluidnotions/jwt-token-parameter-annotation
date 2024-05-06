package com.fluidnotions.springdatarest;

import com.fluidnotions.jwtsupport.JwtTokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.Map;


@RepositoryEventHandler
public class DataRestJwtSupportEventHandler {
    private JwtSupportDataRestProperties authDataRestProperties;

    public DataRestJwtSupportEventHandler(JwtSupportDataRestProperties authDataRestProperties) {
        this.authDataRestProperties = authDataRestProperties;
    }

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @HandleBeforeSave
    public void handleBeforeSave(Object entity) {
        log.info("Setting tokenPayloadField(s) before saving spring-data-REST entity: {}", entity.getClass().getSimpleName());
        try {
            String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
            log.info("token: {}", token);
            Map<String, Object> userProfile = (Map<String, Object>) JwtTokenHelper.decodeJwtToken(token);
            var mapping = authDataRestProperties.getMapping();
            for (Map.Entry<String, String> entry : mapping.entrySet()) {
                String tokenPayloadField = entry.getKey();
                String[] entityPropertyParts = entry.getValue().split(",");
                String entityPropertyName = entityPropertyParts[0];
                if (entityPropertyParts.length > 1 && entityPropertyParts[1].equals("number")) {
                    setEntityInstancePropertyNumber(entity, tokenPayloadField, entityPropertyName, userProfile);
                }
                else {
                    setEntityInstanceProperty(entity, userProfile.get(tokenPayloadField), entityPropertyName);
                }
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    private void setEntityInstancePropertyNumber(Object entity, String tokenPayloadField, String entityPropertyName, Map<String, Object> userProfile) {
        Integer num = (Integer) userProfile.get(tokenPayloadField);
        if (num != null) {
            Long aLong = Long.valueOf(num);
            setEntityInstanceProperty(entity, aLong, entityPropertyName);
        }
        else {
            log.warn("tokenPayloadField: {} is null", tokenPayloadField);
        }
    }

    private void setEntityInstanceProperty(Object entity, Object value, String entityPropertyName) {
        try {
            var clazz = entity.getClass();
            if (doesPropertyExist(clazz, entityPropertyName)) {
                Field field = entity.getClass().getDeclaredField(entityPropertyName);
                field.setAccessible(true);
                field.set(entity, value);
            }
            else {
                log.warn("Field {} does not exist on entity {}", entityPropertyName, clazz.getName());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error: {}, stacktrace: {}", e.getMessage(), e.getStackTrace());
        }
    }

    private boolean doesPropertyExist(Class<?> clazz, String fieldName) {
        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
