/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomExistsException;
import util.exception.RoomNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

@Local
public interface RoomSessionBeanLocal {
    
    public Long createNewRoom(Room newRoom, Long roomTypeId) throws RoomExistsException, UnknownPersistenceException, InputDataValidationException;

    public Room retrieveRoomByRoomId(Long roomId, Boolean loadReservations);

    public List<Room> retrieveAllRooms();

    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomNotFoundException;

    public void updateRoom(Room room) throws RoomNotFoundException, UpdateRoomException, InputDataValidationException;

    public void unavailRoom(Long roomId);

    public void deleteRoom(Long roomId) throws DeleteRoomException;
    
}
