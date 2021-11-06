package hotelreservationsystemmanagementclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class HotelOperationModule
{

    public HotelOperationModule()
    {
    }
    
    public void operationManagerMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("\n*** HoRS Management Client :: Operation Manager Menu ***\n");
            System.out.println("1: Room Type Operations");
            System.out.println("2: Room Operations");
            System.out.println("3: View Room Allocation Exception Report");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    roomTypeOperationsMenu();
                }
                else if(response == 2)
                {
                    roomOperationsMenu();
                }
                else if(response == 3)
                {
                    break;
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
    
    public void salesManagerMenu()
    {
        
    }
    
    private void roomTypeOperationsMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("\n*** HoRs Management Client :: Operation Manager Menu :: Room Type Operations ***");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details");
            System.out.println("3: Update Room Type");
            System.out.println("4: Delete Room Type");
            System.out.println("5: View All Room Types");
            System.out.println("6: Back\n");
            response = 0;
            
            while(response < 1 || response > 6)
            {
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    createRoomType();
                }
                else if(response == 2)
                {
                    break;
                }
                else if(response == 3)
                {
                    break;
                }
                else if(response == 4)
                {
                    break;
                }
                else if(response == 5)
                {
                    break;
                }
                else if(response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");                
                }
            }
            
            if(response == 6)
            {
                break;
            }
        }
    }
    
    private void createRoomType()
    {
        Scanner scanner = new Scanner(System.in);
        
        String name;
        String description;
        int size;
        // List<Bed> beds = new ArrayList<>();
        int capacity;
        List<String> amenities = new ArrayList<>();
        boolean disabled;
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: Room Type Creation");
        System.out.print("Enter Room Type Name> ");
        name = scanner.nextLine().trim();
        System.out.print("Enter Room Type Description");
        description = scanner.nextLine().trim();
        System.out.print("Enter Size of Room Type> ");
        size = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Capacity of Room Type (No. of pax)> ");
        capacity = scanner.nextInt();
        scanner.nextLine();
        
        Integer response = 1; // Have to add at least 1 bed
        while(response == 1)
        {
            System.out.println("Add Beds to Room Type:");
            System.out.print("(1: Queen, 2: King, 3: Single)> ");
            Integer bedTypeInt = scanner.nextInt();
            
            if(bedTypeInt >= 1 && bedTypeInt <= 3)
            {
                //Bed bedTooAdd = Bed.values()[bedTypeInt-1];
                //beds.add(bedTooAdd);
                //System.out.println("\nAdded: " + bedToAdd.toString());
                //System.out.println("Beds in Room Type:");
//                for(int i = 0; i < beds.size(); i++)
//                {
//                    System.out.println(i + ": " + beds.get(i).toString());
//                }
                
                response = 0;
                while(response < 1 || response > 2)
                {
                    System.out.println("\nWould you like to add more beds?");
                    System.out.println("1: Yes");
                    System.out.println("2: No");
                    System.out.print("Select Option> ");
                    response = scanner.nextInt();
                    scanner.nextLine();
                    if(response < 1 || response > 2)
                    {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        while(true)
        {
            if(amenities.size() == 0)
            {
                System.out.println("\nWould you like to add Amenities to the Room Type?");
            }
            else
            {
                System.out.println("Current Amenities for this Room Type:");
                for(int i = 0; i < amenities.size(); i++)
                {
                    System.out.println(i + ": " + amenities.get(i));
                }
                System.out.println("\nWould you like to add more Amenities?");
            }
            System.out.println("1: Yes");
            System.out.println("2: No");
            System.out.print("Select Option> ");
            if(response == 1)
            {
                System.out.print("\nAmenity to add> ");
                amenities.add(scanner.nextLine().trim());
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
        //RoomType newRoomType = new RoomType(name, description, size, beds, capacity, amenities, false);
    }
    
    private void roomOperationsMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("\n*** HoRs Management Client :: Operation Manager Menu :: Room Operations ***");
            System.out.println("1: Create New Room");
            System.out.println("2: Update Room");
            System.out.println("3: Delete Room");
            System.out.println("4: View All Rooms");
            System.out.println("5: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    break;
                }
                else if(response == 2)
                {
                    break;
                }
                else if(response == 3)
                {
                    break;
                }
                else if(response == 4)
                {
                    break;
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
