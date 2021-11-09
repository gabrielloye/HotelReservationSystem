/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.CheckedOutException;

@Local
public interface ReservationSessionBeanLocal {

    public List<Reservation> retrieveReservationsWithStartDate(Date startDate);

    public void checkOutReservation(Long reservationId) throws CheckedOutException;
    
}
