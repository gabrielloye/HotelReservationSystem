package ejb.session.stateless;

import entity.RoomRate;
import java.util.List;
import javax.ejb.Local;
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;

@Local
public interface RoomRateSessionBeanLocal {

    public List<RoomRate> retrieveAllRoomRates();

    public Long createNewRoomRate(RoomRate newRoomRate, Long roomTypeId) throws UnknownPersistenceException, InputDataValidationException;

    public RoomRate retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException;

    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, InputDataValidationException;

    public void deleteRoomRate(Long roomRateId) throws DeleteRoomRateException, RoomRateNotFoundException;

    public void disableRoomRate(Long roomRateId) throws RoomRateNotFoundException;
    
}
