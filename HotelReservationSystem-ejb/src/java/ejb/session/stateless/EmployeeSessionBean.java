package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public List<Employee> retrieveAllEmployees()
    {
        Query query = em.createQuery("SELECT e FROM Employee e");
        
        return query.getResultList();
    }
    
    @Override
    public Long createNewEmployee(Employee newEmployee)
    {
        em.persist(newEmployee);
        em.flush();
        
        return newEmployee.getEmployeeId();
    }
}
