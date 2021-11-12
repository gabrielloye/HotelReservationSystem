
package hotelreservationsystemreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import entity.Guest;
import java.util.Scanner;
import util.embeddable.Name;
import util.exception.GuestExistsException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

public class MainApp {
    
    private Guest loggedInGuest;
    private GuestSessionBeanRemote guestSessionBeanRemote;

    public MainApp() {
    }

    public MainApp(GuestSessionBeanRemote guestSessionBeanRemote) {
        this.guestSessionBeanRemote = guestSessionBeanRemote;
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
            
            if(response == 3)
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
            return guestSessionBeanRemote.guestLogin(username, password);
        }
        else
        {
            throw new InvalidLoginCredentialException("Invalid/Missing Login Credentials");
        }
    }
    
    private Long registerAsGuest()
    {
        Scanner scanner = new Scanner(System.in);
        while(true)
        {
            String username;
            String password;
            Name name = new Name();
            String email;
            Long mobileNum;

            System.out.print("Enter A Username> ");
            username = scanner.nextLine().trim();
            System.out.print("Enter A Password> ");
            password = scanner.nextLine().trim();
            System.out.print("Enter Your First Name> ");
            name.setFirstName(scanner.nextLine().trim());
            System.out.print("Enter Your Last Name> ");
            name.setLastName(scanner.nextLine().trim());
            System.out.print("Enter Your Email> ");
            email = scanner.nextLine().trim();
            System.out.print("Enter Your Mobile Number> ");
            mobileNum = scanner.nextLong();
            scanner.nextLine();
            

            Guest newGuest = new Guest(username, password, name, email, mobileNum);

            try
            {
                System.out.println(guestSessionBeanRemote);
                Long newCustomerId = guestSessionBeanRemote.createNewGuest(newGuest);
                System.out.println("New Guest created with ID: " + newCustomerId);
                return newCustomerId;
            }
            catch(GuestExistsException ex)
            {
                System.out.println("An error has occurred while creating the new Guest: The user name or email or mobile number already exist\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new Guest!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
    }
    
    private void reservationClientMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("\n*** HoRS Reservation Client ***\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2: View All My Reservations");
            System.out.println("3: Logout\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    searchHotelRoom();
                }
                else if(response == 2)
                {
                    viewAllMyReservations();
                }
                else if(response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 3)
            {
                break;
            }
        }
    }
    
    private void searchHotelRoom()
    {
        
    }
    
    private void viewAllMyReservations()
    {
        
    }
}
