/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

public class ReservationNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>ReservationNotFoundException</code>
     * without detail message.
     */
    public ReservationNotFoundException() {
    }

    /**
     * Constructs an instance of <code>ReservationNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public ReservationNotFoundException(String msg) {
        super(msg);
    }
}
