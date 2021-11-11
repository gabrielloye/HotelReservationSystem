package hotelreservationsystemmanagementclient;

import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import util.enumeration.Bed;
import util.enumeration.RateType;
import util.exception.DeleteRoomException;
import util.exception.DeleteRoomRateException;
import util.exception.DeleteRoomTypeException;
import util.exception.InputDataValidationException;
import util.exception.RoomExistsException;
import util.exception.RoomNotFoundException;
import util.exception.RoomRateNotFoundException;
import util.exception.RoomTypeExistsException;
import util.exception.RoomTypeNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;
import util.exception.UpdateRoomTypeException;


public class HotelOperationModule
{
    
    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;

    public HotelOperationModule()
    {
    }
    
    public HotelOperationModule(RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote)
    {
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
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
                    viewAllRoomTypes();
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
        
        System.out.print("Enter Normal Room Rate> $");
        BigDecimal normalRate = scanner.nextBigDecimal();
        scanner.nextLine();
        
        System.out.print("Enter Published Room Rate> $");
        BigDecimal publishedRate = scanner.nextBigDecimal();
        scanner.nextLine();
        
        try
        {
            Long newRoomTypeId = roomTypeSessionBeanRemote.createNewRoomType(newRoomType, lowerRoomTypeId, higherRoomTypeId, normalRate, publishedRate);
            System.out.println("New Room Type created with ID: " + newRoomTypeId);
        }
        catch(RoomTypeExistsException ex)
        {
            System.out.println("An error has occurred while creating the new room type: The room type with name, " + newRoomType.getName()  + " already exists!\n");
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
                deleteRoomType(selectedRoomType);
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
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: Update Room Type Details\n");

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
        
        response = 0;
        while(response < 1 || response > 2)
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
            System.out.println("\nRoom Type updated successfully!\n");
        }
        catch(RoomTypeNotFoundException | UpdateRoomTypeException ex) 
        {
            System.out.println("\nAn error has occurred while updating Room Type: " + ex.getMessage() + "\n");
        }
        catch(InputDataValidationException ex)
        {
            System.out.println(ex.getMessage() + "\n");
        }
    }
    
    private void deleteRoomType(RoomType roomType)
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: Delete Room Type\n");
        System.out.printf("Confirm Deletion of Room Type %s (Room Type ID: %d) - Enter 'Y' to Delete> ", roomType.getName(), roomType.getRoomTypeId());
        
