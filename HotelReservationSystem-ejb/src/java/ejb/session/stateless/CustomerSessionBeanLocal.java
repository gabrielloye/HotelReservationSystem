/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import javax.ejb.Local;
import util.exception.CustomerNotFoundException;

@Local
public interface CustomerSessionBeanLocal {

    public Customer retrieveCustomerByEmail(String email) throws CustomerNotFoundException;
    
}
