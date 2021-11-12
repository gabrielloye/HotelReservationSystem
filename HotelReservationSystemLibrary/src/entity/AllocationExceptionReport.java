/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import util.enumeration.AllocationExceptionType;

@Entity
public class AllocationExceptionReport implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationExceptionReportId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AllocationExceptionType allocationExceptionType;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date date;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    public AllocationExceptionReport() {
    }

    public AllocationExceptionReport(AllocationExceptionType allocationExceptionType, Date date) {
        this.allocationExceptionType = allocationExceptionType;
        this.date = date;
    }
    

    public Long getAllocationExceptionReportId() {
        return allocationExceptionReportId;
    }

    public void setAllocationExceptionReportId(Long allocationExceptionReportId) {
        this.allocationExceptionReportId = allocationExceptionReportId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (allocationExceptionReportId != null ? allocationExceptionReportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the allocationExceptionReportId fields are not set
        if (!(object instanceof AllocationExceptionReport)) {
            return false;
        }
        AllocationExceptionReport other = (AllocationExceptionReport) object;
        if ((this.allocationExceptionReportId == null && other.allocationExceptionReportId != null) || (this.allocationExceptionReportId != null && !this.allocationExceptionReportId.equals(other.allocationExceptionReportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AllocationExceptionReport[ id=" + allocationExceptionReportId + " ]";
    }
    
    /**
     * @return the allocationExceptionType
     */
    public AllocationExceptionType getAllocationExceptionType() {
        return allocationExceptionType;
    }

    /**
     * @param allocationExceptionType the allocationExceptionType to set
     */
    public void setAllocationExceptionType(AllocationExceptionType allocationExceptionType) {
        this.allocationExceptionType = allocationExceptionType;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }    

    /**
     * @return the reservation
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * @param reservation the reservation to set
     */
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
}
