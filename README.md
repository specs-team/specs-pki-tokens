SPECS Security Tokens
================

The specs-security-tokens project provides infrastructure for dealing with security tokens. A SPECS security token is a digitally signed piece of information intented for transmitting assertions from the token issuer to relying parties. The token carry claims (statements) about a specific subject made by the token issuer.
The specs-security-tokens project consists of following modules:

 * security-tokens-service: a RESTful web service that issues the security tokens
 * security-tokens-client: library providing support for requesting, parsing and validating of security tokens

Security tokens are designed with two goals in mind:

 * to enable offline validation of tokens without calling the central validation service. The advantages of offline validation are shorter time and less traffic and load on the Security token service
 * to be small enough to fit into HTTP header so the token could be simply put into HTTP header with every request when calling various web services

SPECS security tokens resemble SAML assertions to a certain extent but use much simpler token model than SAML and use the JSON encoding syntax. Therefore the tokens are not intended as a full replacement for SAML assertions, but rather as a token format to be used when ease of implementation or compactness are important.

Security tokens are digitally signed which ensures the authenticity and integrity of tokens. Authenticity means that the receiver of the token can be assured that the token really comes from the Security token service, integrity means that the receiver can be sure that the token has not been altered during transmission.

Security tokens are not encrypted, therefore they must be transmitted only through secure connection. It is safe to put the token into HTTP header but it is not recommended to put them in URL query string.

More: https://github.com/specs-team/specs-pki-tokens/wiki
