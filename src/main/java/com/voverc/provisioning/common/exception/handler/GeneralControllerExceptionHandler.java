package com.voverc.provisioning.common.exception.handler;

import com.voverc.provisioning.common.exception.DataProcessingException;
import com.voverc.provisioning.common.exception.DeviceNotFoundException;
import com.voverc.provisioning.common.exception.ProvisioningFileBuilderNotFoundException;
import com.voverc.provisioning.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@ControllerAdvice
public class GeneralControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DeviceNotFoundException.class)
    @ResponseBody
    ResponseEntity<Object> handleDataNotFound(HttpServletRequest request, Throwable ex) {
        String failedResponse;
        if (StringUtils.isNotBlank(ex.getMessage())) {
            failedResponse = ex.getMessage();
        } else {
            failedResponse = "Device not found";
        }
        log.error(failedResponse, ex);
        return new ResponseEntity<>(failedResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DataProcessingException.class, ProvisioningFileBuilderNotFoundException.class})
    @ResponseBody
    ResponseEntity<Object> handleDataProcessingException(HttpServletRequest request, Throwable ex) {
        String failedResponse;
        if (StringUtils.isNotBlank(ex.getMessage())) {
            failedResponse = ex.getMessage();
        } else {
            failedResponse = "Server internal error";
        }
        log.error(failedResponse, ex);
        return new ResponseEntity<>(failedResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
