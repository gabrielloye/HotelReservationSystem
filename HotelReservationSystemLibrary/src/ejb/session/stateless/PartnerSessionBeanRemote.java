package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerExistsException;
import util.exception.UnknownPersistenceException;

@Remote
public interface PartnerSessionBeanRemote {
    
    public Long createNewPartner(Partner newPartner) throws PartnerExistsException, UnknownPersistenceException, InputDataValidationException;
    
    public List<Partner> retrieveAllPartners();
    
    public Partner partnerLogin(String organisation, String password) throws InvalidLoginCredentialException;
    
    public void associatePartnerAndReservation(Long partnerId, Long reservationId);
    
}
