
package org.specs.specsdb.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "service")
@NamedQueries({
    @NamedQuery(name = "Service.findAll", query = "SELECT s FROM Service s")})
public class Service implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 36)
    @Column(name = "service_id", nullable = false, length = 36)
    private String serviceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "uri", nullable = false, length = 255)
    private String uri;
    @Size(max = 100)
    @Column(name = "name", length = 100)
    private String name;
    @JoinTable(name = "sla_has_service", joinColumns = {
        @JoinColumn(name = "service_id", referencedColumnName = "service_id", nullable = false)}, inverseJoinColumns = {
        @JoinColumn(name = "sla_id", referencedColumnName = "sla_id", nullable = false)})
    @ManyToMany
    private List<Sla> slaList;

    public Service() {
    }

    public Service(String serviceId) {
        this.serviceId = serviceId;
    }

    public Service(String serviceId, String uri) {
        this.serviceId = serviceId;
        this.uri = uri;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Sla> getSlaList() {
        return slaList;
    }

    public void setSlaList(List<Sla> slaList) {
        this.slaList = slaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceId != null ? serviceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Service)) {
            return false;
        }
        Service other = (Service) object;
        if ((this.serviceId == null && other.serviceId != null) || (this.serviceId != null && !this.serviceId.equals(other.serviceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.specs.specsdb.model.Service[ serviceId=" + serviceId + " ]";
    }

}
