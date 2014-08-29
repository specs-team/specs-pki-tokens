package org.specs.pkitokens.core;

import org.bouncycastle.util.encoders.Base64;
import org.codehaus.jackson.annotate.JsonProperty;
import org.specs.pkitokens.core.claims.Claim;
import org.specs.pkitokens.core.exceptions.ValidationException;

import java.io.IOException;
import java.security.Signature;
import java.util.*;

public class Token {
    private static final char SEPARATOR = '.';

    private Header header;

    private Payload payload;

    private String encodedValue;

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
        header.setTokenId(UUID.randomUUID().toString());
        header.setSignatureAlgorithm("SHA256withRSA"); // TODO: conf
        header.setIssuedAt(now);
        header.setExpiryDate(expiryDate);
        header.setIssuer(signer.getSignerName());

        String headerJson = JacksonSerializer.writeValueAsString(header);
        String payloadJson = JacksonSerializer.writeValueAsString(payload);
        byte[] payloadCompressed = CompressUtils.compress(payloadJson);

        byte[] headerEncoded = Base64.encode(headerJson.getBytes());
        byte[] claimsEncoded = Base64.encode(payloadCompressed);

        byte[] separatorArray = String.valueOf(SEPARATOR).getBytes();
        byte[] message = new byte[headerEncoded.length + claimsEncoded.length + 1];
        System.arraycopy(headerEncoded, 0, message, 0, headerEncoded.length);
        System.arraycopy(separatorArray, 0, message, headerEncoded.length, 1);
        System.arraycopy(claimsEncoded, 0, message, headerEncoded.length + 1, claimsEncoded.length);

        Signature sigInstance = Signature.getInstance("SHA256withRSA");
        sigInstance.initSign(signer.getSigningPrivateKey());
        sigInstance.update(message);
        byte[] signature = sigInstance.sign();
        byte[] signatureEncoded = Base64.encode(signature);

        StringBuilder sb = new StringBuilder(message.length + 1 + signatureEncoded.length);
        sb.append(new String(message));
        sb.append(SEPARATOR);
        sb.append(new String(signatureEncoded));
        encodedValue = sb.toString();

        return encodedValue;
    }

    public static Token decode(String encodedToken, TokenSigner tokenSigner) throws Exception {
        int pos1 = encodedToken.indexOf(SEPARATOR);
        int pos2 = encodedToken.indexOf(SEPARATOR, pos1 + 1);
        byte[] encodedTokenBytes = encodedToken.getBytes();

        String headerPart = encodedToken.substring(0, pos1);

        String headerJson = new String(Base64.decode(headerPart.getBytes()));
        Header header = JacksonSerializer.readValue(headerJson, Header.class);

        // check expiry date
        if (header.getExpiryDate().before(new Date())) {
            throw new ValidationException("The token is expired.");
        }

        // verify signature
        Signature sigInstance = Signature.getInstance(header.getSignatureAlgorithm());
        sigInstance.initVerify(tokenSigner.getSigningCertificate());
        sigInstance.update(encodedTokenBytes, 0, pos2);
        byte[] signature = Base64.decode(Arrays.copyOfRange(encodedTokenBytes, pos2 + 1, encodedTokenBytes.length));
        boolean isVerified = sigInstance.verify(signature);
        if (!isVerified) {
            throw new ValidationException("The token's digital signature is invalid.");
        }

        byte[] claimsCompressed = Base64.decode(Arrays.copyOfRange(encodedTokenBytes, pos1 + 1, pos2));
        String claimsJson = CompressUtils.decompress(claimsCompressed);
        Payload payload = JacksonSerializer.readValue(claimsJson, Payload.class);

        Token token = new Token();
        token.header = header;
        token.payload = payload;
        token.encodedValue = encodedToken;

        return token;
    }

    public String toJson() throws IOException {
        return JacksonSerializer.writeValueAsString(this);
    }

    public String dump() throws IOException {
        String headerJson = JacksonSerializer.getObjectMapper().defaultPrettyPrintingWriter()
                .writeValueAsString(header);
        String claimsJson = JacksonSerializer.getObjectMapper().defaultPrettyPrintingWriter()
                .writeValueAsString(payload);
        return String.format("Header:\n%s\nPayload:\n%s", headerJson, claimsJson);
    }

    public static class Header {

        @JsonProperty("id")
        private String tokenId;

        @JsonProperty("sigAlg")
        private String signatureAlgorithm;

        @JsonProperty("iat")
        private Date issuedAt;

        @JsonProperty("exp")
        private Date expiryDate;

        @JsonProperty("iss")
        private String issuer;

        public Header() {
        }

        public String getTokenId() {
            return tokenId;
        }

        void setTokenId(String tokenId) {
            this.tokenId = tokenId;
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
