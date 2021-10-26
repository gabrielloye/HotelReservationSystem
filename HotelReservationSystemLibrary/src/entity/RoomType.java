/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author hsien
 */
@Entity
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    private String name;
    private String description;
    private Integer size;
    

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
    
}
