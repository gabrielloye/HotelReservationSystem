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
import util.embeddable.Name;
import util.enumeration.EmployeeAccessRight;

@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct()
    {
        if(em.find(Employee.class, 1l) == null)
        {
            Employee systemAdministrator = new Employee(new Name("Hsien Jie", "Loke"), EmployeeAccessRight.SYSTEMADMINISTRATOR, "systemadmin", "password");
            em.persist(systemAdministrator);
            Employee operationManager = new Employee(new Name("Gabriel", "Loye"), EmployeeAccessRight.OPERATIONMANAGER, "operationmanager", "password");
            em.persist(operationManager);
            Employee salesManager = new Employee(new Name("Sales", "Manager"), EmployeeAccessRight.SALESMANAGER, "salesmanager", "password");
            em.persist(salesManager);
        }
    }
    
}
