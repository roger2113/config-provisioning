package com.voverc.provisioning.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voverc.provisioning.common.exception.DataProcessingException;
import com.voverc.provisioning.configuration.ProvisioningConfiguration;
import com.voverc.provisioning.entity.Device;
import com.voverc.provisioning.entity.DeviceModel;
import com.voverc.provisioning.service.ProvisioningFileBuilder;
import com.voverc.provisioning.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.voverc.provisioning.common.Constants.CODECS_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.DOMAIN_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.PASSWORD_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.PORT_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.USERNAME_PROPERTY_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConferenceProvisioningFileBuilder implements ProvisioningFileBuilder {

    private final ProvisioningConfiguration provisioningConfiguration;
    private final ObjectMapper objectMapper;

    @Override
    public String buildProvisioningFile(Device device) {
        try {
            Map<String, Object> properties = new LinkedHashMap<>();
            properties.put(USERNAME_PROPERTY_KEY, device.getUsername());
            properties.put(PASSWORD_PROPERTY_KEY, device.getPassword());
            properties.put(DOMAIN_PROPERTY_KEY, provisioningConfiguration.getDomain());
            properties.put(PORT_PROPERTY_KEY, provisioningConfiguration.getPort());
            properties.put(CODECS_PROPERTY_KEY, provisioningConfiguration.getCodecs());

            String overrideFragment = device.getOverrideFragment();
            if (StringUtils.isNotBlank(overrideFragment)) {
                Map<String, Object> overrideProperties = objectMapper.readValue(overrideFragment, new TypeReference<Map<String, Object>>() {});
                properties.putAll(overrideProperties);
            }

            return objectMapper.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            log.error("Error while building provisional file for device={}", device, e);
            throw new DataProcessingException("Cannot create provisional file");
        }
    }

    @Override
    public DeviceModel getBuilderDeviceModel() {
        return DeviceModel.CONFERENCE;
    }
}
