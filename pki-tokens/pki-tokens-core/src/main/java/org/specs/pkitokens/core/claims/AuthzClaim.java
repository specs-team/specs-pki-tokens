package org.specs.pkitokens.core.claims;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class AuthzClaim implements Claim {

    @JsonProperty("sv")
    private List<SpecsService> services = new ArrayList<SpecsService>();

    public AuthzClaim() {
    }

    public List<SpecsService> getServices() {
        return services;
    }

    public void addService(SpecsService service) {
        services.add(service);
    }

    public static class SpecsService {
        @JsonProperty("id")
        public String id;

        @JsonProperty("uri")
        public String uri;

        public SpecsService() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
