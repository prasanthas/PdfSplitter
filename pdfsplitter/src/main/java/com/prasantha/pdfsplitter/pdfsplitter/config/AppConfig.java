package com.prasantha.pdfsplitter.pdfsplitter.config;

import org.apache.pdfbox.multipdf.Splitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    public Splitter createSplitter() {
        return new Splitter();
    }

}
