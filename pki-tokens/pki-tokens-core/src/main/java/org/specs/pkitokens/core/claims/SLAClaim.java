package org.specs.pkitokens.core.claims;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SLAClaim implements Claim {

    @JsonProperty("id")
    private String slaId;

    @JsonProperty("srv")
    private List<SpecsService> services = new ArrayList<SpecsService>();

    public SLAClaim() {
    }

    public String getSlaId() {
        return slaId;
    }

    public void setSlaId(String slaId) {
        this.slaId = slaId;
    }

    public List<SpecsService> getServices() {
        return services;
    }

    public static class SpecsService {
        @JsonProperty("id")
        public String id;

        @JsonProperty("uri")
        public String uri;

        public SpecsService() {
        }

        public SpecsService(String id, String uri) {
            this.id = id;
            this.uri = uri;
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
