package hotelreservationsystemmanagementclient;

import entity.RoomRate;
import entity.RoomType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class FrontOfficeModule
{

    public FrontOfficeModule()
    {
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
        
        boolean incorrectFormat = true;
        while(incorrectFormat)
        {
            try {
                System.out.print("Enter Check-in Date (dd/MM/yyyy)> ");
                Date startDate = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine().trim());
                incorrectFormat = false;
            } catch (ParseException ex) 
            {
                System.out.println("Date is in wrong format! Please enter in dd/MM/yyyy!: " + ex.getMessage() + "\n");
            }
        }
        incorrectFormat = true;
        while(incorrectFormat)
        {
            try {
                System.out.print("Enter Check-out Date (dd/MM/yyyy)> ");
                Date endDate = new SimpleDateFormat("dd/MM/yyyy").parse(scanner.nextLine().trim());
                incorrectFormat = false;
            } catch (ParseException ex) 
            {
                System.out.println("Date is in wrong format! Please enter in dd/MM/yyyy!: " + ex.getMessage() + "\n");
            }
        }
        
//        List<RoomType> availableRoomTypes; 
//        
//        for(int i = 0; i < availableRoomTypes.size(); i++)
//        {
//            List<RoomRate> roomRates = availableRoomTypes.get(i).getRoomRates();
//            System.out.println((i+1) + ". " + availableRoomTypes.get(i).getName() + );
//        }
    }
    
    private void checkInGuest()
    {

    }
    
    private void checkOutGuest()
    {

    }
}
