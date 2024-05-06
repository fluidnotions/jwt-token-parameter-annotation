package com.fluidnotions.jwtsupport;

import com.fluidnotions.jwttokenparameterannotation.JwtTokenArgumentResolver;
import com.fluidnotions.springdatarest.DataRestJwtSupportEventHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
public class AutoConfiguration {

    @Configuration
    public class WebMvcConfig implements WebMvcConfigurer {

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new JwtTokenArgumentResolver());
        }
    }

    @Bean
    @DependsOn("repositoryRestConfigurer")
    @ConditionalOnBean(RepositoryRestConfigurer.class)
    DataRestJwtSupportEventHandler dataRestAuthEventHandler() {
        return new DataRestJwtSupportEventHandler();
    }


}
