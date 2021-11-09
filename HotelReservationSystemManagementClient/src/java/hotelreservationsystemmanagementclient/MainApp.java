package hotelreservationsystemmanagementclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import ejb.session.stateless.TimerSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRight;
import util.exception.InvalidLoginCredentialException;

public class MainApp
{
    
    private SystemAdministrationModule systemAdministrationModule;
    private HotelOperationModule hotelOperationModule;
    private FrontOfficeModule frontOfficeModule;
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private TimerSessionBeanRemote timerSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    
    
    public MainApp(EmployeeSessionBeanRemote employeeSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, TimerSessionBeanRemote timerSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, CustomerSessionBeanRemote customerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote)
    {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.timerSessionBeanRemote = timerSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
    }
    
    public MainApp()
    {
    }
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        timerSessionBeanRemote.currentDayRoomAllocationTimer();
        
        while(true)
        {
            System.out.println("*** Welcome to the HoRS Management Client ** \n");
            System.out.println("1. Login");
            System.out.println("2. Exit\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    try
                    {
                        Employee currentEmployee = doLogin();
                        System.out.println("\nLogin Successful!");

                        mainMenu(currentEmployee);
                    }
                    catch(InvalidLoginCredentialException ex)
                    {
                        System.out.println("Invalid Login Credentials: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please choose one of the two options!\n");
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
        
    private Employee doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username;
        String password;

        System.out.println("\n*** Employee Login ***");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if(username.length() > 0 && password.length() > 0)
        {
            return employeeSessionBeanRemote.employeeLogin(username, password);
        }
        else
        {
            throw new InvalidLoginCredentialException("Invalid/Missing Login Credentials");
        }
    }
    
    private void mainMenu(Employee currentEmployee)
    {
        if(currentEmployee.getAccessRight() == EmployeeAccessRight.SYSTEMADMINISTRATOR)
        {
            systemAdministrationModule = new SystemAdministrationModule(employeeSessionBeanRemote, partnerSessionBeanRemote);
            systemAdministrationModule.SystemAdministrationMenu();
        }
        else if(currentEmployee.getAccessRight() == EmployeeAccessRight.OPERATIONMANAGER)
        {
            hotelOperationModule = new HotelOperationModule(roomTypeSessionBeanRemote, roomSessionBeanRemote);
            hotelOperationModule.operationManagerMenu();
        }
        else if(currentEmployee.getAccessRight() == EmployeeAccessRight.SALESMANAGER)
        {
            hotelOperationModule = new HotelOperationModule();
            hotelOperationModule.salesManagerMenu();
        }
        else if(currentEmployee.getAccessRight() == EmployeeAccessRight.GUESTRELATIONOFFICER)
        {
            frontOfficeModule = new FrontOfficeModule(roomTypeSessionBeanRemote, customerSessionBeanRemote, reservationSessionBeanRemote);
            frontOfficeModule.frontOfficeMenu();
        }
    }
}
