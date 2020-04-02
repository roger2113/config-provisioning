package com.voverc.provisioning.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voverc.provisioning.ProvisioningApplication;
import com.voverc.provisioning.configuration.ProvisioningConfiguration;
import com.voverc.provisioning.entity.Device;
import com.voverc.provisioning.common.exception.DataProcessingException;
import com.voverc.provisioning.service.impl.ConferenceProvisioningFileBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.voverc.provisioning.common.Constants.CODECS_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.DOMAIN_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.PASSWORD_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.PORT_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.USERNAME_PROPERTY_KEY;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ProvisioningApplication.class)
@EnableConfigurationProperties(value = ProvisioningConfiguration.class)
@TestPropertySource("classpath:application-test.properties")
public class ConferenceProvisioningFileBuilderTest {

    @Autowired
    private ConferenceProvisioningFileBuilder provisioningFileBuilder;
    @Autowired
    private ProvisioningConfiguration provisioningConfiguration;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void buildProvisioningFile_withoutOverrideFragment() throws JsonProcessingException {
        Device device = new Device();
        device.setUsername("user");
        device.setPassword("pass");
        String provisioningFile = provisioningFileBuilder.buildProvisioningFile(device);
        Map<String, Object> provisioningFileProperties = objectMapper.readValue(provisioningFile, new TypeReference<Map<String, Object>>() {
        });

        Assertions.assertEquals(provisioningFileProperties.get(USERNAME_PROPERTY_KEY), device.getUsername());
        Assertions.assertEquals(provisioningFileProperties.get(PASSWORD_PROPERTY_KEY), device.getPassword());
        Assertions.assertEquals(provisioningFileProperties.get(DOMAIN_PROPERTY_KEY), provisioningConfiguration.getDomain());
        Assertions.assertEquals(provisioningFileProperties.get(PORT_PROPERTY_KEY), provisioningConfiguration.getPort());

        List<String> provisioningFileCodecs = (ArrayList) provisioningFileProperties.get(CODECS_PROPERTY_KEY);
        List<String> configurationCodecs = Arrays.asList(provisioningConfiguration.getCodecs());
        Assertions.assertLinesMatch(provisioningFileCodecs, configurationCodecs);
    }

    @Test
    public void buildProvisioningFile_withOverrideFragment() throws JsonProcessingException {
        Device device = new Device();
        device.setUsername("user");
        device.setPassword("pass");
        device.setOverrideFragment("{\"domain\":\"sip.anotherdomain.com\",\"port\":\"5161\",\"timeout\":10,\"codecs\":[\"G911\",\"G720\"]}");
        String provisioningFile = provisioningFileBuilder.buildProvisioningFile(device);
        Map<String, Object> provisioningFileProperties = objectMapper.readValue(provisioningFile, new TypeReference<Map<String, Object>>() {
        });

        Assertions.assertEquals(provisioningFileProperties.get(USERNAME_PROPERTY_KEY), device.getUsername());
        Assertions.assertEquals(provisioningFileProperties.get(PASSWORD_PROPERTY_KEY), device.getPassword());
        Assertions.assertEquals(provisioningFileProperties.get(DOMAIN_PROPERTY_KEY), "sip.anotherdomain.com");
        Assertions.assertEquals(provisioningFileProperties.get(PORT_PROPERTY_KEY), "5161");
        Assertions.assertEquals((int)provisioningFileProperties.get("timeout"), 10);

        List<String> provisioningFileCodecs = (ArrayList) provisioningFileProperties.get(CODECS_PROPERTY_KEY);
        Assertions.assertLinesMatch(provisioningFileCodecs, Arrays.asList("G911","G720"));
    }

    @Test
    public void buildProvisioningFile_withOverrideFragment_withSingleCodec() throws JsonProcessingException {
        Device device = new Device();
        device.setUsername("user");
        device.setPassword("pass");
        device.setOverrideFragment("{\"codecs\":[\"G911\"]}");
        String provisioningFile = provisioningFileBuilder.buildProvisioningFile(device);
        Map<String, Object> provisioningFileProperties = objectMapper.readValue(provisioningFile, new TypeReference<Map<String, Object>>() {
        });
        List<String> provisioningFileCodecs = (ArrayList) provisioningFileProperties.get(CODECS_PROPERTY_KEY);
        Assertions.assertLinesMatch(provisioningFileCodecs, Arrays.asList("G911"));
    }

    @Test
    public void buildProvisioningFile_expectedDataProcessingException_whenJsonExceptionOccurs() {
        Device device = new Device();
        device.setUsername("user");
        device.setPassword("pass");
        device.setOverrideFragment("INVALID_JSON");
        Assertions.assertThrows(DataProcessingException.class, () -> provisioningFileBuilder.buildProvisioningFile(device));
    }

}
