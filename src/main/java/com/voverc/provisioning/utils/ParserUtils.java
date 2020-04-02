package com.voverc.provisioning.utils;

import java.util.HashMap;
import java.util.Map;

import static com.voverc.provisioning.common.Constants.PROPERTY_DELIMITER;
import static com.voverc.provisioning.common.Constants.PROPERTY_KEY_VALUE_DELIMITER;

public class ParserUtils {

    /**
     * Utility method to read strings in desk provisioning file format
     * @param input in format of "property1=value1\nproperty2=value2"
     * @return
     */
    public static Map<String, String> readDeskFileProperties(String input) {
        Map<String, String> overrideProperties = new HashMap<>();

        String[] propertyEntries = input.split(PROPERTY_DELIMITER);
        for (String propertyEntry : propertyEntries) {
            String[] propertyPair = propertyEntry.split(PROPERTY_KEY_VALUE_DELIMITER);
            overrideProperties.put(propertyPair[0], propertyPair[1]);
        }

        return overrideProperties;
    }
}
