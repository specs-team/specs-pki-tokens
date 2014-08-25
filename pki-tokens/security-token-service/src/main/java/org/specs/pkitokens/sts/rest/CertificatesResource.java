package org.specs.pkitokens.sts.rest;

import org.specs.pkitokens.sts.utils.Conf;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;

@Path("certificates")
public class CertificatesResource {

    @GET
    @Produces("application/x-pem-file")
    @Path("signing")
    public Response getSigningCertificate() {
        File signingCertFile = Conf.getSigningCertificateFile();
        return Response.ok(signingCertFile).build();
    }
}
