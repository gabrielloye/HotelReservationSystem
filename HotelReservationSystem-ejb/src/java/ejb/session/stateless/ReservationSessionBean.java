/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationExceptionReport;
import entity.Customer;
import entity.Employee;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CheckedInException;
import util.exception.CheckedOutException;
import util.exception.InputDataValidationException;
import util.exception.ReservationExistsException;
import util.exception.UnknownPersistenceException;

@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ReservationSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public List<Reservation> retrieveReservationsWithStartDate(Date startDate)
    {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.startDate = :inStartDate ORDER BY r.reservationDate ASC");
        query.setParameter("inStartDate", startDate);
        List<Reservation> reservations = query.getResultList();
        for (Reservation res : reservations)
        {
            res.getRooms().size();
            res.getAllocationExceptionReports().size();
        }
        return reservations;
    }
    
    @Override
    public void checkOutReservation(Long reservationId) throws CheckedOutException
    {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (!reservation.getCheckOut())
        {
            reservation.setCheckOut(true);
        }
        else
        {
            throw new CheckedOutException("Customer has already checked out!");
        }
    }
    
    @Override
    public void checkInReservation(Long reservationId) throws CheckedInException
    {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (!reservation.getCheckIn())
        {
            reservation.setCheckIn(true);
        }
        else
        {
            throw new CheckedInException("Customer has already checked in!");
        }
    }
    
    @Override
    public List<Room> getRoomsForReservation(Long reservationId)
    {
        Reservation reservation = em.find(Reservation.class, reservationId);
        reservation.getRooms().size();
        return reservation.getRooms();
    }
    
    @Override
    public List<AllocationExceptionReport> getAllocationReportForReservation(Long reservationId)
    {
        Reservation reservation = em.find(Reservation.class, reservationId);
        reservation.getAllocationExceptionReports().size();
        return reservation.getAllocationExceptionReports();
    }
    
    @Override
    public Long createNewReservation(Reservation newReservation, Long roomTypeId, Long customerId) throws ReservationExistsException, UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<Reservation>>constraintViolations = validator.validate(newReservation);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                em.persist(newReservation);
                RoomType rt = em.find(RoomType.class, roomTypeId);
                Customer customer = em.find(Customer.class, customerId);
                newReservation.setRoomType(rt);
                rt.getReservations().add(newReservation);
                newReservation.setCustomer(customer);
                customer.getReservations().add(newReservation);
                em.flush();

                return newReservation.getReservationId();
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new ReservationExistsException();
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
    public void associateEmployeeWithReservation(Long employeeId, Long reservationId)
    {
        Reservation reservation = em.find(Reservation.class, reservationId);
        Employee employee = em.find(Employee.class, employeeId);
        
        reservation.setEmployee(employee);
        employee.getReservations().add(reservation);
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Reservation>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
