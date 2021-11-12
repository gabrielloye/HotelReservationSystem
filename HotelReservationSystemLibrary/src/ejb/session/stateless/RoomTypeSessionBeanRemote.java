package ejb.session.stateless;

import entity.RoomType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeExistsException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

@Remote
public interface RoomTypeSessionBeanRemote {
    
    public Long createNewRoomType(RoomType newRoomType, Long lowerRoomTypeId, Long higherRoomTypeId, BigDecimal normalRate, BigDecimal publishedRate) throws RoomTypeExistsException, UnknownPersistenceException, InputDataValidationException;

    public List<RoomType> retrieveAllRoomTypes();
    
    public List<RoomType> retrieveAllRoomTypesOrderedByRank();
    
    public void updateRoomType(RoomType roomType, Long lowerRoomTypeId, Long higherRoomTypeId) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException;
    
    public void deleteRoomType(Long roomTypeId) throws DeleteRoomTypeException;
    
    public void disableRoomType(Long roomTypeId);

    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId, Boolean loadRooms, Boolean loadReservations, Boolean loadRoomRates);
    
    public List<RoomType> retrieveAvailableRoomTypes(Date startDate, Integer numRooms);
    
    public int getMaxNumRoomsForRoomType(Long roomTypeId);
    
}
