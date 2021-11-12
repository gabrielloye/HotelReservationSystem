package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.enumeration.RateType;
import util.exception.DeleteRoomRateException;
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public RoomRateSessionBean()
    {
    }
    
    @Override
    public Long createNewRoomRate(RoomRate newRoomRate, Long roomTypeId) throws UnknownPersistenceException
    {
        try
        {
            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeId(roomTypeId, false, false, false);
            em.persist(newRoomRate);
            newRoomRate.setRoomType(roomType);
            roomType.getRoomRates().add(newRoomRate);
            em.flush();
            
            return newRoomRate.getRoomRateId();
        }
        catch(PersistenceException ex)
        {
            throw new UnknownPersistenceException(ex.getMessage());
        }
    }

    @Override
    public RoomRate retrieveRoomRateByRoomRateId(Long roomRateId) throws RoomRateNotFoundException
    {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);
        
        if(roomRate != null)
        {
            return roomRate;
        }
        else
        {
            throw new RoomRateNotFoundException("Room Rate ID " + roomRateId + " does not exist!");
        }
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRates()
    {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr");
        
        return query.getResultList();
    }
    
    @Override
    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException
    {
        if(roomRate != null && roomRate.getRoomRateId() != null)
        {
            RoomRate roomRateToUpdate = retrieveRoomRateByRoomRateId(roomRate.getRoomRateId());
            
            roomRateToUpdate.setName(roomRate.getName());
            roomRateToUpdate.setRateType(roomRate.getRateType());
            roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
            roomRateToUpdate.setValidityStartDate(roomRate.getValidityStartDate());
            roomRateToUpdate.setValidityEndDate(roomRate.getValidityEndDate());
            roomRateToUpdate.setDisabled(roomRate.getDisabled());
        }
    }
    
    @Override
    public void deleteRoomRate(Long roomRateId) throws DeleteRoomRateException, RoomRateNotFoundException
    {
        RoomRate roomRateToDelete = retrieveRoomRateByRoomRateId(roomRateId);
        if(roomRateToDelete.getRateType().equals(RateType.PUBLISHED) || roomRateToDelete.getRateType().equals(RateType.NORMAL))
        {
            throw new DeleteRoomRateException("Normal/Published Room Rates cannot be deleted: Please use the update function to update the details!");
        }
        
        if(roomRateToDelete.getReservations().isEmpty())
        {
            List<RoomRate> updatedRoomRatesList = roomRateToDelete.getRoomType().getRoomRates();
            updatedRoomRatesList.removeIf(roomRate -> roomRate.getRoomRateId().equals(roomRateToDelete.getRoomRateId()));
            roomRateToDelete.getRoomType().setRoomRates(updatedRoomRatesList);
            em.remove(roomRateToDelete);
        }
        else
        {
            throw new DeleteRoomRateException("Room Rate " + roomRateId + " cannot be deleted: The room rate is in use");
        }
    }
    
    @Override
    public void disableRoomRate(Long roomRateId) throws RoomRateNotFoundException
    {
        RoomRate roomRateToDisable = retrieveRoomRateByRoomRateId(roomRateId);
        roomRateToDisable.setDisabled(true);
    }
}
