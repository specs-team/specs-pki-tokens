package org.specs.pkitokens.core.claims;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class UserClaim implements Claim {

    @JsonProperty("id")
    private String userId;

    @JsonProperty("un")
    private String username;

    @JsonProperty("fn")
    private String firstname;

    @JsonProperty("ln")
    private String lastname;

    @JsonProperty("em")
    private String email;

    @JsonProperty("ro")
    private List<String> roles = new ArrayList<String>();

    public UserClaim() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }
}
