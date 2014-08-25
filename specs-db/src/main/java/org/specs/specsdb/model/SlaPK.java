
package org.specs.specsdb.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class SlaPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "sla_id", nullable = false)
    private int slaId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 36)
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    public SlaPK() {
    }

    public SlaPK(int slaId, String userId) {
        this.slaId = slaId;
        this.userId = userId;
    }

    public int getSlaId() {
        return slaId;
    }

    public void setSlaId(int slaId) {
        this.slaId = slaId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) slaId;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SlaPK)) {
            return false;
        }
        SlaPK other = (SlaPK) object;
        if (this.slaId != other.slaId) {
            return false;
        }
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.specs.specsdb.model.SlaPK[ slaId=" + slaId + ", userId=" + userId + " ]";
    }

}
