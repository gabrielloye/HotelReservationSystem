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
            System.out.println("3: View All Room Types");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
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
    
    private void createRoomType()
    {
        Scanner scanner = new Scanner(System.in);
        
        String name;
        String description;
        Integer size;
        Integer capacity;
        Long higherRoomTypeId = null;
        Long lowerRoomTypeId = null;
        
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
        
        List<Bed> beds = getBedList();
        List<String> amenities = getAmenitiesList();
        
        RoomType newRoomType = new RoomType(name, description, size, beds, capacity, amenities, false);
        
        List<RoomType> existingRoomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypesOrderedByRank();
        if(existingRoomTypes.size() > 0)
        {
            System.out.println("\n** Select Rank of new Room Type **");
            System.out.println("1. Before " + existingRoomTypes.get(0).getName());
            int i;
            for(i = 2; i <= existingRoomTypes.size(); i++)
            {
                System.out.println(i + ". Between " + existingRoomTypes.get(i-2).getName() + " & " + existingRoomTypes.get(i - 1).getName());
            }
            System.out.println(i + ". After " + existingRoomTypes.get(i-2).getName());
            
            Integer selectedOption = 0;
            
            while(selectedOption < 1 || selectedOption > i)
            {
                System.out.print("> ");
                selectedOption = scanner.nextInt();
                scanner.nextLine();
              
                if(selectedOption == 1)
                {
                    higherRoomTypeId = existingRoomTypes.get(0).getRoomTypeId();
                }
                else if(selectedOption > 1 && selectedOption < i)
                {
                    lowerRoomTypeId = existingRoomTypes.get(selectedOption - 2).getRoomTypeId();
                    higherRoomTypeId = existingRoomTypes.get(selectedOption - 1).getRoomTypeId();
                }
                else if(selectedOption == i)
                {
                    lowerRoomTypeId = existingRoomTypes.get(selectedOption - 2).getRoomTypeId();
                }
                else
                {
                    System.out.println("Invalid Option! Please Try Again!\n");
                }
            }
        }

        
        try
        {
            Long newRoomTypeId = roomTypeSessionBeanRemote.createNewRoomType(newRoomType, lowerRoomTypeId, higherRoomTypeId);
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
    
    private List<Bed> getBedList()
    {
        Scanner scanner = new Scanner(System.in);
        List<Bed> beds = new ArrayList<>();
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
        return beds;
    }
    
    private List<String> getAmenitiesList()
    {
        Scanner scanner = new Scanner(System.in);
        
        Integer response;
        List<String> amenities = new ArrayList<>();
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
        return amenities;
    }
    
    private void viewRoomTypeDetails()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: View Room Type Details\n");
        
        RoomType selectedRoomType = selectRoomType();
        System.out.println("\nRoom Type Name: " + selectedRoomType.getName());
        System.out.println("Description: " + selectedRoomType.getDescription());
        System.out.println("Size: " + selectedRoomType.getSize());
        System.out.println("Capacity: " + selectedRoomType.getCapacity());
        System.out.println("Beds: " + getBedListString(selectedRoomType.getBeds()));
        System.out.println("Amenities: " + String.join(", ", selectedRoomType.getAmenities()));
        System.out.println("Disabled: " + (selectedRoomType.getDisabled() ? "True" : "False"));
        
        System.out.println("--------------------");
        System.out.println("1: Update Room Type");
        System.out.println("2: Delete Room Type");
        System.out.println("3: Back\n");
        
        int response = 0;
        while(response < 1 || response > 3)
        {
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();
            if(response == 1)
            {
                updateRoomType(selectedRoomType);
            }
            else if(response == 2)
            {
                // DELETE ROOM TYPE
            }
            else
            {
                break;
            }
        }
    }
    
    private RoomType selectRoomType()
    {
        Scanner scanner = new Scanner(System.in);
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

        return allRoomTypes.get(selectedRoomTypeInt-1);
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
    
    private void updateRoomType(RoomType roomType)
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        Long lowerRoomTypeId = null;
        Long higherRoomTypeId = null;
        
        System.out.print("Enter Room Type Name (blank if unchanged)> ");
        String newName = scanner.nextLine().trim();
        if(newName.length() > 0)
        {
            roomType.setName(newName);
        }
        
        System.out.print("Enter Description (blank if unchanged)> ");
        String newDescription = scanner.nextLine().trim();
        if(newDescription.length() > 0)
        {
            roomType.setDescription(newDescription);
        }
        
        System.out.print("Enter Room Type size (blank if unchanged)> ");
        String newSize = scanner.nextLine().trim();
        if(newSize.length() > 0)
        {
            roomType.setSize(Integer.parseInt(newSize));
        }
        
        System.out.print("Enter Room Type capacity (blank if unchanged)> ");
        String newCapacity = scanner.nextLine().trim();
        if(newCapacity.length() > 0)
        {
            roomType.setCapacity(Integer.parseInt(newCapacity));
        }
        
        while(true)
        {
            System.out.print("Enter whether Room Type is enabled - 1. Enabled, 2. Disabled (blank if unchanged)> ");
            String newDisabled = scanner.nextLine().trim();
            if(newDisabled.length() > 0)
            {
                response = Integer.parseInt(newDisabled);
                if(response == 1)
                {
                    roomType.setDisabled(false);
                }
                else if(response == 2)
                {
                    roomType.setDisabled(true);
                }
                else
                {
                    System.out.println("Invalid option! Please try again!\n");
                }
            }
            else
            {
                break;
            }
        }
        
        response = 0;
        while(response < 1 || response > 2)
        {
            System.out.println("Would you like to change the beds in this room type?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            System.out.print("> ");
            
            response = scanner.nextInt();
            scanner.nextLine();
            if(response == 1)
            {
                List<Bed> newBedList = getBedList();
                roomType.setBeds(newBedList);
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
        
        response = 0;
        while(response < 1 || response > 2)
        {
            System.out.println("Would you like to change the amenities in this room type?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            System.out.print("> ");
            
            response = scanner.nextInt();
            scanner.nextLine();
            if(response == 1)
            {
                List<String> newAmenitiesList = getAmenitiesList();
                roomType.setAmenities(newAmenitiesList);
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
        
        response = 0;
        while(response < 1 || response > 2)
        {
            System.out.println("Would you like to update the ranking of this room type?");
            System.out.println("1: Yes");
            System.out.println("2: No");
            System.out.print("> ");
            
            response = scanner.nextInt();
            scanner.nextLine();
            if(response == 1)
            {
                List<RoomType> existingRoomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypesOrderedByRank();
                existingRoomTypes.removeIf(rt -> rt.getRoomTypeId().equals(roomType.getRoomTypeId()));
                if(existingRoomTypes.size() > 0)
                {
                    System.out.println("\n** Select Rank of new Room Type **");
                    System.out.println("1. Before " + existingRoomTypes.get(0).getName());
                    int i;
                    for(i = 2; i <= existingRoomTypes.size(); i++)
                    {
                        System.out.println(i + ". Between " + existingRoomTypes.get(i-2).getName() + " & " + existingRoomTypes.get(i - 1).getName());
                    }
                    System.out.println(i + ". After " + existingRoomTypes.get(i-2).getName());
                    
                    Integer selectedOption = 0;

                    while(selectedOption < 1 || selectedOption > i)
                    {
                        System.out.print("> ");
                        selectedOption = scanner.nextInt();
                        scanner.nextLine();

                        if(selectedOption == 1)
                        {
                            higherRoomTypeId = existingRoomTypes.get(0).getRoomTypeId();
                        }
                        else if(selectedOption > 1 && selectedOption < i)
                        {
                            lowerRoomTypeId = existingRoomTypes.get(selectedOption - 2).getRoomTypeId();
                            higherRoomTypeId = existingRoomTypes.get(selectedOption - 1).getRoomTypeId();
                        }
                        else if(selectedOption == i)
                        {
                            lowerRoomTypeId = existingRoomTypes.get(selectedOption - 2).getRoomTypeId();
                        }
                        else
                        {
                            System.out.println("Invalid Option! Please Try Again!\n");
                        }
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
        
        try
        {
            roomTypeSessionBeanRemote.updateRoomType(roomType, lowerRoomTypeId, higherRoomTypeId);
            System.out.println("Room Type updated successfully!\n");
        }
        catch (RoomTypeExistsException ex) 
        {
            System.out.println("An error has occurred while updating Room Type: " + ex.getMessage() + "\n");
        }
        catch(UnknownPersistenceException | InputDataValidationException ex)
        {
            System.out.println(ex.getMessage() + "\n");
        }
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
