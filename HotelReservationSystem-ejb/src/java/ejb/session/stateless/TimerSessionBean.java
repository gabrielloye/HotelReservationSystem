/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class TimerSessionBean implements TimerSessionBeanRemote, TimerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;

    public TimerSessionBean()
    {
    }
    
    @Schedule(dayOfWeek = "*", hour = "2", info = "currentDayRoomAllocationTimer")
    public void currentDayRoomAllocationTimer() 
    {
        Date now = new Date();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(now);
        System.out.println("********** TimerSessionBean.currentDayRoomAllocationTimer(): Timeout at " + timeStamp);
        
        int year = now.getYear();
        int month = now.getMonth();
        int date = now.getDate();
        Date today = new Date(year, month, date);
        
        allocateRoomForDay(today);
    }
    
    public void allocateRoomForDay(Date date)
    {
        List<Reservation> todayReservations = reservationSessionBeanLocal.retrieveReservationsWithStartDate(date);
        for(Reservation reservation : todayReservations)
        {
            reservationSessionBeanLocal.allocateRoomsForReservationByReservationId(reservation.getReservationId());
        }
    }
    
}
