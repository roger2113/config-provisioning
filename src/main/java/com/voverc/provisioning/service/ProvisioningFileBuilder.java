package com.voverc.provisioning.service;

import com.voverc.provisioning.entity.Device;
import com.voverc.provisioning.entity.DeviceModel;

public interface ProvisioningFileBuilder {

    String buildProvisioningFile(Device device);

    DeviceModel getBuilderDeviceModel();

}
