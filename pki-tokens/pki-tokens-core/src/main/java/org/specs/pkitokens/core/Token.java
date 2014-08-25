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

    private Metadata metadata;

    private ClaimsCollection claimsCollection;

    private String encodedValue;

    public Token() {
        claimsCollection = new ClaimsCollection();
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public ClaimsCollection getClaimsCollection() {
        return claimsCollection;
    }

    public String getEncodedValue() {
        return encodedValue;
    }

    public String sign(TokenSigner signer) throws Exception {

        metadata = new Metadata();
        metadata.setTokenId(UUID.randomUUID().toString());
        metadata.setSignatureAlgorithm("SHA256withRSA");
        metadata.setExpiryDate(new Date(new Date().getTime() + 3600 * 1000)); // TODO: conf
        metadata.setSignerName(signer.getSignerName());

        String metadataJson = JacksonSerializer.writeValueAsString(metadata);
        String claimsCollJson = JacksonSerializer.writeValueAsString(claimsCollection);
        byte[] claimsCollCompressed = CompressUtils.compress(claimsCollJson);

        byte[] metadataEncoded = Base64.encode(metadataJson.getBytes());
        byte[] claimsEncoded = Base64.encode(claimsCollCompressed);

        byte[] separatorArray = String.valueOf(SEPARATOR).getBytes();
        byte[] message = new byte[metadataEncoded.length + claimsEncoded.length + 1];
        System.arraycopy(metadataEncoded, 0, message, 0, metadataEncoded.length);
        System.arraycopy(separatorArray, 0, message, metadataEncoded.length, 1);
        System.arraycopy(claimsEncoded, 0, message, metadataEncoded.length + 1, claimsEncoded.length);

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

        String metadataPart = encodedToken.substring(0, pos1);

        String metadataJson = new String(Base64.decode(metadataPart.getBytes()));
        Metadata metadata = JacksonSerializer.readValue(metadataJson, Metadata.class);

        // check expiry date
        if (metadata.getExpiryDate().before(new Date())) {
            throw new ValidationException("The token is expired.");
        }

        // verify signature
        Signature sigInstance = Signature.getInstance(metadata.getSignatureAlgorithm());
        sigInstance.initVerify(tokenSigner.getSigningCertificate());
        sigInstance.update(encodedTokenBytes, 0, pos2);
        byte[] signature = Base64.decode(Arrays.copyOfRange(encodedTokenBytes, pos2 + 1, encodedTokenBytes.length));
        boolean isVerified = sigInstance.verify(signature);
        if (!isVerified) {
            throw new ValidationException("The token's digital signature is invalid.");
        }

        byte[] claimsCompressed = Base64.decode(Arrays.copyOfRange(encodedTokenBytes, pos1 + 1, pos2));
        String claimsJson = CompressUtils.decompress(claimsCompressed);
        ClaimsCollection claimsCollection = JacksonSerializer.readValue(claimsJson, ClaimsCollection.class);

        Token token = new Token();
        token.metadata = metadata;
        token.claimsCollection = claimsCollection;
        token.encodedValue = encodedToken;

        return token;
    }

    public String toJson() throws IOException {
        return JacksonSerializer.writeValueAsString(this);
    }

    public String dump() throws IOException {
        String metadataJson = JacksonSerializer.getObjectMapper().defaultPrettyPrintingWriter()
                .writeValueAsString(metadata);
        String claimsJson = JacksonSerializer.getObjectMapper().defaultPrettyPrintingWriter()
                .writeValueAsString(claimsCollection);
        return String.format("Metadata:\n%s\nClaims:\n%s", metadataJson, claimsJson);
    }

    public static class Metadata {

        @JsonProperty("id")
        private String tokenId;

        @JsonProperty("alg")
        private String signatureAlgorithm;

        @JsonProperty("exp")
        private Date expiryDate;

        @JsonProperty("signer")
        private String signerName;

        public Metadata() {
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

        public Date getExpiryDate() {
            return expiryDate;
        }

        void setExpiryDate(Date expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getSignerName() {
            return signerName;
        }

        void setSignerName(String signerName) {
            this.signerName = signerName;
        }
    }

    public static class ClaimsCollection {
        List<Claim> claims;

        public ClaimsCollection() {
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
