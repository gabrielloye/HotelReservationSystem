package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ReservationExistsException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;


@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "organisation") String organisation,
                                @WebParam(name = "password") String password)
                    throws InvalidLoginCredentialException
    {
        Partner partner = partnerSessionBeanLocal.partnerLogin(organisation, password);
        em.detach(partner);
        partner.getReservations().clear();
        System.out.println("********** PartnerWebService.partnerLogin(): Partner " + partner.getOrganisation() + " login remotely via web service");
        return partner;
    }
    
    @WebMethod(operationName = "retrieveAvailableRoomTypes")
    public List<RoomType> retrieveAvailableRoomTypes(@WebParam(name = "startDate") Date startDate,
                                                     @WebParam(name = "numRooms") Integer numRooms)
    {
        List<RoomType> roomTypes = roomTypeSessionBeanLocal.retrieveAvailableRoomTypes(startDate, numRooms);
        for(RoomType roomType : roomTypes)
        {
            for(RoomRate roomRate : roomType.getRoomRates())
            {
                em.detach(roomRate);
                roomRate.setRoomType(null);
                roomRate.getReservations().clear();
            }
            em.detach(roomType);
            roomType.getRooms().clear();
            roomType.getReservations().clear();
            roomType.setLowerRoomType(null);
            roomType.setHigherRoomType(null);
        }
        return roomTypes;
    }
    
    @WebMethod(operationName = "createNewPartnerReservation")
    public Long createNewPartnerReservation(@WebParam(name = "organisation") String organisation,
                                            @WebParam(name = "password") String password,
                                            @WebParam(name = "newReservation") Reservation newReservation,
                                            @WebParam(name = "roomTypeId") Long roomTypeId,
                                            @WebParam(name = "roomRateIds") List<Long> roomRateIds)
                    throws InvalidLoginCredentialException, UnknownPersistenceException, InputDataValidationException
    {
        Partner partner = partnerSessionBeanLocal.partnerLogin(organisation, password);
        System.out.println("********** PartnerWebService.createNewPartnerReservation(): Partner " 
                            + partner.getOrganisation() 
                            + " login remotely via web service");
        
        Long reservationId = reservationSessionBeanLocal.createNewReservation(newReservation, roomTypeId, null, roomRateIds);
        // Associate partner and reservation
        partnerSessionBeanLocal.associatePartnerAndReservation(partner.getPartnerId(), reservationId);
        
        return reservationId;
    }
    
    @WebMethod(operationName = "allocateRoomsForReservationByReservationId")
    public void allocateRoomsForReservationByReservationId(@WebParam(name = "organisation") String organisation,
                                                           @WebParam(name = "password") String password,
                                                           @WebParam(name = "reservationId") Long reservationId)
                        throws InvalidLoginCredentialException
    {
        Partner partner = partnerSessionBeanLocal.partnerLogin(organisation, password);
        System.out.println("********** PartnerWebService.allocateRoomsForReservationByReservationId(): Partner " 
                            + partner.getOrganisation() 
                            + " login remotely via web service");
        
        reservationSessionBeanLocal.allocateRoomsForReservationByReservationId(reservationId);
    }
    
    @WebMethod(operationName = "viewPartnerReservationDetails")
    public Reservation viewPartnerReservationDetails(@WebParam(name = "organisation") String organisation,
                                                     @WebParam(name = "password") String password,
                                                     @WebParam(name = "reservationId") Long reservationId)
                    throws InvalidLoginCredentialException, ReservationNotFoundException
    {
        Partner partner = partnerSessionBeanLocal.partnerLogin(organisation, password);
        System.out.println("********** PartnerWebService.viewPartnerReservationDetails(): Partner " 
                            + partner.getOrganisation() 
                            + " login remotely via web service");
        
        
        Reservation reservation = reservationSessionBeanLocal.retrieveReservationByReservationId(reservationId, false, false);
        if(reservation.getPartner() == null || !reservation.getPartner().getPartnerId().equals(partner.getPartnerId()))
        {
            throw new ReservationNotFoundException("This reservation does not belong to Parter: " + partner.getOrganisation());
        }
        RoomType roomType = reservation.getRoomType();
        em.detach(roomType);
        roomType.getRooms().clear();
        roomType.getReservations().clear();
        roomType.getRoomRates().clear();
        roomType.setLowerRoomType(null);
        roomType.setHigherRoomType(null);

        em.detach(reservation);
        reservation.getAllocationExceptionReports().clear();
        reservation.getRooms().clear();
        reservation.setPartner(null);
        reservation.setEmployee(null);
        reservation.setCustomer(null);
        return reservation;
    }
    
    @WebMethod(operationName = "viewAllPartnerReservations")
    public List<Reservation> viewAllPartnerReservations(@WebParam(name = "organisation") String organisation,
                                                        @WebParam(name = "password") String password)
                    throws InvalidLoginCredentialException
    {
        Partner partner = partnerSessionBeanLocal.partnerLogin(organisation, password);
        System.out.println("********** PartnerWebService.viewAllPartnerReservations(): Partner " 
                            + partner.getOrganisation() 
                            + " login remotely via web service");
        
        List<Reservation> reservations = reservationSessionBeanLocal.retrieveReservationsForPartner(partner.getPartnerId());
        for(Reservation reservation : reservations)
        {
            RoomType roomType = reservation.getRoomType();
            em.detach(roomType);
            roomType.getRooms().clear();
            roomType.getReservations().clear();
            roomType.getRoomRates().clear();
            roomType.setLowerRoomType(null);
            roomType.setHigherRoomType(null);
            
            em.detach(reservation);
            reservation.getAllocationExceptionReports().clear();
            reservation.getRooms().clear();
            reservation.setPartner(null);
            reservation.setEmployee(null);
            reservation.setCustomer(null);
        }
        return reservations;
    }
}
