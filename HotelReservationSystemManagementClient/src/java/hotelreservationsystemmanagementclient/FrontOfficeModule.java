package hotelreservationsystemmanagementclient;

import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import util.enumeration.RateType;
import util.exception.RoomRateNotFoundException;


public class FrontOfficeModule
{

    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    
    public FrontOfficeModule()
    {
    }

    public FrontOfficeModule(RoomTypeSessionBeanRemote roomTypeSessionBeanRemote) {
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
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
        
        List<RoomType> availableRoomTypes = roomTypeSessionBeanRemote.retrieveAvailableRoomTypes(startDate);
        
        for(int i = 0; i < availableRoomTypes.size(); i++)
        {
            List<RoomRate> roomRates = availableRoomTypes.get(i).getRoomRates();
            
            RoomRate pubRoomRate = getPublishedRoomRateFromList(roomRates);
            long timeDiff = endDate.getTime() - startDate.getTime();
            BigDecimal numDays = new BigDecimal((timeDiff / (1000 * 60 * 60 * 24)));
            BigDecimal amount = pubRoomRate.getRatePerNight().multiply(numDays);
            System.out.println((i+1) + ". " + availableRoomTypes.get(i).getName() + " - Amount : $" + amount.toString());
        }
        
        Integer response = 0;
        while(response < 1 || response > 2) 
        {
            System.out.println("Would you like to reserve a room from this list?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            System.out.print("> ");

            response = scanner.nextInt();
            scanner.nextLine();
            if(response == 1)
            {
                walkInReserveRoom();
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
    
    private void walkInReserveRoom() {
        
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

    }
    
    private void checkOutGuest()
    {

    }
}
