package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeExistsException;
import util.exception.UnknownPersistenceException;

@Remote
public interface RoomTypeSessionBeanRemote {
    
    public Long createNewRoomType(RoomType newRoomType, Long lowerRoomTypeId, Long higherRoomTypeId) throws RoomTypeExistsException, UnknownPersistenceException, InputDataValidationException;

    public List<RoomType> retrieveAllRoomTypes();
    
    public List<RoomType> retrieveAllRoomTypesOrderedByRank();
    
    public void updateRoomType(RoomType roomType, Long lowerRoomTypeId, Long higherRoomTypeId) throws RoomTypeExistsException, UnknownPersistenceException, InputDataValidationException;
    
    public void deleteRoomType(Long roomTypeId) throws DeleteRoomTypeException;
    
    public void disableRoomType(Long roomTypeId);

    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId, Boolean loadRooms, Boolean loadReservations, Boolean loadRoomRates);
    
}
