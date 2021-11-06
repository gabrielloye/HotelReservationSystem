package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Remote;
import util.exception.EmployeeUsernameExistsException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

@Remote
public interface EmployeeSessionBeanRemote {
    
    public List<Employee> retrieveAllEmployees();
    
    public Long createNewEmployee(Employee newEmployee) throws EmployeeUsernameExistsException, UnknownPersistenceException, InputDataValidationException;
    
    public Employee employeeLogin(String username, String password) throws InvalidLoginCredentialException;
    
}
