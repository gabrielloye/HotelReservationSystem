/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Local;
import util.exception.CustomerExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

@Local
public interface CustomerSessionBeanLocal {

    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException;

    public Customer retrieveCustomerByCustomerId(Long customerId, boolean loadReservations);

    public Long createNewCustomer(Customer newCustomer) throws CustomerExistsException, UnknownPersistenceException, InputDataValidationException;
    
}
