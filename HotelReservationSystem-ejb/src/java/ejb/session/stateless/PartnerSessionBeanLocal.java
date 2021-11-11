package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerExistsException;
import util.exception.UnknownPersistenceException;

@Local
public interface PartnerSessionBeanLocal {

    public Long createNewPartner(Partner newPartner) throws PartnerExistsException, UnknownPersistenceException, InputDataValidationException;
    
    public List<Partner> retrieveAllPartners();
    
    public Partner partnerLogin(String organisation, String password) throws InvalidLoginCredentialException;
    
}
