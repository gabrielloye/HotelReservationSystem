/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationExceptionReport;
import entity.Reservation;
import entity.Room;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.CheckedInException;
import util.exception.CheckedOutException;
import util.exception.InputDataValidationException;
import util.exception.ReservationExistsException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

@Remote
public interface ReservationSessionBeanRemote {
    
    public List<Reservation> retrieveReservationsWithStartDate(Date startDate);
    
    public void checkOutReservation(Long reservationId) throws CheckedOutException;
    
    public void checkInReservation(Long reservationId) throws CheckedInException;

    public List<Room> getRoomsForReservation(Long reservationId);

    public List<AllocationExceptionReport> getAllocationReportForReservation(Long reservationId);
    
    public Long createNewReservation(Reservation newReservation, Long roomTypeId, Long customerId) throws ReservationExistsException, UnknownPersistenceException, InputDataValidationException;
    
    public void associateEmployeeWithReservation(Long employeeId, Long reservationId);
    
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationNotFoundException;

    public Reservation retrieveReservationByReservationId(Long reservationId, boolean loadRoom, boolean loadAllocationReports) throws ReservationNotFoundException;
    
}
