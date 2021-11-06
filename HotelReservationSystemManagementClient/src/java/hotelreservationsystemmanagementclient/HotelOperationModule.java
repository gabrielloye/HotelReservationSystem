package hotelreservationsystemmanagementclient;

import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.RoomType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import util.enumeration.Bed;
import util.exception.InputDataValidationException;
import util.exception.RoomTypeExistsException;
import util.exception.UnknownPersistenceException;


public class HotelOperationModule
{
    
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;

    public HotelOperationModule()
    {
    }
    
    public HotelOperationModule(RoomTypeSessionBeanRemote roomTypeSessionBeanRemote)
    {
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
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
                    viewRoomTypeDetails();
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
        List<Bed> beds = new ArrayList<>();
        int capacity;
        List<String> amenities = new ArrayList<>();
        int rank = 0;
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: Room Type Creation");
        System.out.print("Enter Room Type Name> ");
        name = scanner.nextLine().trim();
        System.out.print("Enter Room Type Description> ");
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
                Bed bedToAdd = Bed.values()[bedTypeInt-1];
                beds.add(bedToAdd);
                System.out.println("\nAdded: " + bedToAdd.toString());
                System.out.println("Beds in Room Type:");
                for(int i = 0; i < beds.size(); i++)
                {
                    System.out.println((i + 1) + ": " + beds.get(i).toString());
                }
                
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
                    System.out.println((i + 1) + ": " + amenities.get(i));
                }
                System.out.println("\nWould you like to add more Amenities?");
            }
            System.out.println("1: Yes");
            System.out.println("2: No");
            System.out.print("Select Option> ");
            response = scanner.nextInt();
            scanner.nextLine();
            
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
        
        List<RoomType> existingRoomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypes();
        System.out.println("\n** Select Rank of new Room Type **");
        while(rank < 1 || rank > existingRoomTypes.size() + 1)
        {
            System.out.println("Existing Room Types:");
            for(RoomType roomType : existingRoomTypes)
            {
                System.out.println(roomType.getRoomTypeRank() + ". " + roomType.getName());
            }
            System.out.println("(Note: Choosing an existing rank will push the other ranks up)");
            System.out.print("Input rank of new Room Type (1 being the lowest)> ");
            rank = scanner.nextInt();
            scanner.nextLine();
            if(rank < 1 || rank > existingRoomTypes.size() + 1)
            {
                System.out.println("Invalid rank option, please try again!\n");
            }
        }

        RoomType newRoomType = new RoomType(name, description, size, beds, capacity, amenities, false, rank);
        try
        {
            Long newRoomTypeId = roomTypeSessionBeanRemote.createNewRoomType(newRoomType);
            System.out.println("New Room Type created with ID: " + newRoomTypeId);
        }
        catch(RoomTypeExistsException ex)
        {
            System.out.println("An error has occurred while creating the new room type!: The room type with name: " + newRoomType.getName()  + " already exist\n");
        }
        catch(UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while creating the new room type!: " + ex.getMessage() + "\n");
        }
        catch(InputDataValidationException ex)
        {
            System.out.println(ex.getMessage() + "\n");
        }
    }
    
    private void viewRoomTypeDetails()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: View Room Type Details\n");
        List<RoomType> allRoomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypes();
    
        int selectedRoomTypeInt = 0;
        while(selectedRoomTypeInt < 1 || selectedRoomTypeInt > allRoomTypes.size())
        {
            System.out.println("All Room Types:");
            for(int i = 0; i < allRoomTypes.size(); i++)
            {
                System.out.println((i+1) + ". " + allRoomTypes.get(i).getName());
            }
            System.out.print("Select Room Type> ");
            selectedRoomTypeInt = scanner.nextInt();
            scanner.nextLine();
            if(selectedRoomTypeInt < 1 || selectedRoomTypeInt > allRoomTypes.size())
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }

        RoomType selectedRoomType = allRoomTypes.get(selectedRoomTypeInt-1);
        System.out.println("\nRoom Type Name: " + selectedRoomType.getName());
        System.out.println("Description: " + selectedRoomType.getDescription());
        System.out.println("Size: " + selectedRoomType.getSize());
        System.out.println("Capacity: " + selectedRoomType.getCapacity());
        System.out.println("Beds: " + getBedListString(selectedRoomType.getBeds()));
        System.out.println("Amenities: " + String.join(", ", selectedRoomType.getAmenities()));
        System.out.println("Room Type Rank: " + selectedRoomType.getRoomTypeRank());
        System.out.println("Disabled: " + (selectedRoomType.getDisabled() ? "True" : "False"));
        System.out.println("Press any key to continue...");
        scanner.nextLine();
    }
    
    private String getBedListString(List<Bed> beds)
    {
        Map<Bed, Integer> bedMap = new HashMap<>();
        for(Bed bed : beds)
        {
            if(bedMap.containsKey(bed))
            {
                bedMap.put(bed, bedMap.get(bed)+1);
            }
            else
            {
                bedMap.put(bed, 1);
            }
        }
        List<String> outputStringList = new ArrayList<>();
        for(Map.Entry<Bed,Integer> entry : bedMap.entrySet())
        {
            outputStringList.add(entry.getKey().toString() + ": " + entry.getValue().toString());
        }
        return String.join(", ", outputStringList);
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
