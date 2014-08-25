
package org.specs.specsdb.model;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "sla")
@NamedQueries({
    @NamedQuery(name = "Sla.findAll", query = "SELECT s FROM Sla s")})
public class Sla implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "sla_id", nullable = false)
    private Integer slaId;
    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;
    @ManyToMany(mappedBy = "slaList")
    private List<Service> serviceList;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    @ManyToOne(optional = false)
    private User user;

    public Sla() {
    }

    public Sla(Integer slaId) {
        this.slaId = slaId;
    }

    public Integer getSlaId() {
        return slaId;
    }

    public void setSlaId(Integer slaId) {
        this.slaId = slaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (slaId != null ? slaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sla)) {
            return false;
        }
        Sla other = (Sla) object;
        if ((this.slaId == null && other.slaId != null) || (this.slaId != null && !this.slaId.equals(other.slaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.specs.specsdb.model.Sla[ slaId=" + slaId + " ]";
    }

}
