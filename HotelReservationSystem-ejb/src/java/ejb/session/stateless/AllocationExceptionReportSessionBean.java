/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationExceptionReport;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class AllocationExceptionReportSessionBean implements AllocationExceptionReportSessionBeanRemote, AllocationExceptionReportSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public AllocationExceptionReportSessionBean() {
    }
    
    @Override
    public List<AllocationExceptionReport> retrieveAllAllocationExceptionReports()
    {
        Query query = em.createQuery("SELECT aer FROM AllocationExceptionReport aer");
      
        return query.getResultList();
    }
}
