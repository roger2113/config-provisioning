package com.voverc.provisioning.service;

import com.voverc.provisioning.ProvisioningApplication;
import com.voverc.provisioning.entity.Device;
import com.voverc.provisioning.entity.DeviceModel;
import com.voverc.provisioning.common.exception.DataProcessingException;
import com.voverc.provisioning.common.exception.DeviceNotFoundException;
import com.voverc.provisioning.common.exception.ProvisioningFileBuilderNotFoundException;
import com.voverc.provisioning.repository.DeviceRepository;
import com.voverc.provisioning.service.impl.ConferenceProvisioningFileBuilder;
import com.voverc.provisioning.service.impl.DeskProvisioningFileBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ProvisioningApplication.class)
public class ProvisioningServiceTest {

    private static final String PROVISIONAL_FILE = "provisional_file";

    @Autowired
    private ProvisioningService provisioningService;

    @MockBean
    private ConferenceProvisioningFileBuilder conferenceProvisioningFileBuilder;
    @MockBean
    private DeskProvisioningFileBuilder deskProvisioningFileBuilder;
    @MockBean
    private DeviceRepository deviceRepository;

    @BeforeEach
    public void setup() {
        given(conferenceProvisioningFileBuilder.getBuilderDeviceModel()).willCallRealMethod();
        given(deskProvisioningFileBuilder.getBuilderDeviceModel()).willCallRealMethod();
    }

    @Test
    public void getProvisioningFile_expectedDeviceNotFound() {
        given(deviceRepository.findById(anyString())).willReturn(Optional.empty());
        Assertions.assertThrows(DeviceNotFoundException.class, () -> provisioningService.getProvisioningFile("any"));
    }

    @Test
    public void getProvisioningFile_expectedProvisionalFileBuilderNotFound() {
        given(deviceRepository.findById(anyString())).willReturn(Optional.of(new Device()));
        Assertions.assertThrows(ProvisioningFileBuilderNotFoundException.class, () -> provisioningService.getProvisioningFile("any"));
    }

    @Test
    public void getProvisioningFile_forConferenceDevice_expectedOk() {
        Device device = new Device();
        device.setModel(DeviceModel.CONFERENCE);
        given(deviceRepository.findById(anyString())).willReturn(Optional.of(device));
        given(conferenceProvisioningFileBuilder.buildProvisioningFile(any(Device.class))).willReturn(PROVISIONAL_FILE);
        given(deskProvisioningFileBuilder.buildProvisioningFile(any(Device.class))).willThrow(ProvisioningFileBuilderNotFoundException.class);
        String provisionalFile = provisioningService.getProvisioningFile("any");
        Assertions.assertEquals(PROVISIONAL_FILE, provisionalFile);
    }

    @Test
    public void getProvisioningFile_forConferenceDevice_expectedBuilderException() {
        Device device = new Device();
        device.setModel(DeviceModel.CONFERENCE);
        given(deviceRepository.findById(anyString())).willReturn(Optional.of(device));
        given(conferenceProvisioningFileBuilder.buildProvisioningFile(any(Device.class))).willThrow(DataProcessingException.class);
        given(deskProvisioningFileBuilder.buildProvisioningFile(any(Device.class))).willThrow(ProvisioningFileBuilderNotFoundException.class);
        Assertions.assertThrows(DataProcessingException.class, () -> provisioningService.getProvisioningFile("any"));
    }

    @Test
    public void getProvisioningFile_forDeskDevice_expectedOk() {
        Device device = new Device();
        device.setModel(DeviceModel.DESK);
        given(deviceRepository.findById(anyString())).willReturn(Optional.of(device));
        given(deskProvisioningFileBuilder.buildProvisioningFile(any(Device.class))).willReturn(PROVISIONAL_FILE);
        given(conferenceProvisioningFileBuilder.buildProvisioningFile(any(Device.class))).willThrow(ProvisioningFileBuilderNotFoundException.class);
        String provisionalFile = provisioningService.getProvisioningFile("any");
        Assertions.assertEquals(PROVISIONAL_FILE, provisionalFile);
    }

    @Test
    public void getProvisioningFile_forDeskDevice_expectedBuilderException() {
        Device device = new Device();
        device.setModel(DeviceModel.DESK);
        given(deviceRepository.findById(anyString())).willReturn(Optional.of(device));
        given(deskProvisioningFileBuilder.buildProvisioningFile(any(Device.class))).willThrow(DataProcessingException.class);
        given(conferenceProvisioningFileBuilder.buildProvisioningFile(any(Device.class))).willThrow(ProvisioningFileBuilderNotFoundException.class);
        Assertions.assertThrows(DataProcessingException.class, () -> provisioningService.getProvisioningFile("any"));
    }

}