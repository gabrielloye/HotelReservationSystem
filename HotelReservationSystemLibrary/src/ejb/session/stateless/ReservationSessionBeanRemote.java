/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface ReservationSessionBeanRemote {
    
    public List<Reservation> retrieveReservationsWithStartDate(Date startDate);
    
}
