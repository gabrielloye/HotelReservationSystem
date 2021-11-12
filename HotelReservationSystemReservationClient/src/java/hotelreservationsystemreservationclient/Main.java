/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelreservationsystemreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import javax.ejb.EJB;

public class Main {

    @EJB
    private static GuestSessionBeanRemote guestSessionBeanRemote;
    @EJB
    private static ReservationSessionBeanRemote reservationSessionBeanRemote;

    public static void main(String[] args) {
        MainApp mainApp = new MainApp(guestSessionBeanRemote, reservationSessionBeanRemote);
        mainApp.runApp();
    }
    
}
