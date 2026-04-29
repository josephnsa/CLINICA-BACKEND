package com.clinica.salud.shared.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class ApiPreflightFilterRegistration {

    @Bean
    public FilterRegistrationBean<ApiPreflightOptionsFilter> apiPreflightFilterRegistration(
            @Value("${app.cors.allowed-origins:}") String allowedOrigins) {
        FilterRegistrationBean<ApiPreflightOptionsFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new ApiPreflightOptionsFilter(allowedOrigins));
        reg.addUrlPatterns("/api/*");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        reg.setName("apiPreflightOptionsFilter");
        return reg;
    }
}
