/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

public class RoomRateNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>RoomRateNotFoundException</code> without
     * detail message.
     */
    public RoomRateNotFoundException() {
    }

    /**
     * Constructs an instance of <code>RoomRateNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public RoomRateNotFoundException(String msg) {
        super(msg);
    }
}
