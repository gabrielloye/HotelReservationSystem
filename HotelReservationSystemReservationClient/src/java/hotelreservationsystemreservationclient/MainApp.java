
package hotelreservationsystemreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Guest;
import entity.Reservation;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import util.embeddable.Name;
import util.enumeration.RateType;
import util.exception.GuestExistsException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

public class MainApp {
    
    private Guest loggedInGuest;
    private GuestSessionBeanRemote guestSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;

    public MainApp() {
    }

    public MainApp(GuestSessionBeanRemote guestSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote) {
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
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
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout\n");
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
                    viewMyReservationDetails();
                }
                else if(response == 3)
                {
                    viewAllMyReservations();
                }
                else if(response == 4)
                {
                    this.loggedInGuest = null;
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 4)
            {
                break;
            }
        }
    }
    
    private void searchHotelRoom()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n*** HoRS Reservation Client :: Search Rooms ***\n");
        
        Date startDate = enterDate("Enter Check-in Date (dd/MM/yyyy)> ");
        Date endDate = enterDate("Enter Check-out Date (dd/MM/yyyy)> ");
        while(startDate.compareTo(endDate) >= 0)
        {
            System.out.println("Invalid Start/End Date Range: Start date must be before end date");
            startDate = enterDate("Enter Check-in Date (dd/MM/yyyy)> ");
            endDate = enterDate("Enter Check-out Date (dd/MM/yyyy)> ");
        }
        
        long timeDiff = endDate.getTime() - startDate.getTime();
        BigDecimal numDays = new BigDecimal((timeDiff / (1000 * 60 * 60 * 24)));
        
        System.out.print("Enter Number of Rooms Required> ");
        Integer numRooms = scanner.nextInt();
        scanner.nextLine();
        
        List<RoomType> availableRoomTypes = roomTypeSessionBeanRemote.retrieveAvailableRoomTypes(startDate, numRooms);
        if (!availableRoomTypes.isEmpty())
        {
            Integer counter = 1;
            List<RoomRate> allRoomRates = new ArrayList<>();
            for(RoomType roomType : availableRoomTypes)
            {
                RoomRate chosenRoomRate = getPriorityRoomRate(roomType.getRoomRates());
                allRoomRates.add(chosenRoomRate);
                System.out.println(counter + ". " + roomType.getName() + " - Amount : $" + chosenRoomRate.getRatePerNight().multiply(numDays).multiply(new BigDecimal(numRooms)));
                counter++;
            }
            
            Integer response = 0;
            String anotherReservation = "N";
            while(response < 1 || response > 2 || anotherReservation.equals("Y")) 
            {
                if (anotherReservation.equals("Y"))
                {
                    counter = 1;
                    for(RoomType roomType : availableRoomTypes)
                    {
                        System.out.println(counter + ". " + roomType.getName() + " - Amount : $" + allRoomRates.get(counter - 1).getRatePerNight().multiply(numDays).multiply(new BigDecimal(numRooms)));
                        counter++;
                    }
                }
                System.out.println("Would you like to reserve a room from this list?");
                System.out.println("1: Yes");
                System.out.println("2: No");
                System.out.print("> ");

                response = scanner.nextInt();
                scanner.nextLine();
                if(response == 1)
                {
                    Reservation newReservation = new Reservation(new Date(), startDate, endDate, numRooms, BigDecimal.valueOf(0.0), false, false);
                    reserveHotelRoom(availableRoomTypes, allRoomRates, newReservation, loggedInGuest.getCustomerId(), numDays);
                    System.out.print("Would you like to reserve another room? Enter 'Y' if yes> ");
                    anotherReservation = scanner.nextLine().trim();
                }
                else if(response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
        }
        else
        {
            System.out.println("Sorry, no available rooms during the indicated period!");
        }
    }
    
    private void reserveHotelRoom(List<RoomType> availableRoomTypes, List<RoomRate> allRoomRates, Reservation newReservation, Long customerId, BigDecimal numDays) {
        Scanner scanner = new Scanner(System.in);
        
        Integer selectedRoomTypeInt = 0;
        while (selectedRoomTypeInt < 1 || selectedRoomTypeInt > availableRoomTypes.size())
        {
            System.out.println("Which room would you like to reserve?");
            System.out.print("Select Option> ");
            selectedRoomTypeInt = scanner.nextInt();
            scanner.nextLine();
            
            if(selectedRoomTypeInt < 1 || selectedRoomTypeInt > availableRoomTypes.size())
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        RoomType selectedRoomType = availableRoomTypes.get(selectedRoomTypeInt - 1);
        RoomRate selectedRoomRate = allRoomRates.get(selectedRoomTypeInt - 1);

        BigDecimal numRooms = new BigDecimal(newReservation.getNumRooms());
        newReservation.setPrice(selectedRoomRate.getRatePerNight().multiply(numDays).multiply(numRooms));

        try 
        {
            Long newReservationId = reservationSessionBeanRemote.createNewReservation(newReservation, selectedRoomType.getRoomTypeId(), customerId, selectedRoomRate.getRoomRateId());
            System.out.println("New Reservation created with ID: " + newReservationId);
            Date now = new Date();
            Date today = new Date(now.getYear(), now.getMonth(), now.getDate());
            if (newReservation.getStartDate().equals(today) && now.getHours() >= 2)
            {
                reservationSessionBeanRemote.allocateRoomsForReservationByReservationId(newReservationId);
                System.out.println("Room has been allocated");
            }
        }
        catch(UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while creating the new reservation!: " + ex.getMessage() + "\n");
        }
        catch(InputDataValidationException ex)
        {
            System.out.println(ex.getMessage() + "\n");
        }
    }
    
    private RoomRate getPriorityRoomRate(List<RoomRate> roomRates)
    {
        List<RoomRate> validRoomRates = new ArrayList<>();
        for(RoomRate roomRate : roomRates)
        {
            if(roomRate.getRateType().equals(RateType.NORMAL))
            {
                validRoomRates.add(roomRate);
            }
            // If validity end date is after current date and start date before current date
            else if(!roomRate.getRateType().equals(RateType.PUBLISHED) && // Published only for walk-in
                (roomRate.getValidityEndDate().after(new Date()) ||
                roomRate.getValidityStartDate().before(new Date())) )
            {
                validRoomRates.add(roomRate);
            }
        }

        Map<RateType, Integer> ratePriorityMap = new HashMap<>();
        ratePriorityMap.put(RateType.PROMOTION, 1);
        ratePriorityMap.put(RateType.PEAK, 2);
        ratePriorityMap.put(RateType.NORMAL, 3);

        Collections.sort(validRoomRates, (x, y) -> ratePriorityMap.get(x.getRateType()) - ratePriorityMap.get(y.getRateType()));
        return validRoomRates.get(0); // Assume there is at least 1 valid room rate
    }
    
    private Date enterDate(String dateMessage)
    {
        Scanner scanner = new Scanner(System.in);
        while(true)
        {
            try 
            {
                System.out.print(dateMessage);
                return new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine().trim());
            } catch (ParseException ex) 
            {
                System.out.println("Date is in wrong format! Please enter in dd/MM/yyyy!: " + ex.getMessage() + "\n");
            }
        }
    }
    
    private void viewMyReservationDetails()
    {
        Scanner scanner = new Scanner(System.in);
        Long reservationId;

        System.out.println("\n***  HoRS Reservation Client :: View My Reservation Details ***");
        System.out.print("Enter reservation ID> ");
        reservationId = scanner.nextLong();
        scanner.nextLine();
        
        List<Reservation> reservations = guestSessionBeanRemote.retrieveAllGuestReservations(this.loggedInGuest.getCustomerId());
        boolean myReservation = false;
        for (Reservation res : reservations)
        {
            if (reservationId.equals(res.getReservationId()))
            {
                myReservation = true;
                break;
            }
        }
        
        if (myReservation)
        {
            try {
                Reservation reservation = reservationSessionBeanRemote.retrieveReservationByReservationId(reservationId);
                System.out.println("ID: " + reservation.getReservationId());
                System.out.println("Reservation Date: " + formatDate(reservation.getReservationDate()));
                System.out.println("Room Type: " + reservation.getRoomType().getName());
                System.out.println("Start Date: " + formatDate(reservation.getStartDate()));
                System.out.println("End Date: " + formatDate(reservation.getEndDate()));
                System.out.println("No. of Rooms: " + reservation.getNumRooms());
                System.out.println("Price: $" + reservation.getPrice());
                System.out.println("Checked In: " + (reservation.getCheckIn() ? "True" : "False"));
                System.out.println("Checked Out: " + (reservation.getCheckOut() ? "True" : "False"));

                System.out.println("--------------------");
                System.out.print("Press Enter to continue...> ");
                scanner.nextLine();
            }
            catch (ReservationNotFoundException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        else
        {
            System.out.println("You do not have a reservation with ID " + reservationId);
        }
    }
    
    private void viewAllMyReservations()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n***  HoRS Reservation Client :: View All My Reservations ***");
        List<Reservation> reservations = guestSessionBeanRemote.retrieveAllGuestReservations(this.loggedInGuest.getCustomerId());
        System.out.printf("%-8s%-30s%-20s%-20s%-20s%-15s%-10s%-15s%-15s", "ID", "Reservation Date", "Room Type", "Start Date", "End Date", "No. of Rooms", "Price", "Checked-in", "Checked-out");
        System.out.println();
        
        for(Reservation reservation : reservations)
        {
            System.out.printf("%-8s%-30s%-20s%-20s%-20s%-15s%-10s%-15s%-15s", reservation.getReservationId(), reservation.getReservationDate(), reservation.getRoomType().getName(),
                formatDate(reservation.getStartDate()), formatDate(reservation.getEndDate()), reservation.getNumRooms(), "$" + reservation.getPrice(),
                reservation.getCheckIn() ? "True" : "False", reservation.getCheckOut() ? "True" : "False");
            System.out.println();
        }
        System.out.print("Press Enter to continue...> ");
        scanner.nextLine();
    }
    
    private String formatDate(Date dateObj)
    {
        if(dateObj == null)
        {
            return "N/A";
        }
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(dateObj);
    }
}
