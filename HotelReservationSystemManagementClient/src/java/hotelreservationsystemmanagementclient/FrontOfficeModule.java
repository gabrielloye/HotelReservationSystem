package hotelreservationsystemmanagementclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
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
import util.exception.RoomRateNotFoundException;
import util.exception.UnknownPersistenceException;


public class FrontOfficeModule
{

    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private ReservationSessionBeanRemote reservationSessionBeanRemote;  
    
    private Employee loggedInEmployee; 
    
    public FrontOfficeModule()
    {
    }

    public FrontOfficeModule(Employee currentEmployee, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, CustomerSessionBeanRemote customerSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote) {
        this.loggedInEmployee = currentEmployee;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
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
        
//arbituary dates initialised such that Start Date is after End Date
        Date startDate = new Date(121, 11, 10); 
        Date endDate = new Date(121, 11, 9);
        while(startDate.after(endDate))
        {
            startDate = enterDate("Enter Check-in Date (dd/MM/yyyy)> ");
            endDate = enterDate("Enter Check-out Date (dd/MM/yyyy)> ");
            if (startDate.after(endDate))
            {
                System.out.println("End Date must be after Start Date! Try Again!");
            }
        }
        
        List<RoomType> availableRoomTypes = roomTypeSessionBeanRemote.retrieveAvailableRoomTypes(startDate);
        List<BigDecimal> amountForRoomTypes = new ArrayList<>();
        
        for(int i = 0; i < availableRoomTypes.size(); i++)
        {
            List<RoomRate> roomRates = availableRoomTypes.get(i).getRoomRates();
            
            RoomRate pubRoomRate = getPublishedRoomRateFromList(roomRates);
            long timeDiff = endDate.getTime() - startDate.getTime();
            BigDecimal numDays = new BigDecimal((timeDiff / (1000 * 60 * 60 * 24)));
            amountForRoomTypes.add(pubRoomRate.getRatePerNight().multiply(numDays));
            System.out.println((i+1) + ". " + availableRoomTypes.get(i).getName() + " - Amount : $" + amountForRoomTypes.get(i).toString());
        }
        
        Integer response = 0;
        String anotherReservation = "N";
        while(response < 1 || response > 2 || anotherReservation.equals("Y")) 
        {
            System.out.println("Would you like to reserve a room from this list?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            System.out.print("> ");

            response = scanner.nextInt();
            scanner.nextLine();
            if(response == 1)
            {
                Reservation newReservation = new Reservation(new Date(), startDate, endDate, 0, BigDecimal.valueOf(0.0), false, false);
                walkInReserveRoom(availableRoomTypes, amountForRoomTypes, newReservation);
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
    
    private void walkInReserveRoom(List<RoomType> availableRoomTypes, List<BigDecimal> amountForRoomTypes, Reservation newReservation) {
        Scanner scanner = new Scanner(System.in);
        
        Integer selectedRoomType = 0;
        while (selectedRoomType < 1 || selectedRoomType > availableRoomTypes.size())
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
        
        int maxNumRooms = roomTypeSessionBeanRemote.getMaxNumRoomsForRoomType(availableRoomTypes.get(selectedRoomType - 1).getRoomTypeId());

        Integer response = 0;
        while (response < 1 || response > maxNumRooms)
        {
            System.out.println("How many rooms would you like to reserve?");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();

            if(response < 1)
            {
                System.out.println("Invalid option, please try again!\n");
            }

            if(response > maxNumRooms)
            {
                System.out.println("Do not have enough rooms! Please enter a number less than " + maxNumRooms + "!\n");
            }
        }

        Long customerId = checkExistingCustomer();

        BigDecimal numRooms = new BigDecimal(response);
        newReservation.setNumRooms(response);
        newReservation.setPrice(amountForRoomTypes.get(selectedRoomType - 1).multiply(numRooms));

        try {
            Long newReservationId = reservationSessionBeanRemote.createNewReservation(newReservation, availableRoomTypes.get(selectedRoomType - 1).getRoomTypeId(), customerId);
            reservationSessionBeanRemote.associateEmployeeWithReservation(loggedInEmployee.getEmployeeId(), newReservationId);
            System.out.println("New Reservation created with ID: " + newReservationId);
        }
        catch(ReservationExistsException ex) //will this happen? no unique fields in reservation
        {
            System.out.println("An error has occurred while creating the new reservation: It already exists!\n");
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
    
    private Long checkExistingCustomer()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        System.out.println("Are you an existing Customer?");
        System.out.println("1: Yes");
        System.out.println("2: No");
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
                System.out.println("New Customer created with ID: " + newCustomerId);
                return newCustomerId;
            }
            catch(CustomerExistsException ex)
            {
                System.out.println("An error has occurred while creating the new customer: Email or Mobile Number already exist!\n");
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
        
        String email;        
        System.out.println("\n*** HoRS Management Client :: Front Office Menu :: Check In Guest");
        System.out.print("Enter Customer Email> ");
        email = scanner.nextLine().trim();
        
        try
        {
            Customer customer = customerSessionBeanRemote.retrieveCustomerByEmail(email);
            Reservation latestReservation = getCustomerLatestReservation(customer);
            if(latestReservation != null && latestReservation.getStartDate().before(new Date()))
            {
                List<Room> customerRooms = reservationSessionBeanRemote.getRoomsForReservation(latestReservation.getReservationId());
                List<AllocationExceptionReport> customerAllocationReports = reservationSessionBeanRemote.getAllocationReportForReservation(latestReservation.getReservationId());
                List<String> allocationReports = new ArrayList<>();
                    for (AllocationExceptionReport report : customerAllocationReports)
                    {
                        allocationReports.add(report.getAllocationExceptionType().toString());
                    }
                    
                if (!customerRooms.isEmpty())
                {
                    reservationSessionBeanRemote.checkInReservation(latestReservation.getReservationId());

                    List<String> roomNumbers = new ArrayList<>();
                    for (Room room : customerRooms)
                    {
                        roomNumbers.add(room.getRoomNumber());
                    }
                    System.out.println("Rooms allocated: " + String.join(", ", roomNumbers));
                    System.out.println("Allocation Exceptions: " + String.join(", ", allocationReports));
                    System.out.println(String.format("Customer %s Successfully Checked In for Reservation %s!", customer.getName().toString(), latestReservation.getReservationId()));
                }
                else 
                {
                    System.out.println("Sorry! No rooms were available to be allocated to this reservation!");
                    System.out.println("Allocation Exceptions: " + String.join(", ", allocationReports));
                }
            }
                
        }
        catch (CustomerNotFoundException | CheckedInException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    
    private void checkOutGuest()
    {
        Scanner scanner = new Scanner(System.in);
        
        String email;        
        System.out.println("\n*** HoRS Management Client :: Front Office Menu :: Check Out Guest");
        System.out.print("Enter Customer Email> ");
        email = scanner.nextLine().trim();
        
        try
        {
            Customer customer = customerSessionBeanRemote.retrieveCustomerByEmail(email);
            Reservation latestReservation = getCustomerLatestReservation(customer);
            if(latestReservation != null && latestReservation.getCheckIn())
            {
                reservationSessionBeanRemote.checkOutReservation(latestReservation.getReservationId());
                System.out.println(String.format("Customer %s Successfully Checked Out for Reservation %s!", customer.getName().toString(), latestReservation.getReservationId()));
            }
            else
            {
                System.out.println("Customer has not checked in!");
            }
        }
        catch (CustomerNotFoundException | CheckedOutException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    private Reservation getCustomerLatestReservation(Customer customer)
    {        
        List<Reservation> reservations = customer.getReservations();
        if (reservations.isEmpty())
        {
            System.out.println("Customer does not have any reservations!");
            return null;
        }
        else
        {
            return reservations.get(reservations.size() - 1);
        }
}
}
