/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

public class GuestExistsException extends Exception {

    /**
     * Creates a new instance of <code>GuestExistsException</code> without
     * detail message.
     */
    public GuestExistsException() {
    }

    /**
     * Constructs an instance of <code>GuestExistsException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public GuestExistsException(String msg) {
        super(msg);
    }
}
