package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeExistsException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal
{

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomTypeSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewRoomType(RoomType newRoomType, Long lowerRoomTypeId, Long higherRoomTypeId) throws RoomTypeExistsException, UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(newRoomType);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                em.persist(newRoomType);
                updateRanks(newRoomType, lowerRoomTypeId, higherRoomTypeId);
                em.flush();
                
                return newRoomType.getRoomTypeId();
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new RoomTypeExistsException();
                    }
                    else
                    {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<RoomType> retrieveAllRoomTypes()
    {
        Query query = em.createQuery("SELECT rt FROM RoomType rt");
        
        return query.getResultList();
    }
    
    @Override
    public List<RoomType> retrieveAllRoomTypesOrderedByRank()
    {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.lowerRoomType IS NULL");
        List<RoomType> roomTypes = new ArrayList<>();
        
        try
        {
            RoomType roomType = (RoomType)query.getSingleResult();
            roomTypes.add(roomType);
            while(roomType.getHigherRoomType() != null)
            {
                roomType = roomType.getHigherRoomType();
                roomTypes.add(roomType);
            }
            return roomTypes;
        }
        catch(NoResultException ex)
        {
            return roomTypes;
        }
    }
    
    @Override
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId, Boolean loadRooms, Boolean loadReservations, Boolean loadRoomRates)
    {
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        if (loadRooms)
        {
            roomType.getRooms().size();   
        }
        if (loadReservations)
        {
            roomType.getReservations().size();
        }
        if (loadRoomRates)
        {
            roomType.getRoomRates().size();
        }
        return roomType;
    }
    
    @Override
    public void updateRoomType(RoomType roomType, Long lowerRoomTypeId, Long higherRoomTypeId) throws RoomTypeNotFoundException, UpdateRoomTypeException, InputDataValidationException
    {
        if(roomType != null && roomType.getRoomTypeId() != null)
        {
            Set<ConstraintViolation<RoomType>> constraintViolations = validator.validate(roomType);
            
            if(constraintViolations.isEmpty())
            {
                RoomType roomTypeToUpdate = retrieveRoomTypeByRoomTypeId(roomType.getRoomTypeId(), false, false, false);
             
                if(roomTypeToUpdate.getName().equals(roomType.getName()))
                {
                    // Updates here, room type name cannot be updated through this method
                    roomTypeToUpdate.setDescription(roomType.getDescription());
                    roomTypeToUpdate.setSize(roomType.getSize());
                    roomTypeToUpdate.setBeds(roomType.getBeds());
                    roomTypeToUpdate.setCapacity(roomType.getCapacity());
                    roomTypeToUpdate.setAmenities(roomType.getAmenities());
                    roomTypeToUpdate.setDisabled(roomType.getDisabled());
                    updateRanks(roomTypeToUpdate, lowerRoomTypeId, higherRoomTypeId);
                }
                else
                {
                    throw new UpdateRoomTypeException("Name of room type record to be updated does not match the existing record!");
                }
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new RoomTypeNotFoundException("Room Type ID not provided for room type to be updated");
        }
    }
    
    private void updateRanks(RoomType newRoomType, Long lowerRoomTypeId, Long higherRoomTypeId)
    {
        if(lowerRoomTypeId != null)
        {
            RoomType lowerRoomType = retrieveRoomTypeByRoomTypeId(lowerRoomTypeId, false, false, false);
            newRoomType.setLowerRoomType(lowerRoomType);
            lowerRoomType.setHigherRoomType(newRoomType);
        }
        if(higherRoomTypeId != null)
        {
            RoomType higherRoomType = retrieveRoomTypeByRoomTypeId(higherRoomTypeId, false, false, false);
            newRoomType.setHigherRoomType(higherRoomType);
            higherRoomType.setLowerRoomType(newRoomType);
        }
    }
    
    @Override
    public void deleteRoomType(Long roomTypeId) throws DeleteRoomTypeException
    {
        RoomType roomTypeToDelete = retrieveRoomTypeByRoomTypeId(roomTypeId, false, false, false);
        
        if(roomTypeToDelete.getRooms().isEmpty())
        {
            em.remove(roomTypeToDelete);
        }
        else
        {
            throw new DeleteRoomTypeException("Room Type " + roomTypeToDelete.getName() + " is associated with existing room(s) and cannot be deleted!");
        }
    }
    
    public void disableRoomType(Long roomTypeId)
    {
        RoomType roomTypeToDisable = retrieveRoomTypeByRoomTypeId(roomTypeId, false, false, false);
        roomTypeToDisable.setDisabled(true);
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomType>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
