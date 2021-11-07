package ejb.session.stateless;

import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeExistsException;
import util.exception.UnknownPersistenceException;

@Local
public interface RoomTypeSessionBeanLocal {

    public Long createNewRoomType(RoomType newRoomType, Long lowerRoomTypeId, Long higherRoomTypeId) throws RoomTypeExistsException, UnknownPersistenceException, InputDataValidationException;

    public List<RoomType> retrieveAllRoomTypes();

    public List<RoomType> retrieveAllRoomTypesOrderedByRank();

    public void updateRoomType(RoomType roomType, Long lowerRoomTypeId, Long higherRoomTypeId) throws RoomTypeExistsException, UnknownPersistenceException, InputDataValidationException;

    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId, Boolean loadRooms, Boolean loadReservations, Boolean loadRoomRates);

    public List<RoomType> retrieveAvailableRoomTypes(Date startDate);
    
}
