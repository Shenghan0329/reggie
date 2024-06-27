package com.John.reggie.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.John.reggie.Common.JacksonObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport{

    @Override
    protected void addResourceHandlers(@SuppressWarnings("null") ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry
            .addResourceHandler("/backend/**")
            .addResourceLocations("classpath:/backend/");
        registry
            .addResourceHandler("/front/**")
            .addResourceLocations("classpath:/front/");
        log.info("Static resource mapping success");
    }

    @Override
    protected void extendMessageConverters(@SuppressWarnings("null") List<HttpMessageConverter<?>> converters) {
        // TODO Auto-generated method stub
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new JacksonObjectMapper());
        super.extendMessageConverters(converters);
        converters.add(0,converter);
    }
    
    
}
