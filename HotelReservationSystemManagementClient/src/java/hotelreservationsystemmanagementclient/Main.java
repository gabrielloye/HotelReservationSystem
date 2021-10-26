package hotelreservationsystemmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import javax.ejb.EJB;

public class Main {

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;
    
    public static void main(String[] args) {
        MainApp mainApp = new MainApp();
        mainApp.runApp();
    }
    
}
