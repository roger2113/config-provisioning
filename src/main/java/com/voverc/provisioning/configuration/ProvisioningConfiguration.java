package com.voverc.provisioning.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "provisioning")
public class ProvisioningConfiguration {

    private String domain;
    private String port;
    private String[] codecs;

}
