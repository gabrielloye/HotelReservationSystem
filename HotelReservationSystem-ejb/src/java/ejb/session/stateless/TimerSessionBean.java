/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationExceptionReport;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.AllocationExceptionType;


@Stateless
public class TimerSessionBean implements TimerSessionBeanRemote, TimerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanRemote;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanRemote;
    
    @EJB
    private RoomSessionBeanLocal roomSessionBeanRemote;
    
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
        
        List<Reservation> todayReservations = reservationSessionBeanRemote.retrieveReservationWithStartDate(today);
        for(Reservation reservation : todayReservations)
        {
            RoomType reservationRoomType = roomTypeSessionBeanRemote.retrieveRoomTypeByRoomTypeId(reservation.getRoomType().getRoomTypeId(), true, false, false);
            List<Room> roomsWithRoomType = new ArrayList<>();
            for (Room room : reservationRoomType.getRooms())
            {
                roomsWithRoomType.add(roomSessionBeanRemote.retrieveRoomByRoomId(room.getRoomId(), true));
            }
            
            Integer numRooms = reservation.getNumRooms();
            int counter = 0;
            
            counter = allocateRooms(reservation, roomsWithRoomType, counter, numRooms, today); //allocation with no exceptions
            
            if (counter < numRooms) //not finished allocating yet, still need to upgrade/unavailable
            {
                if (reservationRoomType.getHigherRoomType() != null) //if theres a higher roomType
                {
                    RoomType nextHigherRoomType = roomTypeSessionBeanRemote.retrieveRoomTypeByRoomTypeId(reservationRoomType.getHigherRoomType().getRoomTypeId(), true, false, false);
                    List<Room> roomsWithHigherRoomType = new ArrayList<>();
                        for (Room room : nextHigherRoomType.getRooms())
                        {
                            roomsWithHigherRoomType.add(roomSessionBeanRemote.retrieveRoomByRoomId(room.getRoomId(), true));
                        }
                        
                    counter = allocateRoomsWithUpgradedException(reservation ,roomsWithHigherRoomType, counter, numRooms, today);

                    allocateRoomsWithUnavailableException(reservation, counter, numRooms); //allocate all remaining as unavailable
                }  
                else //if no higher roomType
                {
                    allocateRoomsWithUnavailableException(reservation, counter, numRooms); //allocate all remaining as unavailable
                }
            }
        }
    }
    
    @Override
    @Asynchronous
    public void useTimer()
    {
        currentDayRoomAllocationTimer();
    }
    
    private int allocateRooms(Reservation reservation, List<Room> roomsWithRoomType, int counter, int numRooms, Date today)
    {
        for (Room room : roomsWithRoomType) 
        {
            List<Reservation> roomReservations = room.getReservations();
            if (!roomReservations.isEmpty())
            {
                Reservation latestReservation = roomReservations.get(roomReservations.size() - 1);
                if (room.getAvailable() && (latestReservation.getEndDate().before(today) || latestReservation.getEndDate().equals(today))) 
                {
                    reservation.getRooms().add(room);
                    room.getReservations().add(reservation);
                    System.out.println(String.format("Room %s allocated to Reservation %s", room.getRoomNumber(), reservation.getReservationId()));
                    counter++;
                }
            } 
            else 
            {
                reservation.getRooms().add(room);
                room.getReservations().add(reservation);
                System.out.println(String.format("Room %s allocated to Reservation %s", room.getRoomNumber(), reservation.getReservationId()));
                counter++;
            }
            if (counter == numRooms)
            {
                break;
            }
        }
        return counter;
    }
    
    private int allocateRoomsWithUpgradedException(Reservation reservation, List<Room> roomsWithHigherRoomType, int counter, int numRooms, Date today)
    {
        for (Room room : roomsWithHigherRoomType) 
        {
            List<Reservation> roomReservations = room.getReservations();
            if (!roomReservations.isEmpty())
            {
                Reservation latestReservation = roomReservations.get(roomReservations.size() - 1);
                if (room.getAvailable() && (latestReservation.getEndDate().before(today) || latestReservation.getEndDate().equals(today))) 
                {
                    reservation.getRooms().add(room);
                    room.getReservations().add(reservation);

                    AllocationExceptionReport allocationExceptionReport = new AllocationExceptionReport(AllocationExceptionType.UPGRADED, new Date());
                    em.persist(allocationExceptionReport);
                    allocationExceptionReport.setReservation(reservation);
                    reservation.getAllocationExceptionReports().add(allocationExceptionReport);
                    em.flush();

                    System.out.println(String.format("Room %s allocated to Reservation %s", room.getRoomNumber(), reservation.getReservationId()));
                    System.out.println("UPGRADED Allocation Exception Report Generated : " + allocationExceptionReport.getAllocationExceptionReportId());
                    counter++;
                }
            } 
            else 
            {
                AllocationExceptionReport allocationExceptionReport = new AllocationExceptionReport(AllocationExceptionType.UPGRADED, new Date());
                em.persist(allocationExceptionReport);
                allocationExceptionReport.setReservation(reservation);
                reservation.getAllocationExceptionReports().add(allocationExceptionReport);
                em.flush();
                reservation.getRooms().add(room);
                room.getReservations().add(reservation);
                System.out.println(String.format("Room %s allocated to Reservation %s", room.getRoomNumber(), reservation.getReservationId()));
                System.out.println("UPGRADED Allocation Exception Report Generated : " + allocationExceptionReport.getAllocationExceptionReportId());
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
}
