package hotelreservationsystemmanagementclient;

import entity.Employee;
import java.util.Scanner;
import util.enumeration.EmployeeAccessRight;
import util.exception.InvalidLoginCredentialException;

public class MainApp
{
    
    private SystemAdministrationModule systemAdministrationModule;
    private HotelOperationModule hotelOperationModule;
    private FrontOfficeModule frontOfficeModule;
            
    public MainApp()
    {
    }
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
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
                        System.out.println("Login Successful!\n");

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

        System.out.println("*** Employee Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if(username.length() > 0 && password.length() > 0)
        {
            // TODO: Call employee session bean .login() method and set to currentEmployee
            return new Employee();
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
            // TODO: Pass beans in Module
            systemAdministrationModule = new SystemAdministrationModule();
            systemAdministrationModule.SystemAdministrationMenu();
        }
        else if(currentEmployee.getAccessRight() == EmployeeAccessRight.OPERATIONMANAGER)
        {
            hotelOperationModule = new HotelOperationModule();
            hotelOperationModule.operationManagerMenu();
        }
        else if(currentEmployee.getAccessRight() == EmployeeAccessRight.SALESMANAGER)
        {
            hotelOperationModule = new HotelOperationModule();
            hotelOperationModule.salesManagerMenu();
        }
        else if(currentEmployee.getAccessRight() == EmployeeAccessRight.GUESTRELATIONOFFICER)
        {
            frontOfficeModule = new FrontOfficeModule();
            frontOfficeModule.frontOfficeMenu();
        }
    }
}