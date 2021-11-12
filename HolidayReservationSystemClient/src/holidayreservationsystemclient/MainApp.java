package holidayreservationsystemclient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import ws.client.InputDataValidationException_Exception;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.Partner;
import ws.client.PartnerWebService;
import ws.client.PartnerWebService_Service;
import ws.client.RateType;
import ws.client.Reservation;
import ws.client.ReservationNotFoundException_Exception;
import ws.client.RoomRate;
import ws.client.RoomType;
import ws.client.UnknownPersistenceException_Exception;

public class MainApp
{
    
    private String currentOrganisation;
    private String currentPassword;
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to the Holiday Reservation System Client ** \n");
            System.out.println("1. Partner Login");
            System.out.println("2. Search Rooms");
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
                        Partner currentPartner = doLogin();
                        System.out.println("\nLogin Successful!");
                        currentOrganisation = currentPartner.getOrganisation();
                        currentPassword = currentPartner.getPassword();
                        
                        managerMenu(currentPartner);
                    }
                    catch(InvalidLoginCredentialException_Exception ex)
                    {
                        System.out.println("Invalid Login Credentials: " + ex.getMessage() +"\n");
                    }
                }
                else if(response == 2)
                {
                    searchRoom();
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
    
    private Partner doLogin() throws InvalidLoginCredentialException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        String organisation;
        String password;

        System.out.println("\n*** Partner Login ***");
        System.out.print("Enter Organisation> ");
        organisation = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        password = scanner.nextLine().trim();

        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        return port.partnerLogin(organisation, password);
    }
    
    private void searchRoom()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n*** Holiday Reservation System Client :: Search Rooms ***\n");
    
        DatatypeFactory factory;
        try
        {
            factory = DatatypeFactory.newInstance();
        }
        catch(DatatypeConfigurationException ex)
        {
            System.out.println("\nAn unknown error has occured: " + ex.getMessage());
            return;
        }
        
        Date startDate = enterDate("Enter Check-in Date (dd/MM/yyyy)> ");
        Date endDate = enterDate("Enter Check-out Date (dd/MM/yyyy)> ");
        while(startDate.compareTo(endDate) >= 0)
        {
            System.out.println("Invalid Start/End Date Range: Start date must be before end date");
            startDate = enterDate("Enter Check-in Date (dd/MM/yyyy)> ");
            endDate = enterDate("Enter Check-out Date (dd/MM/yyyy)> ");
        }
        
        System.out.print("Enter Number of Rooms Required> ");
        Integer numRooms = scanner.nextInt();
        scanner.nextLine();
        
        long timeDiff = endDate.getTime() - startDate.getTime();
        BigDecimal numDays = new BigDecimal((timeDiff / (1000 * 60 * 60 * 24)));
        
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        
        GregorianCalendar startDateCalendar = new GregorianCalendar();
        startDateCalendar.setTime(startDate);
        GregorianCalendar endDateCalendar = new GregorianCalendar();
        endDateCalendar.setTime(endDate);
        
        List<RoomType> availableRoomTypes = port.retrieveAvailableRoomTypes(factory.newXMLGregorianCalendar(startDateCalendar), numRooms);
        
        
        if(!availableRoomTypes.isEmpty())
        {
            Integer counter = 1;
            List<List<RoomRate>> allRoomRates = new ArrayList<>();
            List<BigDecimal> allPrices = new ArrayList<>();
            for(RoomType roomType : availableRoomTypes)
            {
                List<RoomRate> chosenRoomRates = getPriorityRoomRates(roomType.getRoomRates(), startDate, numDays.setScale(0, RoundingMode.HALF_UP).intValue());
                allRoomRates.add(chosenRoomRates);
                BigDecimal price = new BigDecimal(0);
                for(RoomRate roomRate : chosenRoomRates)
                {
                    price = price.add(roomRate.getRatePerNight().multiply(new BigDecimal(numRooms)));
                }
                allPrices.add(price);
                System.out.println(counter + ". " + roomType.getName() + " - Amount : $" + price);
                counter++;
            }
            
            Integer response = 0;
            while(response < 1 || response > 2)
            {
                System.out.println("\nWould you like to reserve a room from this list?");
                System.out.println("1: Yes");
                System.out.println("2: No");
                System.out.print("> ");
                
                response = scanner.nextInt();
                scanner.nextLine();
                if(response == 1)
                {
                    // Check if customer is logged in
                    if(currentOrganisation == null || currentPassword == null)
                    {
                        System.out.println("Please login/re-login to complete this action!");
                        return;
                    }
                    else
                    {
                        Integer selectedRoomType = 0;
                        while(selectedRoomType < 1 || selectedRoomType > availableRoomTypes.size())
                        {
                            System.out.println("Which room would you like to reserve?");
                            System.out.print("Select Option> ");
                            selectedRoomType = scanner.nextInt();
                            scanner.nextLine();
                            
                            if(selectedRoomType < 1 || selectedRoomType > availableRoomTypes.size())
                            {
                                System.out.println("Invalid option, please try again!\n");
                            }
                        }

                        try
                        {
                            Reservation newReservation = new Reservation();
                            GregorianCalendar currentDateCalendar = new GregorianCalendar();
                            currentDateCalendar.setTime(new Date());
                            newReservation.setReservationDate(factory.newXMLGregorianCalendar(currentDateCalendar));
                            newReservation.setStartDate(factory.newXMLGregorianCalendar(startDateCalendar));
                            newReservation.setEndDate(factory.newXMLGregorianCalendar(endDateCalendar));
                            newReservation.setNumRooms(numRooms);
                            
                            reserveRoom(newReservation, availableRoomTypes.get(selectedRoomType - 1), allRoomRates.get(selectedRoomType - 1), allPrices.get(selectedRoomType - 1), numDays);
                        }
                        catch(InvalidLoginCredentialException_Exception ex)
                        {
                            System.out.println("Login Credentials not valid, Please logout and retry");
                        }
                    }
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
    
    private List<RoomRate> getPriorityRoomRates(List<RoomRate> roomRates, Date startDate, Integer numDays)
    {
        Map<RateType, Integer> ratePriorityMap = new HashMap<>();
        ratePriorityMap.put(RateType.PROMOTION, 1);
        ratePriorityMap.put(RateType.PEAK, 2);
        ratePriorityMap.put(RateType.NORMAL, 3);
        
        List<RoomRate> finalRoomRates = new ArrayList<>();
        for(int i = 0; i < numDays; i++)
        {
            Date rangeStart = new Date(startDate.getTime() + i * (1000 * 60 * 60 * 24));
            Date rangeEnd = new Date(startDate.getTime() + (i + 1) * (1000 * 60 * 60 * 24));
            List<RoomRate> validRoomRates = new ArrayList<>();
            for(RoomRate roomRate : roomRates)
            {
                if(roomRate.getRateType().equals(RateType.NORMAL))
                {
                    validRoomRates.add(roomRate);
                }
                else if(!roomRate.getRateType().equals(RateType.PUBLISHED) &&
                        roomRate.getValidityEndDate().toGregorianCalendar().getTime().compareTo(rangeStart) >= 0 && // Validity end date needs to be greater or equal to range start date
                        roomRate.getValidityStartDate().toGregorianCalendar().getTime().compareTo(rangeEnd) < 0) // Validity start date needs to be before range end
                {
                    validRoomRates.add(roomRate);
                }
            }
            Collections.sort(validRoomRates, (x, y) -> ratePriorityMap.get(x.getRateType()) - ratePriorityMap.get(y.getRateType()));
            finalRoomRates.add(validRoomRates.get(0));// Assume there is at least 1 valid room rate
        }
        return finalRoomRates;
    }
    
    private void reserveRoom(Reservation newReservation, RoomType selectedRoomType, List<RoomRate> selectedRoomRates, BigDecimal price, BigDecimal numDays)
            throws InvalidLoginCredentialException_Exception
    {   
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        
        newReservation.setPrice(price);
        List<Long> roomRateIds = new ArrayList<>();
        for(RoomRate roomRate : selectedRoomRates)
        {
            roomRateIds.add(roomRate.getRoomRateId());
        }

        
        try
        {
            Long newReservationId = port.createNewPartnerReservation(currentOrganisation, currentPassword, newReservation, selectedRoomType.getRoomTypeId(), roomRateIds);
            System.out.println("\nNew Reservation created with ID: " + newReservationId);
            Date now = new Date();
            Date today = new Date(now.getYear(), now.getMonth(), now.getDate());
            if (newReservation.getStartDate().equals(today) && now.getHours() >= 2)
            {
                port.allocateRoomsForReservationByReservationId(currentOrganisation, currentPassword, newReservationId);
                System.out.println("\nRoom has been allocated");
            }
        }
        catch(UnknownPersistenceException_Exception ex)
        {
            System.out.println("An unknown error has occurred while creating the new reservation!: " + ex.getMessage() + "\n");
        }
        catch(InputDataValidationException_Exception ex)
        {
            System.out.println(ex.getMessage() + "\n");
        }
    }
    
    private void managerMenu(Partner currentPartner)
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("\n*** Holiday Reservation System Client :: Reservation Manager Menu ***\n");
            System.out.println("1. Search Rooms");
            System.out.println("2. View Reservation Details");
            System.out.println("3. View All Reservations");
            System.out.println("4. Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    searchRoom();
                }
                else if (response == 2)
                {
                    try
                    {
                        viewReservationDetails();
                    }
                    catch(InvalidLoginCredentialException_Exception ex)
                    {
                        System.out.println("Login Credentials not valid, Please logout and retry");
                    }
                    catch(ReservationNotFoundException_Exception ex)
                    {
                        System.out.println(ex.getMessage());
                    }
                }
                else if(response == 3)
                {
                    try
                    {
                        viewAllReservations();
                    }
                    catch(InvalidLoginCredentialException_Exception ex)
                    {
                        System.out.println("Login Credentials not valid, Please logout and retry");
                    }
                }
                else if(response == 4)
                {
                    currentOrganisation = null;
                    currentPassword = null;
                    break;
                }
                else{
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if(response == 4)
            {
                break;
            }
        }
    }
    
    private void viewReservationDetails() throws InvalidLoginCredentialException_Exception, ReservationNotFoundException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** Holiday Reservation System Client :: Reservation Manager Menu :: View Reservation Details ***\n");
    
        System.out.print("Enter Reservation ID> ");
        Long reservationId = scanner.nextLong();
        scanner.nextLine();
        
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        Reservation reservation = port.viewPartnerReservationDetails(currentOrganisation, currentPassword, reservationId);
        
        System.out.println("ID: " + reservation.getReservationId());
        System.out.println("Reservation Date: " + reservation.getReservationDate());
        System.out.println("Room Type: " + reservation.getRoomType().getName());
        System.out.println("Start Date: " + formatDate(reservation.getStartDate().toGregorianCalendar()));
        System.out.println("End Date: " + formatDate(reservation.getEndDate().toGregorianCalendar()));
        System.out.println("No. of Rooms: " + reservation.getNumRooms());
        System.out.println("Price: $" + reservation.getPrice());
        System.out.println("Checked In: " + (reservation.isCheckIn() ? "True" : "False"));
        System.out.println("Checked Out: " + (reservation.isCheckOut() ? "True" : "False"));
        System.out.println("--------------------");
        System.out.print("Press Enter to continue...> ");
        scanner.nextLine();
    }
    
    private void viewAllReservations() throws InvalidLoginCredentialException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** Holiday Reservation System Client :: Reservation Manager Menu :: View All Reservations ***\n");
        
        PartnerWebService_Service service = new PartnerWebService_Service();
        PartnerWebService port = service.getPartnerWebServicePort();
        List<Reservation> reservations = port.viewAllPartnerReservations(currentOrganisation, currentPassword);
        System.out.printf("%-8s%-30s%-20s%-20s%-20s%-15s%-10s%-15s%-15s\n", "ID", "Reservation Date", "Room Type", "Start Date", "End Date", "No. of Rooms", "Price", "Checked-in", "Checked-out");
    
        for(Reservation reservation : reservations)
        {
            System.out.printf("%-8s%-30s%-20s%-20s%-20s%-15s%-10s%-15s%-15s\n", reservation.getReservationId(), reservation.getReservationDate(), reservation.getRoomType().getName(),
                    formatDate(reservation.getStartDate().toGregorianCalendar()), formatDate(reservation.getEndDate().toGregorianCalendar()), reservation.getNumRooms(), "$" + reservation.getPrice(),
                    reservation.isCheckIn() ? "True" : "False", reservation.isCheckOut() ? "True" : "False");
        }
        System.out.println("--------------------");
        System.out.print("Press Enter to continue...> ");
        scanner.nextLine();
    }
    
    private String formatDate(GregorianCalendar dateObj)
    {
        if(dateObj == null)
        {
            return "N/A";
        }
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setCalendar(dateObj);
        return dateFormat.format(dateObj.getTime());
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
}
