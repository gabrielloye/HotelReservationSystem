package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct()
    {
        if(em.find(Employee.class, 1l) == null)
        {
            // TODO: Add Employee details
            employeeSessionBeanLocal.createNewEmployee(new Employee());
        }
    }
    
}
