package org.specs.pkitokens.core.claims;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthzClaim.class, name = "AuthzClaim"),
        @JsonSubTypes.Type(value = SLAClaim.class, name = "SLAClaim"),
        @JsonSubTypes.Type(value = UserClaim.class, name = "UserClaim")
})
public interface Claim {
}
