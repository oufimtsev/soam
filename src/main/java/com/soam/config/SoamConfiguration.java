package com.soam.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

@Configuration
@EnableConfigurationProperties(SoamProperties.class)
public class SoamConfiguration {
    private final SoamProperties soamProperties;

    public SoamConfiguration(SoamProperties soamProperties) {
        this.soamProperties = soamProperties;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource() {
            @Override
            protected ResourceBundle getResourceBundle(String basename, Locale locale) {
                return new ListResourceBundle() {
                    @Override
                    protected Object[][] getContents() {
                        return new Object[][] {
                                {"soam.top-entity-name", soamProperties.getTopEntityName()}
                        };
                    }
                };
            }
        };
        messageSource.setBasename("embedded");
        return messageSource;
    }
}
