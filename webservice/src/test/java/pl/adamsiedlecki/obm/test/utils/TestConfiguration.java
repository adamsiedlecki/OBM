package pl.adamsiedlecki.obm.test.utils;

import pl.adamsiedlecki.obm.config.ObmConfiguration;

public class TestConfiguration {

    private static final ObmConfiguration obmConfiguration = new ObmConfiguration();

    public static ObmConfiguration obmConfiguration() {
        return obmConfiguration;
    }
}
