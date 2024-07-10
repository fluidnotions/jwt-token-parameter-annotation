package com.fluidnotions.springdatarest;

import com.fluidnotions.jwtsupport.JwtTokenHelper;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.util.Map;

@RepositoryEventHandler
public class DataRestJwtSupportEventHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public DataRestJwtSupportEventHandler() {
    }

    @HandleBeforeSave
    public void handleBeforeSave(Object entity) {
        this.log.debug("Setting tokenPayloadField lastmodifiedby before saving spring-data-REST entity: {}", entity.getClass().getSimpleName());

        try {
            String token = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("Authorization");
            this.log.info("token: {}", token);
            Map<String, Object> userProfile = (Map<String, Object>) JwtTokenHelper.decodeJwtToken(token);
            this.setEntityInstancePropertyNumber(entity, "id", "lastmodifiedby", userProfile);
        } catch (Exception var4) {
            Exception e = var4;
            this.log.error("Error: {}", e.getMessage());
        }
    }

    private void setEntityInstancePropertyNumber(Object entity, String tokenPayloadField, String entityPropertyName, Map<String, Object> userProfile) {
        Integer num = (Integer) userProfile.get(tokenPayloadField);
        if (num != null) {
            Long aLong = (long) num;
            this.setEntityInstanceProperty(entity, aLong, entityPropertyName);
        } else {
            this.log.warn("tokenPayloadField: {} is null", tokenPayloadField);
        }
    }

    private void setEntityInstanceProperty(Object entity, Object value, String entityPropertyName) {
        try {
            Class<?> clazz = entity.getClass();
            if (this.doesPropertyExist(clazz, entityPropertyName)) {
                Field field = clazz.getDeclaredField(entityPropertyName);
                field.setAccessible(true);
                if (field.getType().isAssignableFrom(Long.class)) {
                    field.set(entity, value);
                } else if (this.isUserType(field)) {
                    Object userInstance = field.getType().newInstance();
                    Field userIdField = userInstance.getClass().getDeclaredField("id");
                    userIdField.setAccessible(true);
                    userIdField.set(userInstance, value);
                    field.set(entity, userInstance);
                } else {
                    this.log.warn("Unsupported field type for field {} on entity {}", entityPropertyName, clazz.getName());
                }
            } else {
                this.log.warn("Field {} does not exist on entity {}", entityPropertyName, clazz.getName());
            }
        } catch (IllegalAccessException | NoSuchFieldException | InstantiationException e) {
            this.log.error("Error: {}, stacktrace: {}", e.getMessage(), e.getStackTrace());
        }
    }

    private boolean doesPropertyExist(Class<?> clazz, String fieldName) {
        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException var4) {
            return false;
        }
    }

    private boolean isUserType(Field field) {
        try {
            Field userIdField = field.getType().getDeclaredField("id");
            return userIdField != null && userIdField.getType().isAssignableFrom(Long.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }
}
