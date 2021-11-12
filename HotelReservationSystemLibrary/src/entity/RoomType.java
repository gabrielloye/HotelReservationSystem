/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.Bed;

@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(min = 1, max = 64)
    private String name;
    @Column(nullable = false, length = 128)
    @NotNull
    @Size(min = 0, max = 128)
    private String description;
    @Column(nullable = false, length = 4)
    @NotNull
    private Integer size;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private List<Bed> beds;
    @Column(nullable = false, length = 2)
    @NotNull
    private Integer capacity;
    @Column(nullable = false)
    @NotNull
    private List<String> amenities;
    @Column(nullable = false)
    @NotNull
    private Boolean disabled;
    
    @OneToMany(mappedBy = "roomType")
    private List<Reservation> reservations;
    
    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;
    
    @OneToMany(mappedBy = "roomType")
    private List<RoomRate> roomRates;
    
    @OneToOne(mappedBy = "lowerRoomType")
    private RoomType higherRoomType;
    
    @OneToOne
    private RoomType lowerRoomType;
    

    public RoomType() {
        this.reservations = new ArrayList<>();
        this.rooms = new ArrayList<>();
        this.roomRates = new ArrayList<>();
    }

    public RoomType(String name, String description, Integer size, List<Bed> beds, Integer capacity, List<String> amenities, Boolean disabled) {
        this();
        this.name = name;
        this.description = description;
        this.size = size;
        this.beds = beds;
        this.capacity = capacity;
        this.amenities = amenities;
        this.disabled = disabled;
    }

    
    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomType[ id=" + roomTypeId + " ]";
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @return the beds
     */
    public List<Bed> getBeds() {
        return beds;
    }

    /**
     * @param beds the beds to set
     */
    public void setBeds(List<Bed> beds) {
        this.beds = beds;
    }

    /**
     * @return the capacity
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    /**
     * @return the amenities
     */
    public List<String> getAmenities() {
        return amenities;
    }

    /**
     * @param amenities the amenities to set
     */
    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
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
     * @return the reservations
     */
    public List<Reservation> getReservations() {
        return reservations;
    }

    /**
     * @param reservations the reservations to set
     */
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
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
     * @return the roomRates
     */
    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    /**
     * @param roomRates the roomRates to set
     */
    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

    public RoomType getHigherRoomType() {
        return higherRoomType;
    }

    public void setHigherRoomType(RoomType higherRoomType) {
        this.higherRoomType = higherRoomType;
    }

    public RoomType getLowerRoomType() {
        return lowerRoomType;
    }

    public void setLowerRoomType(RoomType lowerRoomType) {
        this.lowerRoomType = lowerRoomType;
    }
}
