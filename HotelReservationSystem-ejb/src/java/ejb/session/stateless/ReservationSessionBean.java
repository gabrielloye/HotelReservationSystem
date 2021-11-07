/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    
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
}
