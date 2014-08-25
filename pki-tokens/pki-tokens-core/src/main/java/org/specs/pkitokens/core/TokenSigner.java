package org.specs.pkitokens.core;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 *  Represents an Identity that can sign tokens.
 */
public class TokenSigner {

    private X509Certificate signingCertificate;
    private PrivateKey signingPrivateKey;

    public TokenSigner(X509Certificate signingCertificate) {
        this.signingCertificate = signingCertificate;
    }

    public TokenSigner(File signingCertificateFile) throws Exception {

        this.signingCertificate = CertUtils.readCertificate(signingCertificateFile);
    }

    public TokenSigner(File signingCertificateFile,
                       File signingPrivateKeyFile,
                       String signingPrivateKeyPassword) throws Exception {

        this.signingCertificate = CertUtils.readCertificate(signingCertificateFile);
        this.signingPrivateKey = CertUtils.readPrivateKey(signingPrivateKeyFile, signingPrivateKeyPassword);
    }

    public X509Certificate getSigningCertificate() {
        return signingCertificate;
    }

    public PrivateKey getSigningPrivateKey() {
        return signingPrivateKey;
    }

    public String getSignerName() {
        return signingCertificate.getSubjectDN().getName();
    }
    /*public CMSSignedToken sign(Token token) throws Exception {

        String tokenSerialized = tokenSerializer.serialize(null);
        CMSTypedData msg = new CMSProcessableByteArray(tokenSerialized.getBytes());

        ASN1EncodableVector signedAttributes = new ASN1EncodableVector();
        Date expiryTime = new Date(new Date().getTime() + 3600*1000);
        signedAttributes.add(new Attribute(EXPIRY_TIME_OID, new DERSet(new DERUTCTime(expiryTime))));

        AttributeTable signedAttributesTable = new AttributeTable(signedAttributes);
            signedAttributesTable.toASN1EncodableVector();
            DefaultSignedAttributeTableGenerator signedAttributeGenerator = new DefaultSignedAttributeTableGenerator(signedAttributesTable);
        JcaSignerInfoGeneratorBuilder signerInfoGeneratorBuilder = new JcaSignerInfoGeneratorBuilder(new
                JcaDigestCalculatorProviderBuilder().setProvider("BC").build());
        signerInfoGeneratorBuilder.setSignedAttributeGenerator(signedAttributeGenerator);

        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC").build(signingPrivateKey);

        SignerInfoGenerator signerInfoGenerator = signerInfoGeneratorBuilder.build(sha1Signer, signingCertificate);
        gen.addSignerInfoGenerator(signerInfoGenerator);

        CMSSignedData cmsSignedData = gen.generate(msg, true);

        byte[] tokenDigest = signerInfoGenerator.getCalculatedDigest();
        String tokenDigestString = DatatypeConverter.printHexBinary(tokenDigest);

        CMSSignedToken cmsSignedToken = new CMSSignedToken();
        cmsSignedToken.setToken(token);
        cmsSignedToken.setTokenId(tokenDigestString);
        cmsSignedToken.setCmsSignedData(cmsSignedData);
        cmsSignedToken.setExpiryDate(expiryTime);

        return cmsSignedToken;
    }*/
}
