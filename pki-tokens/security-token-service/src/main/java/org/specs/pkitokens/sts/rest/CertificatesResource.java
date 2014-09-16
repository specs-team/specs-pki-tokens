package org.specs.pkitokens.sts.rest;

import org.specs.pkitokens.core.CertUtils;
import org.specs.pkitokens.core.TokenSigner;
import org.specs.pkitokens.sts.utils.Conf;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.security.cert.X509Certificate;

@Path("certificates")
public class CertificatesResource {

    @GET
    @Produces("application/x-pem-file")
    @Path("{fingerprint}")
    public Response getCertificate(@PathParam("fingerprint") String fingerprint) throws Exception {
        TokenSigner tokenSigner = new TokenSigner(
                Conf.getSignerName(),
                Conf.getSigningKeyStoreFile(),
                Conf.getSigningKeyStorePass(),
                Conf.getSigningCertFingerprint(),
                Conf.getSigningPrivateKeyPass()
        );

        X509Certificate x509Certificate = tokenSigner.getSigningCertificate(fingerprint);
        if (x509Certificate != null) {
            String certPem = CertUtils.writeToPem(x509Certificate);
            return Response.ok(certPem).build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
