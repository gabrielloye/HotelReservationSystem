/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.RateType;

@Entity
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private RateType rateType;
    @Column(precision = 11, scale = 2, nullable = false)
    @NotNull
    @DecimalMin(value="0.0", inclusive = true)
    private BigDecimal ratePerNight;
    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date validityStartDate; 
    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date validityEndDate; 
    @Column(nullable = false)
    @NotNull
    private Boolean disabled;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;
    
    @ManyToMany
    private List<Reservation> reservations;
    
    public RoomRate()
    {
        this.reservations = new ArrayList<>();
    }

    public RoomRate(String name, RateType rateType, BigDecimal ratePerNight, Date validityStartDate, Date validityEndDate, Boolean disabled)
    {
        this();
        this.name = name;
        this.rateType = rateType;
        this.ratePerNight = ratePerNight;
        this.validityStartDate = validityStartDate;
        this.validityEndDate = validityEndDate;
        this.disabled = disabled;
    }
    
    public RoomRate(String name, RateType rateType, BigDecimal ratePerNight)
    {
        this();
        this.name = name;
        this.rateType = rateType;
        this.ratePerNight = ratePerNight;
        this.disabled = false;
    }
    
    
    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomRate[ id=" + roomRateId + " ]";
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the rateType
     */
    public RateType getRateType() {
        return rateType;
    }

    /**
     * @param rateType the rateType to set
     */
    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    /**
     * @return the ratePerNight
     */
    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    /**
     * @param ratePerNight the ratePerNight to set
     */
    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    /**
     * @return the validityStartDate
     */
    public Date getValidityStartDate() {
        return validityStartDate;
    }

    /**
     * @param validityStartDate the validityStartDate to set
     */
    public void setValidityStartDate(Date validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    /**
     * @return the validityEndDate
     */
    public Date getValidityEndDate() {
        return validityEndDate;
    }

    /**
     * @param validityEndDate the validityEndDate to set
     */
    public void setValidityEndDate(Date validityEndDate) {
        this.validityEndDate = validityEndDate;
    }

    /**
     * @return the disabled
     */
    public Boolean getDisabled() {
        return disabled;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @return the roomType
     */
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * @param roomType the roomType to set
     */
    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}
