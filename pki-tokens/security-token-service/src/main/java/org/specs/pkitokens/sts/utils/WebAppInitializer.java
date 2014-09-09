package org.specs.pkitokens.sts.utils;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.specs.pkitokens.sts.ips.IpsFactory;
import org.specs.pkitokens.sts.jpa.EMF;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.security.Security;

public class WebAppInitializer implements ServletContextListener {
    protected static Logger log = Logger.getLogger(WebAppInitializer.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ServletContext context = servletContextEvent.getServletContext();
            String configFilePath = context.getInitParameter("conf-file");
            if (configFilePath == null) {
                throw new RuntimeException("Missing parameter 'conf-file' in web.xml file.");
            }

            // load configuration file
            Conf.load(configFilePath);

            // initialize JPA context
            EMF.init("StsMySQLPersistenceUnit");

            org.specs.specsdb.utils.EMF.init("SpecsdbMySQLPersistenceUnit");

            IpsFactory.init();

            // register BouncyCastleProvider
            if (Security.getProvider("BC") == null) {
                Security.addProvider(new BouncyCastleProvider());
            }

            log.info("Security-token-service was initialized successfully.");
        }
        catch (Exception e) {
            log.error("Failed to initialize security-token-service: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize security-token-service.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        EMF.close();
    }
}
