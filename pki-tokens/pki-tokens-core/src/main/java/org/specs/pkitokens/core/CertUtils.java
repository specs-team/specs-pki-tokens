package org.specs.pkitokens.core;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PasswordFinder;

import java.io.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class CertUtils {
    public static PrivateKey readPrivateKey(File keyFile, final String password) throws Exception {

        PEMReader pemReader = null;
        if (password != null) {
            PasswordFinder passwordFinder = new PasswordFinder() {
                @Override
                public char[] getPassword() {
                    return password.toCharArray();
                }
            };
            pemReader = new PEMReader(new FileReader(keyFile), passwordFinder);
        }
        else {
            pemReader = new PEMReader(new FileReader(keyFile));
        }

        Object o = pemReader.readObject();
        if (o == null) {
            throw new Exception("Invalid PEM file: " + keyFile);
        }
        else if (o instanceof PrivateKey) {
            return (PrivateKey) o;
        }
        else if (o instanceof KeyPair) {
            return ((KeyPair) o).getPrivate();
        }
        else {
            throw new Exception("Invalid PEM object: " + o.getClass());
        }
    }

    public static X509Certificate readCertificate(File certFile) throws Exception {
        PEMReader pemReader = new PEMReader(new FileReader(certFile));
        Object o = pemReader.readObject();
        if (o == null || !(o instanceof X509Certificate)) {
            throw new Exception("Invalid PEM file: " + certFile);
        }
        else {
            return (X509Certificate) o;
        }
    }

    public static X509Certificate readCertificate(String content) throws Exception {
        PEMReader pemReader = new PEMReader(new StringReader(content));
        Object o = pemReader.readObject();
        if (o == null || !(o instanceof X509Certificate)) {
            throw new Exception("Invalid PEM data.");
        }
        else {
            return (X509Certificate) o;
        }
    }

    public static String writeToPem(X509Certificate certificate) throws IOException {
        PEMWriter pemWriter = null;
        try {
            StringWriter sw = new StringWriter();
            pemWriter = new PEMWriter(sw);
            pemWriter.writeObject(certificate);
            pemWriter.flush();
            return sw.toString();
        }
        finally {
            if (pemWriter != null) {
                pemWriter.close();
            }
        }
    }
}
