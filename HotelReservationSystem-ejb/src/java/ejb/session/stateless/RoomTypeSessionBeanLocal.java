package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeExistsException;
import util.exception.UnknownPersistenceException;

@Local
public interface RoomTypeSessionBeanLocal {

    public Long createNewRoomType(RoomType newRoomType) throws RoomTypeExistsException, UnknownPersistenceException, InputDataValidationException;

    public List<RoomType> retrieveAllRoomTypes();
    
}
