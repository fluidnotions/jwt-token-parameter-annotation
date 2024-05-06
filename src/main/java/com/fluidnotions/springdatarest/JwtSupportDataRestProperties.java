package com.fluidnotions.springdatarest;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "jwtsupport.datarest.mapping")
public class JwtSupportDataRestProperties {

    private Map<String, String> mapping;

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }
}
