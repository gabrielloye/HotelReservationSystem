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
import entity.RoomRate;
import entity.RoomType;
import java.util.Date;
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
import util.enumeration.AllocationExceptionType;
import util.exception.CheckedInException;
import util.exception.CheckedOutException;
import util.exception.InputDataValidationException;
import util.exception.ReservationExistsException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public ReservationSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException
    {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation != null)
        {
            return reservation;
        }
        else
        {
            throw new ReservationNotFoundException("Rerservation ID " + reservationId + " does not exist!");
        }
    }
    
    @Override
    public Reservation retrieveReservationByReservationId(Long reservationId, boolean loadRoom, boolean loadAllocationReports) throws ReservationNotFoundException
    {
        Reservation reservation = em.find(Reservation.class, reservationId);
        if (reservation != null)
        {
            if (loadRoom)
            {
                reservation.getRooms().size();
            }
            if (loadAllocationReports)
            {
                reservation.getAllocationExceptionReports().size();
            }
            return reservation;
        }
        else
        {
            throw new ReservationNotFoundException("Rerservation ID " + reservationId + " does not exist!");
        }
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
    public List<Reservation> retrieveReservationsForPartner(Long partnerId)
    {
        Query query = em.createQuery("SELECT r FROM Reservation r WHERE r.partner.partnerId = :partnerId");
        query.setParameter("partnerId", partnerId);
        
        List<Reservation> reservations = query.getResultList();
        
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
    public Long createNewReservation(Reservation newReservation, Long roomTypeId, Long customerId, List<Long> roomRateIds) throws UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<Reservation>>constraintViolations = validator.validate(newReservation);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                em.persist(newReservation);
                // Associate Room Type
                RoomType rt = em.find(RoomType.class, roomTypeId);
                newReservation.setRoomType(rt);
                rt.getReservations().add(newReservation);
                // Associate Room Rate
                for(Long roomRateId : roomRateIds)
                {
                    RoomRate rr = em.find(RoomRate.class, roomRateId);
                    rr.getReservations().add(newReservation);
                }
                // Associate Customer if applicable
                if(customerId != null)
                {
                    Customer customer = em.find(Customer.class, customerId);
                    newReservation.setCustomer(customer);
                    customer.getReservations().add(newReservation);
                }
                em.flush();

                return newReservation.getReservationId();
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
    public void associateEmployeeWithReservation(Long employeeId, Long reservationId)
    {
        Reservation reservation = em.find(Reservation.class, reservationId);
        Employee employee = em.find(Employee.class, employeeId);
        
        reservation.setEmployee(employee);
        employee.getReservations().add(reservation);
    }
    
    @Override
    public List<Room> allocateRoomsForReservationByReservationId(Long reservationId)
    {
        Date now = new Date();
        Date today = new Date(now.getYear(), now.getMonth(), now.getDate());
        
        Reservation reservation = em.find(Reservation.class, reservationId);
        RoomType reservationRoomType = reservation.getRoomType();
        List<Room> roomsWithRoomType = reservationRoomType.getRooms();

        Integer numRooms = reservation.getNumRooms();
        int counter = 0;

        counter = allocateRooms(reservation, roomsWithRoomType, counter, numRooms, today, false); //allocation with no exceptions

        if (counter < numRooms) //not finished allocating yet, still need to upgrade/unavailable
        {
            if (reservationRoomType.getHigherRoomType() != null) //if theres a higher roomType
            {
                RoomType nextHigherRoomType = reservationRoomType.getHigherRoomType();
                List<Room> roomsWithHigherRoomType = nextHigherRoomType.getRooms();

                counter = allocateRooms(reservation, roomsWithHigherRoomType, counter, numRooms, today, true);

                allocateRoomsWithUnavailableException(reservation, counter, numRooms); //allocate all remaining as unavailable
            }  
            else //if no higher roomType
            {
                allocateRoomsWithUnavailableException(reservation, counter, numRooms); //allocate all remaining as unavailable
            }
        }
        return reservation.getRooms();
    }
    
    private int allocateRooms(Reservation reservation, List<Room> roomsWithHigherRoomType, int counter, int numRooms, Date today, boolean upgrade)
    {
        for (Room room : roomsWithHigherRoomType) 
        {
            List<Reservation> roomReservations = room.getReservations();
            if (!roomReservations.isEmpty())
            {
                boolean allocateRoom = true;
                
                for (Reservation res : roomReservations)
                {
                    if (res.getEndDate().after(today))
                    {
                        allocateRoom = false;
                        break;
                    }
                }
                
                if (room.getAvailable() && allocateRoom) 
                {
                    reservation.getRooms().add(room);
                    room.getReservations().add(reservation);
                    System.out.println(String.format("Room %s allocated to Reservation %s", room.getRoomNumber(), reservation.getReservationId()));
                    
                    if (upgrade)
                    {
                        AllocationExceptionReport allocationExceptionReport = new AllocationExceptionReport(AllocationExceptionType.UPGRADED, new Date());
                        em.persist(allocationExceptionReport);
                        allocationExceptionReport.setReservation(reservation);
                        reservation.getAllocationExceptionReports().add(allocationExceptionReport);
                        em.flush();
                        System.out.println("UPGRADED Allocation Exception Report Generated : " + allocationExceptionReport.getAllocationExceptionReportId());
                    }
                    counter++;
                }
            } 
            else if(room.getAvailable())
            {
                System.out.println("DEBUG: " + room.getRoomId() + " " + reservation.getReservationId());
                reservation.getRooms().add(room);
                room.getReservations().add(reservation);
                System.out.println(String.format("Room %s allocated to Reservation %s", room.getRoomNumber(), reservation.getReservationId()));
                
                if (upgrade)
                {
                    AllocationExceptionReport allocationExceptionReport = new AllocationExceptionReport(AllocationExceptionType.UPGRADED, new Date());
                    em.persist(allocationExceptionReport);
                    allocationExceptionReport.setReservation(reservation);
                    reservation.getAllocationExceptionReports().add(allocationExceptionReport);
                    em.flush();
                    System.out.println("UPGRADED Allocation Exception Report Generated : " + allocationExceptionReport.getAllocationExceptionReportId());
                }
                counter++;
            }

            if (counter == numRooms)
            {
                break;
            }
        }
        return counter;
    }
    
    private void allocateRoomsWithUnavailableException(Reservation reservation, int counter, int numRooms)
    {
        while (counter < numRooms)
        {
            AllocationExceptionReport allocationExceptionReport = new AllocationExceptionReport(AllocationExceptionType.UNAVAILABLE, new Date());
            em.persist(allocationExceptionReport);
            allocationExceptionReport.setReservation(reservation);
            reservation.getAllocationExceptionReports().add(allocationExceptionReport);
            em.flush();

            System.out.println("No room available for allocation for Reservation " + reservation.getReservationId());
            System.out.println("UNAVAILABLE Allocation Exception Report Generated : " + allocationExceptionReport.getAllocationExceptionReportId());
            counter++;
        }
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
