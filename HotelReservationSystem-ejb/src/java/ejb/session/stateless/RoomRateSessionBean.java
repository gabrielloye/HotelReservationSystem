package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RateType;
import util.exception.DeleteRoomRateException;
import util.exception.InputDataValidationException;
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomRateSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewRoomRate(RoomRate newRoomRate, Long roomTypeId) throws UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(newRoomRate);
        
        if(constraintViolations.isEmpty())
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
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
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
    public void updateRoomRate(RoomRate roomRate) throws RoomRateNotFoundException, InputDataValidationException
    {
        if(roomRate != null && roomRate.getRoomRateId() != null)
        {
            Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(roomRate);
            
            if(constraintViolations.isEmpty())
            {
                RoomRate roomRateToUpdate = retrieveRoomRateByRoomRateId(roomRate.getRoomRateId());
            
                roomRateToUpdate.setName(roomRate.getName());
                roomRateToUpdate.setRateType(roomRate.getRateType());
                roomRateToUpdate.setRatePerNight(roomRate.getRatePerNight());
                roomRateToUpdate.setValidityStartDate(roomRate.getValidityStartDate());
                roomRateToUpdate.setValidityEndDate(roomRate.getValidityEndDate());
                roomRateToUpdate.setDisabled(roomRate.getDisabled());
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
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
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomRate>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
