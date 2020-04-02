package com.voverc.provisioning.service.impl;

import com.voverc.provisioning.configuration.ProvisioningConfiguration;
import com.voverc.provisioning.entity.Device;
import com.voverc.provisioning.entity.DeviceModel;
import com.voverc.provisioning.service.ProvisioningFileBuilder;
import com.voverc.provisioning.utils.ParserUtils;
import com.voverc.provisioning.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.voverc.provisioning.common.Constants.CODECS_DELIMITER;
import static com.voverc.provisioning.common.Constants.CODECS_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.DOMAIN_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.PASSWORD_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.PORT_PROPERTY_KEY;
import static com.voverc.provisioning.common.Constants.PROPERTY_DELIMITER;
import static com.voverc.provisioning.common.Constants.PROPERTY_KEY_VALUE_DELIMITER;
import static com.voverc.provisioning.common.Constants.USERNAME_PROPERTY_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeskProvisioningFileBuilder implements ProvisioningFileBuilder {

    private final ProvisioningConfiguration provisioningConfiguration;

    @Override
    public String buildProvisioningFile(Device device) {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put(USERNAME_PROPERTY_KEY, device.getUsername());
        properties.put(PASSWORD_PROPERTY_KEY, device.getPassword());
        properties.put(DOMAIN_PROPERTY_KEY, provisioningConfiguration.getDomain());
        properties.put(PORT_PROPERTY_KEY, provisioningConfiguration.getPort());
        properties.put(CODECS_PROPERTY_KEY, String.join(CODECS_DELIMITER, provisioningConfiguration.getCodecs()));

        String overrideFragment = device.getOverrideFragment();
        if (StringUtils.isNotBlank(overrideFragment)) {
            Map<String, String> overrideProperties = ParserUtils.readDeskFileProperties(overrideFragment);
            properties.putAll(overrideProperties);
        }

        return properties.entrySet().stream()
                .map(property -> property.getKey() + PROPERTY_KEY_VALUE_DELIMITER + property.getValue())
                .collect(Collectors.joining(PROPERTY_DELIMITER));
    }

    @Override
    public DeviceModel getBuilderDeviceModel() {
        return DeviceModel.DESK;
    }
}
