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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date reservationDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date endDate;
    @Column(nullable = false, length = 3)
    private Integer numRooms;
    @Column(precision = 11, scale = 2, nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private Boolean checkIn;
    @Column(nullable = false)
    private Boolean checkOut;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Customer customer;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = true)
    private Employee employee;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = true)
    private Partner partner;
    
    @OneToMany(mappedBy = "reservation")
    private List<AllocationExceptionReport> allocationExceptionReports;
    
    @ManyToMany
    private List<Room> rooms;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;

    public Reservation() {
        this.allocationExceptionReports = new ArrayList<>();
        this.rooms = new ArrayList<>();
    }

    public Reservation(Date reservationDate, Date startDate, Date endDate, Integer numRooms, BigDecimal price, Boolean checkIn, Boolean checkOut) {
        this();
        this.reservationDate = reservationDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numRooms = numRooms;
        this.price = price;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }
    
    /**
     * @return the reservationDate
     */
    public Date getReservationDate() {
        return reservationDate;
    }

    /**
     * @param reservationDate the reservationDate to set
     */
    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the numRooms
     */
    public Integer getNumRooms() {
        return numRooms;
    }

    /**
     * @param numRooms the numRooms to set
     */
    public void setNumRooms(Integer numRooms) {
        this.numRooms = numRooms;
    }

    /**
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * @return the checkIn
     */
    public Boolean getCheckIn() {
        return checkIn;
    }

    /**
     * @param checkIn the checkIn to set
     */
    public void setCheckIn(Boolean checkIn) {
        this.checkIn = checkIn;
    }

    /**
     * @return the checkOut
     */
    public Boolean getCheckOut() {
        return checkOut;
    }

    /**
     * @param checkOut the checkOut to set
     */
    public void setCheckOut(Boolean checkOut) {
        this.checkOut = checkOut;
    }
    
    /**
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * @return the employee
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * @param employee the employee to set
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /**
     * @return the partner
     */
    public Partner getPartner() {
        return partner;
    }

    /**
     * @param partner the partner to set
     */
    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    /**
     * @return the allocationExceptionReports
     */
    public List<AllocationExceptionReport> getAllocationExceptionReports() {
        return allocationExceptionReports;
    }

    /**
     * @param allocationExceptionReports the allocationExceptionReports to set
     */
    public void setAllocationExceptionReports(List<AllocationExceptionReport> allocationExceptionReports) {
        this.allocationExceptionReports = allocationExceptionReports;
    }

    /**
     * @return the rooms
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
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

}
