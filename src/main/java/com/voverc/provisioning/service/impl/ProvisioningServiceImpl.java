package com.voverc.provisioning.service.impl;

import com.voverc.provisioning.entity.Device;
import com.voverc.provisioning.entity.DeviceModel;
import com.voverc.provisioning.common.exception.DeviceNotFoundException;
import com.voverc.provisioning.common.exception.ProvisioningFileBuilderNotFoundException;
import com.voverc.provisioning.repository.DeviceRepository;
import com.voverc.provisioning.service.ProvisioningFileBuilder;
import com.voverc.provisioning.service.ProvisioningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProvisioningServiceImpl implements ProvisioningService {

    private final DeviceRepository deviceRepository;
    private final List<ProvisioningFileBuilder> provisioningFileBuilders;

    public String getProvisioningFile(String macAddress) {
        log.info("Mac address '{}' requested provisional file", macAddress);
        Device device = deviceRepository.findById(macAddress).orElseThrow(() -> new DeviceNotFoundException("Device not found: " + macAddress));
        ProvisioningFileBuilder provisioningFileBuilder = getProvisioningFileBuilder(device.getModel());
        String provisioningFile = provisioningFileBuilder.buildProvisioningFile(device);
        log.info("Mac address '{}' provisioning file created: '{}'", macAddress, provisioningFile);
        return provisioningFile;
    }

    private ProvisioningFileBuilder getProvisioningFileBuilder(DeviceModel deviceModel) {
        return provisioningFileBuilders.stream()
                .filter(builder -> Objects.equals(builder.getBuilderDeviceModel(), deviceModel))
                .findAny()
                .orElseThrow(() -> new ProvisioningFileBuilderNotFoundException("Cannot create provisional file"));
    }

}
