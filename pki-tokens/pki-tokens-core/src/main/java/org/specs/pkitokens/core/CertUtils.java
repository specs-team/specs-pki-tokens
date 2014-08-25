package org.specs.pkitokens.core;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
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
}
