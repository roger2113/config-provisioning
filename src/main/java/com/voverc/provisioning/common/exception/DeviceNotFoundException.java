package com.voverc.provisioning.common.exception;

public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException() {
    }

    public DeviceNotFoundException(String message) {
        super(message);
    }
}
