/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.DeleteRoomException;
import util.exception.InputDataValidationException;
import util.exception.RoomExistsException;
import util.exception.RoomNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;


@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal
{

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public RoomSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewRoom(Room newRoom, Long roomTypeId) throws RoomExistsException, UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<Room>>constraintViolations = validator.validate(newRoom);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                em.persist(newRoom);
                RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeId(roomTypeId, false, false, false);
                roomType.getRooms().add(newRoom);
                newRoom.setRoomType(roomType);
                em.flush();
                
                return newRoom.getRoomId();
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new RoomExistsException("The room with the room number, " + newRoom.getRoomNumber()  + " already exists!");
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
    public Room retrieveRoomByRoomId(Long roomId, Boolean loadReservations)
    {
        Room room = em.find(Room.class, roomId);
        if (loadReservations)
        {
            room.getReservations().size();
        }
        return room;
    }
    
    @Override
    public List<Room> retrieveAllRooms()
    {
      Query query = em.createQuery("SELECT r FROM Room r");
      
      return query.getResultList();
    }
    
    @Override
    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomNotFoundException
    {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :roomNumber");
        query.setParameter("roomNumber", roomNumber);
        
        try
        {
            return (Room)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new RoomNotFoundException("Room Number " + roomNumber + " does not exist!\n");
        }
    }
    
    @Override
    public void updateRoom(Room room) throws RoomNotFoundException, UpdateRoomException, InputDataValidationException
    {
        if(room != null && room.getRoomId() != null)
        {
            Set<ConstraintViolation<Room>> constraintViolations = validator.validate(room);
            
            if(constraintViolations.isEmpty())
            {
                Room roomToUpdate = retrieveRoomByRoomId(room.getRoomId(), false);
                
                if(roomToUpdate.getRoomNumber().equals(room.getRoomNumber()))
                {
                    // Updates here, room number cannot be updated through this method
                    roomToUpdate.setAvailable(room.getAvailable());
                    if(!roomToUpdate.getRoomType().getRoomTypeId().equals(room.getRoomType().getRoomTypeId()))
                    {
                        RoomType newRoomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeId(room.getRoomType().getRoomTypeId(), false, false, false);
                        RoomType oldRoomType = roomToUpdate.getRoomType();
                        oldRoomType.getRooms().removeIf(r -> r.getRoomId().equals(room.getRoomId()));
                        newRoomType.getRooms().add(roomToUpdate);
                        roomToUpdate.setRoomType(newRoomType);
                    }
                }
                else
                {
                    throw new UpdateRoomException("Room number of room record to be updated does not match the existing record");
                }
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new RoomNotFoundException("Room ID not provided for Room to be updated!");
        }
    }
    
    @Override
    public void deleteRoom(Long roomId) throws DeleteRoomException
    {
        Room roomToDelete = retrieveRoomByRoomId(roomId, true);
        Date currentDate = new Date();
        
        for(Reservation reservation : roomToDelete.getReservations())
        {
            // If reservation end date is equal or after current date, throw exception
            if(reservation.getEndDate().compareTo(currentDate) = 0)
            {
                throw new DeleteRoomException("Room " + roomToDelete.getRoomNumber() + " is being used/has upcoming reservation(s), and cannot be deleted!");
            }
        }
        em.remove(roomToDelete);
    }
    
    @Override
    public void unavailRoom(Long roomId)
    {
        Room roomToUnavail = retrieveRoomByRoomId(roomId, false);
        roomToUnavail.setAvailable(false);
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Room>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
