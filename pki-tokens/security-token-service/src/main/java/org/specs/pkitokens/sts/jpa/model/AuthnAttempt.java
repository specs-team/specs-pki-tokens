package org.specs.pkitokens.sts.jpa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "authn_attempt")
@NamedQueries({
        @NamedQuery(name = "AuthnAttempt.findAll", query = "SELECT a FROM AuthnAttempt a"),
        @NamedQuery(name = "AuthnAttempt.countFailedByIp", query = "SELECT COUNT(a) FROM AuthnAttempt a WHERE " +
                "a.ipAddress = :ipAddress AND a.timestamp > :timestamp AND a.success = FALSE"),
        @NamedQuery(name = "AuthnAttempt.countSucceededByIp", query = "SELECT COUNT(a) FROM AuthnAttempt a WHERE " +
                "a.ipAddress = :ipAddress AND a.timestamp > :timestamp AND a.success = TRUE"),
        @NamedQuery(name = "AuthnAttempt.countFailedByUsername", query = "SELECT COUNT(a) FROM AuthnAttempt a WHERE " +
                "a.username = :username AND a.timestamp > :timestamp AND a.success = FALSE")
})
public class AuthnAttempt implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "username", nullable = false)
    private String username;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    @Basic
    @Column(name = "timestamp", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @Basic(optional = false)
    @NotNull
    @Column(name = "success", nullable = false)
    private boolean success;

    public AuthnAttempt() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthnAttempt that = (AuthnAttempt) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "org.specs.pkitokens.sts.jpa.model.AuthnAttempt[ id=" + id + " ]";
    }

}