        String response = scanner.nextLine().trim();
        if(response.equals("Y"))
        {
            try
            {
                roomTypeSessionBeanRemote.deleteRoomType(roomType.getRoomTypeId());
                System.out.println("Room Type Deleted Successfully!\n");
            }
            catch(DeleteRoomTypeException ex)
            {
                System.out.println("\nAn error has occured while deleting room type: " + ex.getMessage() + "\n");
                roomTypeSessionBeanRemote.disableRoomType(roomType.getRoomTypeId());
                System.out.println("Room Type: " + roomType.getName() + " has been disabled and NOT deleted!\n");
            }
        }
        else
        {
            System.out.println("\nRoom Type NOT Deleted!\n");
        }
    }
    
    private void viewAllRoomTypes()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: View All Room Types\n");
        
        List<RoomType> roomTypes = roomTypeSessionBeanRemote.retrieveAllRoomTypes();
        System.out.printf("%-8s%-20s%-40s%-10s%-30s%-12s%-40s%-10s\n", "ID", "Name", "Description", "Size", "Beds", "Capacity", "Amenities", "Disabled");
        
        for(RoomType roomType : roomTypes)
        {
            System.out.printf("%-8s%-20s%-40s%-10s%-30s%-12s%-40s%-10s\n", roomType.getRoomTypeId(), roomType.getName(), roomType.getDescription(), roomType.getSize(), getBedListString(roomType.getBeds()), roomType.getCapacity(), String.join(", ", roomType.getAmenities()), roomType.getDisabled() ? "True" : "False");
        }
        
        System.out.print("Press Enter to continue...> ");
        scanner.nextLine();
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
                    createRoom();
                }
                else if(response == 2)
                {
                    updateRoom();
                }
                else if(response == 3)
                {
                    deleteRoom();
                }
                else if(response == 4)
                {
                    viewAllRooms();
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
    
    private void createRoom()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: Room Creation");
        Room newRoom = new Room();
        
        System.out.print("Enter New Room Number> ");
        String roomNumber = scanner.nextLine().trim();
        newRoom.setRoomNumber(roomNumber);
        
        Integer response = 0;
        while(response < 1 || response > 2)
        {
            System.out.println("\nSet Availability of New Room");
            System.out.println("1. Available");
            System.out.println("2. Unavailable");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();
            if(response == 1)
            {
                newRoom.setAvailable(true);
            }
            else if(response == 2)
            {
                newRoom.setAvailable(false);
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        System.out.println("Select Room Type of the new room being created");
        RoomType roomType = selectRoomType();
        
        try
        {
            Long newRoomId = roomSessionBeanRemote.createNewRoom(newRoom, roomType.getRoomTypeId());
            System.out.println("\nNew Room created with ID: " + newRoomId);
        }
        catch(RoomExistsException ex)
        {
            System.out.println("\nAn error has occurred while creating the new room: " + ex.getMessage() + "\n");
        }
        catch(UnknownPersistenceException ex)
        {
            System.out.println("\nAn unknown error has occurred while creating the new room!: " + ex.getMessage() + "\n");
        }
        catch(InputDataValidationException ex)
        {
            System.out.println("\n" + ex.getMessage() + "\n");
        }
    }
    
    private void updateRoom()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: Update Room\n");
        
        Room room = searchRoomNumber();
        
        Integer response = 0;
        while(response < 1 || response > 2)
        {
            System.out.print("Enter whether Room is available - 1. Available, 2. Unavailable (blank if unchanged)> ");
            String newAvailable = scanner.nextLine().trim();
            if(newAvailable.length() > 0)
            {
                response = Integer.parseInt(newAvailable);
                if(response == 1)
                {
                    room.setAvailable(true);
                }
                else if(response == 2)
                {
                    room.setAvailable(false);
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
            System.out.println("Would you like to change the room type of this room?");
            System.out.println("Current Room Type: " + room.getRoomType().getName());
            System.out.println("1: Yes");
            System.out.println("2: No");
            System.out.print("> ");
            
            response = scanner.nextInt();
            scanner.nextLine();
            if(response == 1)
            {
                room.setRoomType(selectRoomType());
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
            roomSessionBeanRemote.updateRoom(room);
            System.out.println("\nRoom updated successfully!\n");
        }
        catch (RoomNotFoundException | UpdateRoomException ex) 
        {
            System.out.println("\nAn error has occurred while updating the room: " + ex.getMessage() + "\n");
        }
        catch(InputDataValidationException ex)
        {
            System.out.println("\n" + ex.getMessage() + "\n");
        }
    }
    
    private void deleteRoom()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: Delete Room\n");
        
        Room room = searchRoomNumber();
        
        System.out.printf("\nConfirm Deletion of Room %s (Room ID: %d) - Enter 'Y' to Delete> ", room.getRoomNumber(), room.getRoomId());
        
        String response = scanner.nextLine().trim();
        if(response.equals("Y"))
        {
            try
            {
                roomSessionBeanRemote.deleteRoom(room.getRoomId());
                System.out.println("\nRoom Deleted Successfully!\n");
            }
            catch(DeleteRoomException ex)
            {
                System.out.println("\nAn error has occured while deleting room: " + ex.getMessage() + "\n");
                roomSessionBeanRemote.unavailRoom(room.getRoomId());
                System.out.println("Room: " + room.getRoomNumber() + " has been made unavailable and NOT deleted!\n");
            }
        }
        else
        {
            System.out.println("\nRoom NOT Deleted!\n");
        }
    }
    
    private void viewAllRooms()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: View All Rooms\n");
        
        List<Room> rooms = roomSessionBeanRemote.retrieveAllRooms();
        System.out.printf("%-8s%-15s%-20s%-10s\n", "ID", "Room Number", "Room Type", "Available");
        
        for(Room room : rooms)
        {
            System.out.printf("%-8s%-15s%-20s%-10s\n", room.getRoomId(), room.getRoomNumber(), room.getRoomType().getName(), room.getAvailable() ? "True" : "False");
        }
        
        System.out.print("Press Enter to continue...> ");
        scanner.nextLine();
    }
    
    private Room searchRoomNumber()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter Room Number> ");
        String roomNumber = scanner.nextLine().trim();
        
        String response = "Y";
        while(true)
        {
            try
            {
                return roomSessionBeanRemote.retrieveRoomByRoomNumber(roomNumber);
            }
            catch(RoomNotFoundException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }
    
        
    public void salesManagerMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("\n*** HoRS Management Client :: Sales Manager Menu ***\n");
            System.out.println("1: Create New Room Rate");
            System.out.println("2: View Room Rate Details");
            System.out.println("3: View All Room Rates");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    createNewRoomRate();
                }
                else if(response == 2)
                {
                    try
                    {
                        viewRoomRate();
                    }
                    catch(RoomRateNotFoundException ex)
                    {
                        System.out.println("\n" + ex.getMessage());
                    }
                }
                else if(response == 3)
                {
                    viewAllRoomRates();
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
    
    private void createNewRoomRate()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: Room Rate Creation");
        RoomRate newRoomRate = new RoomRate();
        
        System.out.print("Enter Room Rate Name> ");
        String roomRateName = scanner.nextLine().trim();
        newRoomRate.setName(roomRateName);
        
        while(true)
        {
            System.out.println("Select Rate Type:");
            System.out.print("(1: Published, 2: Normal, 3: Peak, 4: Promotion)> ");
            Integer rateTypeInt = scanner.nextInt();
            
            if(rateTypeInt >= 1 && rateTypeInt <= 4)
            {
                newRoomRate.setRateType(RateType.values()[rateTypeInt-1]);
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        System.out.println("Select Room Type for the new Room Rate");
        RoomType roomType = selectRoomType();
        
        if(newRoomRate.getRateType().equals(RateType.PEAK) || newRoomRate.getRateType().equals(RateType.PROMOTION))
        {
            while(true)
            {
                Date startDate = enterDate("Enter Validity Start Date> ");
                Date endDate = enterDate("Enter Validity End Date> ");
                if(startDate.compareTo(endDate) >= 0) // If start date is same as or after end date, show error message
                {
                    System.out.println("Invalid Start/End Date Range: Start date must be before end date");
                }
                else
                {
                    newRoomRate.setValidityStartDate(startDate);
                    newRoomRate.setValidityEndDate(endDate);
                    break;
                }
            }
        }
        
        System.out.print("Enter Rate Per Night> ");
        BigDecimal ratePerNight = scanner.nextBigDecimal();
        scanner.nextLine();
        newRoomRate.setRatePerNight(ratePerNight);
        
        Integer response = 0;
        while(response < 1 || response > 2)
        {
            System.out.println("\nSet Disabled status of new Room Rate");
            System.out.println("1. Enabled");
            System.out.println("2. Disabled");
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();
            if(response == 1)
            {
                newRoomRate.setDisabled(false);
            }
            else if(response == 2)
            {
                newRoomRate.setDisabled(true);
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        try
        {
            Long newRoomRateId = roomRateSessionBeanRemote.createNewRoomRate(newRoomRate, roomType.getRoomTypeId());
            System.out.println("\nNew Room Rate created with ID: " + newRoomRateId);
        }
        catch(UnknownPersistenceException ex)
        {
            System.out.println("\nAn unknown error has occurred while creating the new room rate!: " + ex.getMessage() + "\n");
        }
    }
    
    private void viewRoomRate() throws RoomRateNotFoundException
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n*** HoRS Management Client :: Sales Manager Menu :: View Room Rate Details\n");
        
        System.out.print("Enter Room Rate ID> ");
        Long roomRateId = scanner.nextLong();
        
        RoomRate roomRate = roomRateSessionBeanRemote.retrieveRoomRateByRoomRateId(roomRateId);
        
        System.out.println("\nRoom Rate Name: " + roomRate.getName());
        System.out.println("Rate Type: " + roomRate.getRateType().toString());
        System.out.println("Rate Per Night: $" + roomRate.getRatePerNight());
        System.out.println("Room Type: " + roomRate.getRoomType().getName());
        System.out.println("Validity Start Date: " + formatDate(roomRate.getValidityStartDate()));
        System.out.println("Validity End Date: " + formatDate(roomRate.getValidityEndDate()));
        System.out.println("Disabled: " + (roomRate.getDisabled() ? "True" : "False"));
        
        System.out.println("--------------------");
        System.out.println("1: Update Room Rate");
        System.out.println("2: Delete Room Rate");
        System.out.println("3: Back\n");
        
        int response = 0;
        while(response < 1 || response > 3)
        {
            System.out.print("> ");
            response = scanner.nextInt();
            scanner.nextLine();
            if(response == 1)
            {
                updateRoomRate(roomRate);
            }
            else if(response == 2)
            {
                deleteRoomRate(roomRate);
            }
            else
            {
                break;
            }
        }
    }
    
    private void updateRoomRate(RoomRate roomRate)
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Sales Manager Menu :: Update Room Rate Details\n");

        System.out.print("Enter Room Rate Name (blank if unchanged)> ");
        String newName = scanner.nextLine().trim();
        if(newName.length() > 0)
        {
            roomRate.setName(newName);
        }
        
        while(true)
        {
            System.out.println("Rate Types - 1: Published, 2: Normal, 3: Peak, 4: Promotion");
            System.out.print("Select Rate Type (blank if unchanged)> ");
            String rateType = scanner.nextLine().trim();
            
            if(rateType.length() > 0)
            {
                Integer rateTypeInt = Integer.parseInt(rateType);
                if(rateTypeInt >= 1 && rateTypeInt <= 4)
                {
                    roomRate.setRateType(RateType.values()[rateTypeInt-1]);
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            else
            {
                break;
            }
        }
        
        System.out.print("Enter Rate Per Night (blank if unchanged)> ");
        String ratePerNight = scanner.nextLine().trim();
        if(ratePerNight.length() > 0)
        {
            roomRate.setRatePerNight(new BigDecimal(ratePerNight));
        }
        
        if(roomRate.getRateType().equals(RateType.PEAK) || roomRate.getRateType().equals(RateType.PROMOTION))
        {
            Integer response = 0;
            while(response < 1 || response > 2)
            {
                System.out.println("Would you like to change the validity start/end date?");
                System.out.println("1: Yes");
                System.out.println("2: No");
                System.out.print("> ");

                response = scanner.nextInt();
                scanner.nextLine();
                if(response == 1)
                {
                    while(true)
                    {
                        Date startDate = enterDate("Enter Validity Start Date> ");
                        Date endDate = enterDate("Enter Validity End Date> ");
                        if(startDate.compareTo(endDate) >= 0) // If start date is same as or after end date, show error message
                        {
                            System.out.println("Invalid Start/End Date Range: Start date must be before end date");
                        }
                        else
                        {
                            roomRate.setValidityStartDate(startDate);
                            roomRate.setValidityEndDate(endDate);
                            break;
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
        
        
        
        Integer response = 0;
        while(response < 1 || response > 2)
        {
            System.out.print("Enter whether Room Rate is enabled - 1. Enabled, 2. Disabled (blank if unchanged)> ");
            String newDisabled = scanner.nextLine().trim();
            if(newDisabled.length() > 0)
            {
                response = Integer.parseInt(newDisabled);
                if(response == 1)
                {
                    roomRate.setDisabled(false);
                }
                else if(response == 2)
                {
                    roomRate.setDisabled(true);
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
        
        try
        {
            roomRateSessionBeanRemote.updateRoomRate(roomRate);
            System.out.println("\nRoom Rate updated successfully!\n");
        }
        catch(RoomRateNotFoundException ex) 
        {
            System.out.println("\nAn error has occurred while updating Room Rate: " + ex.getMessage() + "\n");
        }
    }
    
    private void deleteRoomRate(RoomRate roomRate)
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Sales Manager Menu :: Delete Room Rate\n");
        
        System.out.printf("\nConfirm Deletion of Room Rate %s (Room Rate ID: %d) - Enter 'Y' to Delete> ", roomRate.getName(), roomRate.getRoomRateId());
        
        String response = scanner.nextLine().trim();
        if(response.equals("Y"))
        {
            try
            {
                roomRateSessionBeanRemote.deleteRoomRate(roomRate.getRoomRateId());
                System.out.println("\nRoom Rate Deleted Successfully!\n");
            }
            catch(DeleteRoomRateException ex)
            {
                System.out.println("\nAn error has occured while deleting room Rate: " + ex.getMessage() + "\n");
                if(roomRate.getRateType().equals(RateType.PEAK) || roomRate.getRateType().equals(RateType.PROMOTION))
                {
                    try
                    {
                        roomRateSessionBeanRemote.disableRoomRate(roomRate.getRoomRateId());
                        System.out.println("Room Rate: " + roomRate.getName() + " has been disabled and NOT deleted!\n");
                    }
                    catch(RoomRateNotFoundException e)
                    {
                        System.out.println("\nRoom Rate cannot be found!");
                    }
                }      
            }
            catch(RoomRateNotFoundException ex)
            {
                System.out.println("\nRoom Rate cannot be found!");
            }
        }
        else
        {
            System.out.println("\nRoom Rate NOT Deleted!\n");
        }
    }
    
    private void viewAllRoomRates()
    {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** HoRS Management Client :: Operation Manager Menu :: View All Room Rates\n");
        
        List<RoomRate> roomRates = roomRateSessionBeanRemote.retrieveAllRoomRates();
        
        System.out.printf("%-8s%-30s%-20s%-15s%-18s%-25s%-25s%-15s\n", "ID", "Name", "RoomType", "Rate Type", "Rate Per Night", "Validity Start Date", "Validity End Date", "Disabled");
        
        for(RoomRate roomRate : roomRates)
        {
            System.out.printf("%-8s%-30s%-20s%-15s%-18s%-25s%-25s%-15s\n", roomRate.getRoomRateId(), roomRate.getName(), roomRate.getRoomType().getName(), roomRate.getRateType(),
                roomRate.getRatePerNight(), formatDate(roomRate.getValidityStartDate()), formatDate(roomRate.getValidityEndDate()), roomRate.getDisabled() ? "True" : "False");
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
