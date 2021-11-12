/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationExceptionReport;
import java.util.List;
import javax.ejb.Remote;

@Remote
public interface AllocationExceptionReportSessionBeanRemote {
    
    public List<AllocationExceptionReport> retrieveAllAllocationExceptionReports();
    
}
