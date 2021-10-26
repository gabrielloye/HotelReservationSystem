package hotelreservationsystemmanagementclient;

import java.util.Scanner;


public class SystemAdministrationModule
{

    public SystemAdministrationModule()
    {
    }
    
    public void SystemAdministrationMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** HoRS Management Client :: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("5: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    // TODO: Create new Employee
                }
                else if(response == 2)
                {
                    // TODO: Display All Employees
                }
                else if(response == 3)
                {
                    // TODO: Create new Partner
                }
                else if(response == 4)
                {
                    // TODO: Display All Partners
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
}
