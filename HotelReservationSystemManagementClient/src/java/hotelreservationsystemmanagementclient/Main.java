package hotelreservationsystemmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import ejb.session.stateless.TimerSessionBeanRemote;
import javax.ejb.EJB;

public class Main {

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;
    @EJB
    private static PartnerSessionBeanRemote partnerSessionBeanRemote;
    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    @EJB
    private static TimerSessionBeanRemote timerSessionBeanRemote;
    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    
     
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(employeeSessionBeanRemote, partnerSessionBeanRemote, roomTypeSessionBeanRemote, timerSessionBeanRemote, roomRateSessionBeanRemote);
        mainApp.runApp();
    }
    
}
