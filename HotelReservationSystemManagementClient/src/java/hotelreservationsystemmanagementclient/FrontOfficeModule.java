package hotelreservationsystemmanagementclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.AllocationExceptionReport;
import entity.Customer;
import entity.Employee;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import util.embeddable.Name;
import util.enumeration.RateType;
import util.exception.CheckedInException;
import util.exception.CheckedOutException;
import util.exception.CustomerExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.ReservationExistsException;
import util.exception.ReservationNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;


public class FrontOfficeModule
{

    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;  
    private RoomSessionBeanRemote roomSessionBeanRemote;
    
    private Employee loggedInEmployee; 
    
    public FrontOfficeModule()
    {
    }

    public FrontOfficeModule(Employee currentEmployee, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, CustomerSessionBeanRemote customerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote) {
        this.loggedInEmployee = currentEmployee;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
    }
    
    public void frontOfficeMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("\n*** HoRS Management Client :: Front Office ***\n");
            System.out.println("1: Walk-in Search Room");
            System.out.println("2: Check-in Guest");
            System.out.println("3: Check-out Guest");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    walkInSearchRoom();
                }
                else if(response == 2)
                {
                    checkInGuest();
                }
                else if(response == 3)
                {
                    checkOutGuest();
                }
                else if(response == 4)
                {
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
    
    private void walkInSearchRoom()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n*** HoRS Management Client :: Front Office Menu :: Walk-in Search Room\n");
        
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
                RoomRate pubRoomRate = getPublishedRoomRateFromList(roomType.getRoomRates());
                allRoomRates.add(pubRoomRate);
                System.out.println(counter + ". " + roomType.getName() + " - Amount : $" + pubRoomRate.getRatePerNight().multiply(numDays).multiply(new BigDecimal(numRooms)));
                counter++;
            }

            Integer response = 0;
            String anotherReservation = "N";
            Long customerId = new Long(0);
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
                    customerId = walkInReserveRoom(availableRoomTypes, allRoomRates, newReservation, customerId, numDays);
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
        } else {
            System.out.println("Sorry, no available rooms during the indicated period!\n");
        }
    }
    
    private Long walkInReserveRoom(List<RoomType> availableRoomTypes, List<RoomRate> allRoomRates, Reservation newReservation, Long customerId, BigDecimal numDays) {
        Scanner scanner = new Scanner(System.in);
        
        Integer selectedRoomTypeInt = 0;
        while (selectedRoomTypeInt < 1 || selectedRoomTypeInt > availableRoomTypes.size())
        {
            System.out.println("Which room would you like to reserve?\n");
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
        
        if(customerId.intValue() == 0) {
            customerId = checkExistingCustomer();
        }

        BigDecimal numRooms = new BigDecimal(newReservation.getNumRooms());
        newReservation.setPrice(selectedRoomRate.getRatePerNight().multiply(numDays).multiply(numRooms));

        try 
        {
            Long newReservationId = reservationSessionBeanRemote.createNewReservation(newReservation, selectedRoomType.getRoomTypeId(), customerId, new ArrayList<Long>());
            reservationSessionBeanRemote.associateEmployeeWithReservation(loggedInEmployee.getEmployeeId(), newReservationId);
            System.out.println("New Reservation created with ID: " + newReservationId + "\n");
            Date now = new Date();
            Date today = new Date(now.getYear(), now.getMonth(), now.getDate());
            if (newReservation.getStartDate().equals(today) && now.getHours() >= 2)
            {
                reservationSessionBeanRemote.allocateRoomsForReservationByReservationId(newReservationId);
                System.out.println("Room has been allocated\n");
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
        return customerId;
    }
    
    private Long checkExistingCustomer()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("Are you an existing Customer?");
        System.out.println("1: Yes");
        System.out.println("2: No\n");
        System.out.print("Select Option> ");
        response = scanner.nextInt();
        scanner.nextLine();

        while (true)
        {
            if(response == 1)
            {
                System.out.print("Enter Email> ");
                String email = scanner.nextLine().trim();
                try
                {
                    Customer customer = customerSessionBeanRemote.retrieveCustomerByEmail(email);
                    return customer.getCustomerId();
                } catch (CustomerNotFoundException ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
            else if(response == 2)
            {
                return createCustomer();
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
    }
    
    private Long createCustomer()
    {
        Scanner scanner = new Scanner(System.in);
        while(true)
        {
            Name name = new Name();
            String email;
            Long mobileNum;

            System.out.print("Enter Customer First Name> ");
            name.setFirstName(scanner.nextLine().trim());
            System.out.print("Enter Customer Last Name> ");
            name.setLastName(scanner.nextLine().trim());
            System.out.print("Enter Customer Email> ");
            email = scanner.nextLine().trim();
            System.out.print("Enter Customer Mobile Number> ");
            mobileNum = scanner.nextLong();
            scanner.nextLine();

            Customer newCustomer = new Customer(name, email, mobileNum);

            try
            {
                Long newCustomerId = customerSessionBeanRemote.createNewCustomer(newCustomer);
                System.out.println("New Customer created with ID: " + newCustomerId + "\n");
                return newCustomerId;
            }
            catch(CustomerExistsException ex)
            {
                System.out.println("An error has occurred while creating the new customer: The email or mobile number already exist!\n");
            }
            catch(UnknownPersistenceException ex)
            {
                System.out.println("An unknown error has occurred while creating the new customer!: " + ex.getMessage() + "\n");
            }
            catch(InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
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
    
    private RoomRate getPublishedRoomRateFromList(List<RoomRate> roomRates)
    {
        for (RoomRate roomRate : roomRates)
        {
            if (roomRate.getRateType() == RateType.PUBLISHED)
            {
                return roomRate;
            }
        }
        return null; //in this list there WILL be a RoomRate with PUBLISHED RateType
    }
    
    private void checkInGuest()
    {
        Scanner scanner = new Scanner(System.in);
              
        System.out.println("\n*** HoRS Management Client :: Front Office Menu :: Check In Guest");
        System.out.print("Enter Reservation ID> ");
        Long reservationId = Long.parseLong(scanner.nextLine().trim());
        
        try
        {
            Reservation reservation = reservationSessionBeanRemote.retrieveReservationByReservationId(reservationId, true, true);
            if(reservation != null && reservation.getStartDate().before(new Date()))
            {
                List<Room> reservationRooms = reservation.getRooms();
                List<AllocationExceptionReport> reservationAllocationReports = reservation.getAllocationExceptionReports();
                List<String> allocationReports = new ArrayList<>();
                for (AllocationExceptionReport report : reservationAllocationReports)
                {
                    allocationReports.add(report.getAllocationExceptionType().toString());
                }
                    
                if (!reservationRooms.isEmpty() && roomSessionBeanRemote.earlyCheckIn(reservationRooms))
                {
                    List<String> roomNumbers = new ArrayList<>();
                    for (Room room : reservationRooms)
                    {
                        roomNumbers.add(room.getRoomNumber());
                    }
                    
                    reservationSessionBeanRemote.checkInReservation(reservation.getReservationId());
                    System.out.println("Rooms allocated: " + String.join(", ", roomNumbers));
                    if (!allocationReports.isEmpty())
                    {
                        System.out.println("Allocation Exceptions: " + String.join(", ", allocationReports));
                    }
                    System.out.println(String.format("Customer successfully checked in for Reservation %s!", reservation.getReservationId()));
                }
                else 
                {
                    System.out.println("Sorry! No rooms were available to be allocated to this reservation!");
                    System.out.println("Allocation Exceptions: " + String.join(", ", allocationReports));
                }
            } 
            else if (reservation == null)
            {
                System.out.println("No such reservation found!");
            } 
            else if (!reservation.getStartDate().before(new Date()))
            {
                System.out.println("Reservation has not been allocated to rooms yet!");
            }
        }
        catch (CheckedInException | ReservationNotFoundException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    private void checkOutGuest()
    {
        Scanner scanner = new Scanner(System.in);
              
        System.out.println("\n*** HoRS Management Client :: Front Office Menu :: Check Out Guest");
        System.out.print("Enter Reservation ID> ");
        Long reservationId = Long.parseLong(scanner.nextLine().trim());
        
        try
        {
            Reservation reservation = reservationSessionBeanRemote.retrieveReservationByReservationId(reservationId);
            if(reservation != null && reservation.getCheckIn())
            {
                reservationSessionBeanRemote.checkOutReservation(reservation.getReservationId());
                System.out.println(String.format("Customer successfully checked out for Reservation %s!", reservation.getReservationId()));
            }
            else
            {
                System.out.println("Reservation does not exist or customer has not checked in!");
            }
        }
        catch (CheckedOutException | ReservationNotFoundException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}
