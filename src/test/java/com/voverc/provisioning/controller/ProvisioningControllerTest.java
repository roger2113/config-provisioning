package com.voverc.provisioning.controller;

import com.voverc.provisioning.common.exception.DataProcessingException;
import com.voverc.provisioning.common.exception.DeviceNotFoundException;
import com.voverc.provisioning.service.ProvisioningService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProvisioningController.class)
public class ProvisioningControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProvisioningService provisioningService;

    @Test
    public void getProvisioningFile_withOkResponse_forConferenceDevice() throws Exception {
        String provisioningFile = "{\"username\" : \"john\",\"password\" : \"doe\", \"codecs\" : [\"G711\",\"G729\",\"OPUS\"]}";
        given(provisioningService.getProvisioningFile(anyString())).willReturn(provisioningFile);
        mvc.perform(get("/api/v1/provisioning/11-2f"))
                .andExpect(status().isOk())
                .andExpect(content().string(provisioningFile));
    }

    @Test
    public void getProvisioningFile_withOkResponse_forDeskDevice() throws Exception {
        String provisioningFile = "username=john\npassword=doe\ncodecs=G711,G729,OPUS";
        given(provisioningService.getProvisioningFile(anyString())).willReturn(provisioningFile);
        mvc.perform(get("/api/v1/provisioning/22-2f"))
                .andExpect(status().isOk())
                .andExpect(content().string(provisioningFile));
    }

    @Test
    public void getProvisioningFile_withDeviceNotFoundException() throws Exception {
        String message = "Device not found";
        given(provisioningService.getProvisioningFile(anyString())).willThrow(new DeviceNotFoundException(message));
        mvc.perform(get("/api/v1/provisioning/33-2f"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(message));
    }

    @Test
    public void getProvisioningFile_withInternalProcessingException() throws Exception {
        String message = "Server internal error";
        given(provisioningService.getProvisioningFile(anyString())).willThrow(new DataProcessingException(message));
        mvc.perform(get("/api/v1/provisioning/44-2f"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string(message));

    }

}
