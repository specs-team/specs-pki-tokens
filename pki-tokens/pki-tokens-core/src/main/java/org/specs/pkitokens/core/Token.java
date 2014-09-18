package org.specs.pkitokens.core;

import org.bouncycastle.util.encoders.Base64;
import org.codehaus.jackson.annotate.JsonProperty;
import org.specs.pkitokens.core.claims.Claim;
import org.specs.pkitokens.core.exceptions.InvalidTokenException;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Token {
    private static final String SEPARATOR = ".";

    private Header header;

    private Payload payload;

    private String encodedValue;

    private String tokenId;

    public Token() {
        payload = new Payload();
    }

    public Header getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
    }

    public String getEncodedValue() {
        return encodedValue;
    }

    public String sign(TokenSigner signer) throws Exception {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600 * 1000); // TODO: conf

        header = new Header();
        header.setSignatureAlgorithm("SHA256withRSA"); // TODO: conf
        header.setIssuedAt(now);
        header.setExpiryDate(expiryDate);
        header.setIssuer(signer.getSignerName());
        header.setSigningCertFingerprint(signer.getSignerFingerprint());

        // convert to JSON
        String headerJson = JacksonSerializer.writeValueAsString(header);
        String payloadJson = JacksonSerializer.writeValueAsString(payload);

        // compress the payload
        byte[] payloadCompressed = CompressUtils.compress(payloadJson);

        // Base64 encode
        byte[] headerEncoded = Base64.encode(headerJson.getBytes());
        byte[] payloadEncoded = Base64.encode(payloadCompressed);

        // prepare data to be signed
        String dataToBeSigned = headerJson + payloadJson;
        byte[] dataToBeSignedBytes = dataToBeSigned.getBytes("UTF-8");

        // create the signature
        Signature sigInstance = Signature.getInstance("SHA256withRSA");
        sigInstance.initSign(signer.getSigningPrivateKey());
        sigInstance.update(dataToBeSignedBytes);
        byte[] signature = sigInstance.sign();
        byte[] signatureEncoded = Base64.encode(signature);

        // compute the signature digest which is used as a token ID
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(signature);
        byte[] digest = md.digest();
        tokenId = DatatypeConverter.printHexBinary(digest);

        StringBuilder sb = new StringBuilder(headerEncoded.length + payloadEncoded.length +
                signatureEncoded.length + 2);
        sb.append(new String(headerEncoded));
        sb.append(SEPARATOR);
        sb.append(new String(payloadEncoded));
        sb.append(SEPARATOR);
        sb.append(new String(signatureEncoded));
        encodedValue = sb.toString();

        return encodedValue;
    }

    public static Token decode(String encodedToken, VerificationCertProvider verifCertProvider,
                               RevocationVerifier revocationVerifier) throws Exception {

        String[] tokenParts = Pattern.compile(SEPARATOR, Pattern.LITERAL).split(encodedToken);
        if (tokenParts.length != 3) {
            throw new InvalidTokenException("Invalid format of the token.");
        }

        // decode from Base64
        byte[] headerBytes = Base64.decode(tokenParts[0].getBytes());
        byte[] payloadBytes = Base64.decode(tokenParts[1].getBytes());
        byte[] signatureBytes = Base64.decode(tokenParts[2].getBytes());

        // deserialize the header
        String headerJson = new String(headerBytes);
        Header header = JacksonSerializer.readValue(headerJson, Header.class);

        // check expiration date
        if (header.getExpiryDate().before(new Date())) {
            throw new InvalidTokenException("The token has expired.");
        }

        // decompress the payload
        String payloadJson = CompressUtils.decompress(payloadBytes);

        // get the signing certificate for signature verification
        X509Certificate verificationCert = verifCertProvider.getCertificate(header.getSigningCertFingerprint());

        // prepare data for signature verification
        String signedData = headerJson + payloadJson;

        // verify the signature
        Signature sigInstance = Signature.getInstance(header.getSignatureAlgorithm());
        sigInstance.initVerify(verificationCert);
        sigInstance.update(signedData.getBytes("UTF-8"));
        boolean isVerified = sigInstance.verify(signatureBytes);
        if (!isVerified) {
            throw new InvalidTokenException("The token's digital signature is invalid.");
        }

        // deserialize the payload
        Payload payload = JacksonSerializer.readValue(payloadJson, Payload.class);

        // compute the signature digest which is used as a token ID
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(signatureBytes);
        byte[] digest = md.digest();
        String tokenId = DatatypeConverter.printHexBinary(digest);

        // check if the token has been revoked
        if (revocationVerifier != null) {
            if (revocationVerifier.isRevoked(tokenId)) {
                throw new InvalidTokenException("The token has been revoked.");
            }
        }

        // create the token object
        Token token = new Token();
        token.header = header;
        token.payload = payload;
        token.encodedValue = encodedToken;
        token.tokenId = tokenId;

        return token;
    }

    public String getTokenId() {
        return tokenId;
    }

    public String toJson() throws IOException {
        return JacksonSerializer.writeValueAsString(this);
    }

    public String dump() throws IOException {
        String headerJson = JacksonSerializer.getObjectMapper().defaultPrettyPrintingWriter()
                .writeValueAsString(header);
        String claimsJson = JacksonSerializer.getObjectMapper().defaultPrettyPrintingWriter()
                .writeValueAsString(payload);
        return String.format("TokenId: %s\nHeader:\n%s\nPayload:\n%s", tokenId, headerJson, claimsJson);
    }

    public static class Header {

        @JsonProperty("sigAlg")
        private String signatureAlgorithm;

        @JsonProperty("iat")
        private Date issuedAt;

        @JsonProperty("exp")
        private Date expiryDate;

        @JsonProperty("iss")
        private String issuer;

        @JsonProperty("scf")
        private String signingCertFingerprint;

        public Header() {
        }

        public String getSignatureAlgorithm() {
            return signatureAlgorithm;
        }

        void setSignatureAlgorithm(String signatureAlgorithm) {
            this.signatureAlgorithm = signatureAlgorithm;
        }

        public Date getIssuedAt() {
            return issuedAt;
        }

        public void setIssuedAt(Date issuedAt) {
            this.issuedAt = issuedAt;
        }

        public Date getExpiryDate() {
            return expiryDate;
        }

        void setExpiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getIssuer() {
            return issuer;
        }

        void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getSigningCertFingerprint() {
            return signingCertFingerprint;
        }

        public void setSigningCertFingerprint(String signingCertFingerprint) {
            this.signingCertFingerprint = signingCertFingerprint;
        }
    }

    public static class Payload {
        List<Claim> claims;

        public Payload() {
            claims = new ArrayList<Claim>();
        }

        public List<Claim> getClaims() {
            return claims;
        }

        public void addClaim(Claim claim) {
            this.claims.add(claim);
        }
    }
}
