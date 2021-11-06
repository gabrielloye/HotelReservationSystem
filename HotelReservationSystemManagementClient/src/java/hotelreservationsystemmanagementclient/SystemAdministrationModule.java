package hotelreservationsystemmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import java.util.List;
import java.util.Scanner;
import util.embeddable.Name;
import util.enumeration.EmployeeAccessRight;
import util.enumeration.PartnerAccessRight;
import util.exception.EmployeeUsernameExistsException;
import util.exception.InputDataValidationException;
import util.exception.PartnerExistsException;
import util.exception.UnknownPersistenceException;


public class SystemAdministrationModule
{
    
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private PartnerSessionBeanRemote partnerSessionBeanRemote;

    public SystemAdministrationModule()
    {
    }
    
    public SystemAdministrationModule(EmployeeSessionBeanRemote employeeSessionBeanRemote, PartnerSessionBeanRemote partnerSessionBeanRemote)
    {
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
    }
    
    public void SystemAdministrationMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("\n*** HoRS Management Client :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("5: Logout\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    createEmployee();
                }
                else if(response == 2)
                {
                    viewAllEmployees();
                }
                else if(response == 3)
                {
                    createPartner();
                }
                else if(response == 4)
                {
                    viewAllPartners();
                }
                else if(response == 5)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 5)
            {
                break;
            }
        }
    }
    
    private void createEmployee()
    {
        Scanner scanner = new Scanner(System.in);
        
        String firstName;
        String lastName;
        String username;
        String password;
        EmployeeAccessRight accessRight;
        
        System.out.println("\n*** HoRS Management Client :: System Administration :: Employee Creation ***");
        System.out.print("Enter First Name> ");
        firstName = scanner.nextLine().trim();
        System.out.print("Enter Last Name> ");
        lastName = scanner.nextLine().trim();
        
        while(true)
        {
            System.out.println("Select Employee Access Right:");
            System.out.print("(1: System Administrator, 2: Operation Manager, 3: Sales Manager, 4: Guest Relation Officer)> ");
            Integer accessRightInt = scanner.nextInt();
            
            if(accessRightInt >= 1 && accessRightInt <= 4)
            {
                accessRight = EmployeeAccessRight.values()[accessRightInt-1];
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        scanner.nextLine();
        System.out.print("Enter Username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        password = scanner.nextLine().trim();
        
        Employee newEmployee = new Employee(new Name(firstName, lastName), accessRight, username, password);
        
        try
        {
            Long newEmployeeId = employeeSessionBeanRemote.createNewEmployee(newEmployee);
            System.out.println("New employee created successfully! Employee ID: " + newEmployeeId + "\n");
        }
        catch(EmployeeUsernameExistsException ex)
        {
            System.out.println("An error has occurred while creating the new employee!: The user name already exist\n");
        }
        catch(UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while creating the new employee!: " + ex.getMessage() + "\n");
        }
        catch(InputDataValidationException ex)
        {
            System.out.println(ex.getMessage() + "\n");
        }
    }
    
    private void viewAllEmployees()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS Management Client :: System Administration :: View All Employees ***\n");
        
        List<Employee> employees = employeeSessionBeanRemote.retrieveAllEmployees();
        System.out.printf("%8s%20s%20s%20s%20s%20s\n", "Employee ID", "First Name", "Last Name", "Access Right", "Username", "Password");

        for(Employee employee : employees)
        {
            System.out.printf("%8s%20s%20s%20s%20s%20s\n", employee.getEmployeeId().toString(), employee.getName().getFirstName(), employee.getName().getLastName(), employee.getAccessRight().toString(), employee.getUsername(), employee.getPassword());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void createPartner()
    {
        Scanner scanner = new Scanner(System.in);
        
        String organisation;
        String password;
        PartnerAccessRight accessRight;
        
        System.out.println("\n*** HoRS Management Client :: System Administration :: Partner Creation ***");
        System.out.print("Enter Organisation> ");
        organisation = scanner.nextLine().trim();
        
        while(true)
        {
            System.out.println("Select Partner Access Right:");
            System.out.print("(1: Employee, 2: Reservation Manager)> ");
            Integer accessRightInt = scanner.nextInt();
            
            if(accessRightInt >= 1 && accessRightInt <= 2)
            {
                accessRight = PartnerAccessRight.values()[accessRightInt-1];
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        scanner.nextLine();
        System.out.print("Enter Password> ");
        password = scanner.nextLine().trim();
        
        Partner newPartner = new Partner(organisation, accessRight, "", password);
        
        try
        {
            Long newPartnerId = partnerSessionBeanRemote.createNewPartner(newPartner);
            System.out.println("New partner created successfully! Partner ID: " + newPartnerId + "\n");
        }
        catch(PartnerExistsException ex)
        {
            System.out.println("An error has occurred while creating the new partner!: The organisation already exist\n");
        }
        catch(UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while creating the new partner!: " + ex.getMessage() + "\n");
        }
        catch(InputDataValidationException ex)
        {
            System.out.println(ex.getMessage() + "\n");
        }
    }
    
    private void viewAllPartners()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("*** HoRS Management Client :: System Administration :: View All Partners ***\n");
        
        List<Partner> partners = partnerSessionBeanRemote.retrieveAllPartners();
        System.out.printf("%8s%20s%20s%20s\n", "Partner ID", "Organisation", "Access Right", "Password");

        for(Partner partner : partners)
        {
            System.out.printf("%8s%20s%20s%20s\n", partner.getPartnerId().toString(), partner.getOrganisation(), partner.getAccessRight(), partner.getPassword());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
}
