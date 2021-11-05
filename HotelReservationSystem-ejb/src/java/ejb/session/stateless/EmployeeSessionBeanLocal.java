/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Local;
import util.exception.EmployeeUsernameExistsException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author gabri
 */
@Local
public interface EmployeeSessionBeanLocal {

    public List<Employee> retrieveAllEmployees();

    public Long createNewEmployee(Employee newEmployee) throws EmployeeUsernameExistsException, UnknownPersistenceException, InputDataValidationException;

    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException;
    
}
