/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import java.util.List;
import javax.ejb.Remote;
import util.exception.GuestExistsException;
import util.exception.GuestNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

@Remote
public interface GuestSessionBeanRemote {
    
    public Guest retrieveGuestByUsername(String username) throws GuestNotFoundException;

    public Guest guestLogin(String username, String password) throws InvalidLoginCredentialException;
    
    public Long createNewGuest(Guest newGuest) throws GuestExistsException, UnknownPersistenceException, InputDataValidationException;
    
    public List<Reservation> retrieveAllGuestReservations(Long guestId);
    
}
