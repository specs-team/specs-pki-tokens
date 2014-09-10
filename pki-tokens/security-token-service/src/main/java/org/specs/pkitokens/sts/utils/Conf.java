package org.specs.pkitokens.sts.utils;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.specs.pkitokens.sts.ips.Ips;

import java.io.File;
import java.util.List;
import java.util.Properties;

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

    public static boolean getIpsAccountBlockedNotifEnabled() {
            return config.getBoolean("ips.accountBlockedNotification.enabled");
        }

    public static Properties getSmtpProperties() {
        List<Object> nameList = config.getList("ips.mail.smtp.properties.property[@name]");
        List<Object> valueList = config.getList("ips.mail.smtp.properties.property[@value]");
        Properties props = new Properties();
        for (int i=0; i<nameList.size(); i++) {
            props.put(nameList.get(i), valueList.get(i));
        }
        return props;
    }

    public static String getIpsFromName() {
        return config.getString("ips.mail.from.name");
    }

    public static String getIpsFromAddress() {
        return config.getString("ips.mail.from.address");
    }

    public static Ips.IpFilterRule getIpsIpFilterRule() {
        Ips.IpFilterRule rule = new Ips.IpFilterRule();
        rule.setTimePeriod(config.getInt("ips.ipFilter.rule.timePeriod"));
        rule.setFailedAttempts(config.getInt("ips.ipFilter.rule.failedAttempts"));
        rule.setFailedRatio(config.getFloat("ips.ipFilter.rule.failedRatio"));
        rule.setDelay(config.getInt("ips.ipFilter.rule.delay"));
        return rule;
    }

    public static Ips.UsernameFilterRule getIpsUsernameFilterRule() {
        Ips.UsernameFilterRule rule = new Ips.UsernameFilterRule();
        rule.setTimePeriod(config.getInt("ips.usernameFilter.rule.timePeriod"));
        rule.setFailedAttempts(config.getInt("ips.usernameFilter.rule.failedAttempts"));
        rule.setDelay(config.getInt("ips.usernameFilter.rule.delay"));
        return rule;
    }

    public static int getIpsAccountLockoutAttempts() {
        return config.getInt("ips.accountLockout.maxAttempt");
    }
}
