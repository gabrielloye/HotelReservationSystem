package ejb.session.singleton;

import entity.Customer;
import entity.Employee;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.embeddable.Name;
import util.enumeration.Bed;
import util.enumeration.EmployeeAccessRight;
import util.enumeration.RateType;

@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct()
    {
        if(em.find(Employee.class, 1l) == null)
        {
            /**
            * Employee generation
            **/
            
            Employee employeeA = new Employee(new Name("Employee", "One"), EmployeeAccessRight.SYSTEMADMINISTRATOR, "sysadmin", "password");
            em.persist(employeeA);
            em.flush();
            Employee employeeB = new Employee(new Name("Employee", "Two"), EmployeeAccessRight.OPERATIONMANAGER, "opmanager", "password");
            em.persist(employeeB);
            em.flush();
            Employee employeeC = new Employee(new Name("Employee", "Three"), EmployeeAccessRight.SALESMANAGER, "salesmanager", "password");
            em.persist(employeeC);
            em.flush();
            Employee employeeD = new Employee(new Name("Employee", "Four"), EmployeeAccessRight.GUESTRELATIONOFFICER, "guestrelo", "password");
            em.persist(employeeD);
            em.flush();
            
            /**
            * RoomType generation
            **/
            
            List<String> amenities = new ArrayList<>();
            amenities.add("Fridge");
            
            List<Bed> deluxeBeds = new ArrayList<>();
            deluxeBeds.add(Bed.QUEEN);
            RoomType deluxeRoomType = new RoomType("Deluxe Room", "Deluxe Room Type", 450, deluxeBeds, 2, amenities, false);
            em.persist(deluxeRoomType);
            em.flush();
            
            List<Bed> premierBeds = new ArrayList<>();
            premierBeds.add(Bed.KING);
            RoomType premierRoomType = new RoomType("Premier Room", "Premier Room Type", 500, premierBeds, 2, amenities, false);
            em.persist(premierRoomType);
            em.flush();
            
            List<Bed> familyBeds = new ArrayList<>();
            familyBeds.add(Bed.QUEEN);
            familyBeds.add(Bed.SINGLE);
            familyBeds.add(Bed.SINGLE);
            RoomType familyRoomType = new RoomType("Family Room", "Family Room Type", 550, familyBeds, 4, amenities, false);
            em.persist(familyRoomType);
            em.flush();
            
            List<Bed> juniorSuiteBeds = new ArrayList<>();
            juniorSuiteBeds.add(Bed.QUEEN);
            juniorSuiteBeds.add(Bed.QUEEN);
            juniorSuiteBeds.add(Bed.SINGLE);
            RoomType juniorSuiteRoomType = new RoomType("Junior Suite", "Junior Suite Room Type", 600, juniorSuiteBeds, 5, amenities, false);
            em.persist(juniorSuiteRoomType);
            em.flush();
            
            List<Bed> grandSuiteBeds = new ArrayList<>();
            grandSuiteBeds.add(Bed.KING);
            grandSuiteBeds.add(Bed.KING);
            grandSuiteBeds.add(Bed.SINGLE);
            grandSuiteBeds.add(Bed.SINGLE);
            RoomType grandSuiteRoomType = new RoomType("Grand Suite", "Grand Suite Room Type", 650, grandSuiteBeds, 6, amenities, false);
            em.persist(grandSuiteRoomType);
            em.flush();
            
            deluxeRoomType.setHigherRoomType(premierRoomType);
            premierRoomType.setLowerRoomType(deluxeRoomType);
            premierRoomType.setHigherRoomType(familyRoomType);
            familyRoomType.setLowerRoomType(premierRoomType);
            familyRoomType.setHigherRoomType(juniorSuiteRoomType);
            juniorSuiteRoomType.setLowerRoomType(familyRoomType);
            juniorSuiteRoomType.setHigherRoomType(grandSuiteRoomType);
            grandSuiteRoomType.setLowerRoomType(juniorSuiteRoomType);
            
            /**
            * RoomRate generation
            **/
            
            RoomRate deluxeRoomPub = new RoomRate("Deluxe Room Published", RateType.PUBLISHED, BigDecimal.valueOf(100.0), null, null, false);
            em.persist(deluxeRoomPub);
            deluxeRoomPub.setRoomType(deluxeRoomType);
            deluxeRoomType.getRoomRates().add(deluxeRoomPub);
            em.flush();
            
            RoomRate deluxeRoomNorm = new RoomRate("Deluxe Room Normal", RateType.NORMAL, BigDecimal.valueOf(50.0), null, null, false);
            em.persist(deluxeRoomNorm);
            deluxeRoomNorm.setRoomType(deluxeRoomType);
            deluxeRoomType.getRoomRates().add(deluxeRoomNorm);
            em.flush();
            
            RoomRate premierRoomPub = new RoomRate("Premier Room Published", RateType.PUBLISHED, BigDecimal.valueOf(200.0), null, null, false);
            em.persist(premierRoomPub);
            premierRoomPub.setRoomType(premierRoomType);
            premierRoomType.getRoomRates().add(premierRoomPub);
            em.flush();
            
            RoomRate premierRoomNorm = new RoomRate("Premier Room Normal", RateType.NORMAL, BigDecimal.valueOf(100.0), null, null, false);
            em.persist(premierRoomNorm);
            premierRoomNorm.setRoomType(premierRoomType);
            premierRoomType.getRoomRates().add(premierRoomNorm);
            em.flush();
            
            RoomRate familyRoomPub = new RoomRate("Family Room Published", RateType.PUBLISHED, BigDecimal.valueOf(300.0), null, null, false);
            em.persist(familyRoomPub);
            familyRoomPub.setRoomType(familyRoomType);
            familyRoomType.getRoomRates().add(familyRoomPub);
            em.flush();
            
            RoomRate familyRoomNorm = new RoomRate("Family Room Normal", RateType.NORMAL, BigDecimal.valueOf(150.0), null, null, false);
            em.persist(familyRoomNorm);
            familyRoomNorm.setRoomType(familyRoomType);
            familyRoomType.getRoomRates().add(familyRoomNorm);
            em.flush();
            
            RoomRate juniorSuitePub = new RoomRate("Junior Suite Published", RateType.PUBLISHED, BigDecimal.valueOf(400.0), null, null, false);
            em.persist(juniorSuitePub);
            juniorSuitePub.setRoomType(juniorSuiteRoomType);
            juniorSuiteRoomType.getRoomRates().add(juniorSuitePub);
            em.flush();
            
            RoomRate juniorSuiteNorm = new RoomRate("Junior Suite Normal", RateType.NORMAL, BigDecimal.valueOf(200.0), null, null, false);
            em.persist(juniorSuiteNorm);
            juniorSuiteNorm.setRoomType(juniorSuiteRoomType);
            juniorSuiteRoomType.getRoomRates().add(juniorSuiteNorm);
            em.flush();
            
            RoomRate grandSuitePub = new RoomRate("Grand Suite Published", RateType.PUBLISHED, BigDecimal.valueOf(500.0), null, null, false);
            em.persist(grandSuitePub);
            grandSuitePub.setRoomType(grandSuiteRoomType);
            grandSuiteRoomType.getRoomRates().add(grandSuitePub);
            em.flush();
            
            RoomRate grandSuiteNorm = new RoomRate("Grand Suite Normal", RateType.NORMAL, BigDecimal.valueOf(250.0), null, null, false);
            em.persist(grandSuiteNorm);
            grandSuiteNorm.setRoomType(grandSuiteRoomType);
            grandSuiteRoomType.getRoomRates().add(grandSuiteNorm);
            em.flush();
            
            /**
            * Room generation
            **/
            
            Room roomA = new Room("0101", true);
            em.persist(roomA);
            roomA.setRoomType(deluxeRoomType);
            deluxeRoomType.getRooms().add(roomA);
            em.flush();
            
            Room roomB = new Room("0201", true);
            em.persist(roomB);
            roomB.setRoomType(deluxeRoomType);
            deluxeRoomType.getRooms().add(roomB);
            em.flush();
            
            Room roomC = new Room("0301", true);
            em.persist(roomC);
            roomC.setRoomType(deluxeRoomType);
            deluxeRoomType.getRooms().add(roomC);
            em.flush();
            
            Room roomD = new Room("0401", true);
            em.persist(roomD);
            roomD.setRoomType(deluxeRoomType);
            deluxeRoomType.getRooms().add(roomD);
            em.flush();
            
            Room roomE = new Room("0501", true);
            em.persist(roomE);
            roomE.setRoomType(deluxeRoomType);
            deluxeRoomType.getRooms().add(roomE);
            em.flush();
            
            
            roomA = new Room("0102", true);
            em.persist(roomA);
            roomA.setRoomType(premierRoomType);
            premierRoomType.getRooms().add(roomA);
            em.flush();
            
            roomB = new Room("0202", true);
            em.persist(roomB);
            roomB.setRoomType(premierRoomType);
            premierRoomType.getRooms().add(roomB);
            em.flush();
            
            roomC = new Room("0302", true);
            em.persist(roomC);
            roomC.setRoomType(premierRoomType);
            premierRoomType.getRooms().add(roomC);
            em.flush();
            
            roomD = new Room("0402", true);
            em.persist(roomD);
            roomD.setRoomType(premierRoomType);
            premierRoomType.getRooms().add(roomD);
            em.flush();
            
            roomE = new Room("0502", true);
            em.persist(roomE);
            roomE.setRoomType(premierRoomType);
            premierRoomType.getRooms().add(roomE);
            em.flush();
            
            
            roomA = new Room("0103", true);
            em.persist(roomA);
            roomA.setRoomType(familyRoomType);
            familyRoomType.getRooms().add(roomA);
            em.flush();
            
            roomB = new Room("0203", true);
            em.persist(roomB);
            roomB.setRoomType(familyRoomType);
            familyRoomType.getRooms().add(roomB);
            em.flush();
            
            roomC = new Room("0303", true);
            em.persist(roomC);
            roomC.setRoomType(familyRoomType);
            familyRoomType.getRooms().add(roomC);
            em.flush();
            
            roomD = new Room("0403", true);
            em.persist(roomD);
            roomD.setRoomType(familyRoomType);
            familyRoomType.getRooms().add(roomD);
            em.flush();
            
            roomE = new Room("0503", true);
            em.persist(roomE);
            roomE.setRoomType(familyRoomType);
            familyRoomType.getRooms().add(roomE);
            em.flush();
            
            
            roomA = new Room("0104", true);
            em.persist(roomA);
            roomA.setRoomType(juniorSuiteRoomType);
            juniorSuiteRoomType.getRooms().add(roomA);
            em.flush();
            
            roomB = new Room("0204", true);
            em.persist(roomB);
            roomB.setRoomType(juniorSuiteRoomType);
            juniorSuiteRoomType.getRooms().add(roomB);
            em.flush();
            
            roomC = new Room("0304", true);
            em.persist(roomC);
            roomC.setRoomType(juniorSuiteRoomType);
            juniorSuiteRoomType.getRooms().add(roomC);
            em.flush();
            
            roomD = new Room("0404", true);
            em.persist(roomD);
            roomD.setRoomType(juniorSuiteRoomType);
            juniorSuiteRoomType.getRooms().add(roomD);
            em.flush();
            
            roomE = new Room("0504", true);
            em.persist(roomE);
            roomE.setRoomType(juniorSuiteRoomType);
            juniorSuiteRoomType.getRooms().add(roomE);
            em.flush();
            
            
            roomA = new Room("0105", true);
            em.persist(roomA);
            roomA.setRoomType(grandSuiteRoomType);
            grandSuiteRoomType.getRooms().add(roomA);
            em.flush();
            
            roomB = new Room("0205", true);
            em.persist(roomB);
            roomB.setRoomType(grandSuiteRoomType);
            grandSuiteRoomType.getRooms().add(roomB);
            em.flush();
            
            roomC = new Room("0305", true);
            em.persist(roomC);
            roomC.setRoomType(grandSuiteRoomType);
            grandSuiteRoomType.getRooms().add(roomC);
            em.flush();
            
            roomD = new Room("0405", true);
            em.persist(roomD);
            roomD.setRoomType(grandSuiteRoomType);
            grandSuiteRoomType.getRooms().add(roomD);
            em.flush();
            
            roomE = new Room("0505", true);
            em.persist(roomE);
            roomE.setRoomType(grandSuiteRoomType);
            grandSuiteRoomType.getRooms().add(roomE);
            em.flush();
            
            //test retrieve reservation by date
            Reservation reservationA = new Reservation(new Date(), new Date(121, 10, 6), new Date(121, 10, 9), 3, BigDecimal.valueOf(100), false, false);
            Customer customerA = new Customer(new Name("customer", "A"), "email1", Long.parseLong("9"));
            em.persist(reservationA);
            em.persist(customerA);
            reservationA.setCustomer(customerA);
            customerA.getReservations().add(reservationA);
            reservationA.setRoomType(deluxeRoomType);
            deluxeRoomType.getReservations().add(reservationA);
            em.flush();
            
            //test upgraded and unavailable (expect 5x normal, 5x UPGRADED, 1x UNAVAILABLE)
            Reservation reservationB = new Reservation(new Date(), new Date(121, 10, 8), new Date(121, 10, 9), 11, BigDecimal.valueOf(100), false, false);
            Customer customerB = new Customer(new Name("customer", "B"), "email2", Long.parseLong("8"));
            em.persist(reservationB);
            em.persist(customerB);
            reservationB.setCustomer(customerB);
            customerB.getReservations().add(reservationB);
            reservationB.setRoomType(deluxeRoomType);
            deluxeRoomType.getReservations().add(reservationB);
            em.flush();
            
            //test no higher roomtype (expect 5x normal, 1x UNAVAILABLE)
            Reservation reservationD = new Reservation(new Date(), new Date(121, 10, 8), new Date(121, 10, 9), 6, BigDecimal.valueOf(100), false, false);
            Customer customerD = new Customer(new Name("customer", "D"), "email4", Long.parseLong("6"));
            em.persist(reservationD);
            em.persist(customerD);
            reservationD.setCustomer(customerD);
            customerD.getReservations().add(reservationD);
            reservationD.setRoomType(grandSuiteRoomType);
            grandSuiteRoomType.getReservations().add(reservationD);
            em.flush();
            
            //test overlapping (expect UPGRADED x3)
            Reservation reservationE = new Reservation(new Date(), new Date(121, 10, 8), new Date(121, 10, 9), 3, BigDecimal.valueOf(100), false, false);
            Customer customerE = new Customer(new Name("customer", "E"), "email5", Long.parseLong("5"));
            em.persist(reservationE);
            em.persist(customerE);
            reservationE.setCustomer(customerE);
            customerE.getReservations().add(reservationE);
            reservationE.setRoomType(premierRoomType);
            premierRoomType.getReservations().add(reservationE);
            em.flush();
            
            //test overlapping (expect normal x 2)
            Reservation reservationF = new Reservation(new Date(), new Date(121, 10, 8), new Date(121, 10, 9), 2, BigDecimal.valueOf(100), false, false);
            Customer customerF = new Customer(new Name("customer", "F"), "email6", Long.parseLong("4"));
            em.persist(reservationF);
            em.persist(customerF);
            reservationF.setCustomer(customerF);
            customerF.getReservations().add(reservationF);
            reservationF.setRoomType(familyRoomType);
            familyRoomType.getReservations().add(reservationF);
            em.flush();
            
            //test retrieve reservation by date
            Reservation reservationC = new Reservation(new Date(), new Date(121, 10, 9), new Date(121, 10, 9), 3, BigDecimal.valueOf(100), false, false);
            Customer customerC = new Customer(new Name("customer", "C"), "email3", Long.parseLong("7"));
            em.persist(reservationC);
            em.persist(customerC);
            reservationC.setCustomer(customerC);
            customerC.getReservations().add(reservationC);
            reservationC.setRoomType(deluxeRoomType);
            deluxeRoomType.getReservations().add(reservationC);
            em.flush();
        }
    }
    
}
