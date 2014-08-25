package org.specs.pkitokens.sts.utils;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.io.File;

public class Conf {
    private static Conf instance = new Conf();
    private static Logger log = Logger.getLogger(Conf.class);
    private static XMLConfiguration config;

    private Conf() {
    }

    public static void load(String configFile) throws Exception {
        try {
            config = new XMLConfiguration(configFile);
            log.info(String.format("Configuration was loaded successfully from file '%s'.", configFile));
        }
        catch (Exception e) {
            throw new Exception(String.format("Failed to read configuration file '%s': %s", configFile,
                    e.getMessage()));
        }
    }

    public static File getSigningCertificateFile() {
        return new File(config.getString("signing.certificate.file"));
    }

    public static File getSigningPrivateKeyFile() {
        return new File(config.getString("signing.privateKey.file"));
    }

    public static String getSigningPrivateKeyPass() {
        return config.getString("signing.privateKey.password");
    }
}
