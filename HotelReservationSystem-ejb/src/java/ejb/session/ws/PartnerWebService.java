package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.InvalidLoginCredentialException;


@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    @EJB
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "organisation") String organisation,
                                @WebParam(name = "password") String password)
                    throws InvalidLoginCredentialException
    {
        Partner partner = partnerSessionBeanLocal.partnerLogin(organisation, password);
        System.out.println("********** PartnerWebService.partnerLogin(): Partner " + partner.getOrganisation() + " login remotely via web service");
        return partner;
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
        
        return reservationSessionBeanLocal.retrieveReservationsForPartner(partner.getPartnerId());
    }
}
