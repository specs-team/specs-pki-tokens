package org.specs.pkitokens.core;

public interface RevocationVerifier {

    public boolean isRevoked(String tokenId) throws Exception;

}
