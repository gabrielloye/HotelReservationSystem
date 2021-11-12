
package hotelreservationsystemreservationclient;

import entity.Guest;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;

public class MainApp {
    
    private Guest loggedInGuest;

    public MainApp() {
    }
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to the HoRS Reservation Client ** \n");
            System.out.println("1. Guest Login");
            System.out.println("2. Register as Guest");
            System.out.println("3. Exit\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    try
                    {
                        this.loggedInGuest = doLogin();
                        System.out.println("\nLogin Successful!");
                        
                        reservationClientMenu();
                    }
                    catch(InvalidLoginCredentialException ex)
                    {
                        System.out.println("Invalid Login Credentials: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 2)
                {
                    registerAsGuest();
                }
                else if(response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please choose one of the three options!\n");
                }
            }
            
            if(response == 2)
            {
                break;
            }
        }
    }
    
    private Guest doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username;
        String password;

        System.out.println("\n*** Guest Login ***");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();

        if(username.length() > 0 && password.length() > 0)
        {
            return null; //stub
            //return guestSessionBeanRemote.guestLogin(username, password);
        }
        else
        {
            throw new InvalidLoginCredentialException("Invalid/Missing Login Credentials");
        }
    }
    
    private void registerAsGuest()
    {
        
    }
    
    private void reservationClientMenu()
    {
        
    }
}
