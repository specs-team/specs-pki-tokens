package org.specs.pkitokens.sts.jpa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "pki_token")
@NamedQueries({
        @NamedQuery(name = "PkiToken.findAll", query = "SELECT t FROM PkiToken t"),
        @NamedQuery(name = "PkiToken.findRevoked", query = "SELECT t FROM PkiToken t WHERE t.revoked=TRUE AND " +
                "t.expiryDate > CURRENT_TIMESTAMP AND t.revocationDate <= :toDate"),
        @NamedQuery(name = "PkiToken.findRevokedFrom", query = "SELECT t FROM PkiToken t WHERE t.revoked=TRUE AND " +
                "t.expiryDate > CURRENT_TIMESTAMP AND t.revocationDate >= :fromDate AND t.revocationDate <= :toDate")})
public class PkiToken implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 64, max = 64)
    @Column(name = "token_id", length = 64, updatable = false, nullable = false)
    private String tokenId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "expiry_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDate;
    @Basic
    @Column(name = "revocation_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date revocationDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    public PkiToken() {
    }

    public PkiToken(String tokenId) {
        this.tokenId = tokenId;
    }

    public PkiToken(String tokenId, Date expiryDate, boolean revoked) {
        this.tokenId = tokenId;
        this.expiryDate = expiryDate;
        this.revoked = revoked;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(Date revocationDate) {
        this.revocationDate = revocationDate;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tokenId != null ? tokenId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PkiToken)) {
            return false;
        }
        PkiToken other = (PkiToken) object;
        if ((this.tokenId == null && other.tokenId != null) || (this.tokenId != null && !this.tokenId.equals(other.tokenId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.specs.pkitokens.sts.jpa.model.Token[ tokenId=" + tokenId + " ]";
    }

}
