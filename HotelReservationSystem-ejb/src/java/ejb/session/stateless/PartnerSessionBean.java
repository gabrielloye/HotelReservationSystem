package ejb.session.stateless;

import entity.Partner;
import entity.Reservation;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerExistsException;
import util.exception.PartnerNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal
{

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public PartnerSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewPartner(Partner newPartner) throws PartnerExistsException, UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<Partner>>constraintViolations = validator.validate(newPartner);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                em.persist(newPartner);
                em.flush();

                return newPartner.getPartnerId();
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new PartnerExistsException();
                    }
                    else
                    {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<Partner> retrieveAllPartners()
    {
        Query query = em.createQuery("SELECT p FROM Partner p");
        
        return query.getResultList();
    }
    
    public Partner retrievePartnerByOrganisation(String organisation) throws PartnerNotFoundException
    {
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.organisation = :organisation");
        query.setParameter("organisation", organisation);
        
        try
        {
            return (Partner)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new PartnerNotFoundException("Partner Organisation " + organisation + " does not exist!");
        }
    }
    
    @Override
    public Partner partnerLogin(String organisation, String password) throws InvalidLoginCredentialException
    {
        try
        {
            Partner partner = retrievePartnerByOrganisation(organisation);
            
            if(partner.getPassword().equals(password))
            {
                return partner;
            }
            else
            {
                throw new InvalidLoginCredentialException("Organisation does not exist or invalid password!");
            }
        }
        catch(PartnerNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Organisation does not exist or invalid password!");
        }
    }
    
    @Override
    public void associatePartnerAndReservation(Long partnerId, Long reservationId)
    {
        Partner partner = em.find(Partner.class, partnerId);
        Reservation reservation = em.find(Reservation.class, reservationId);
        
        partner.getReservations().add(reservation);
        reservation.setPartner(partner);
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Partner>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
