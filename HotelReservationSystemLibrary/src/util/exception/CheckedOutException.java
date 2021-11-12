/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

public class CheckedOutException extends Exception {

    /**
     * Creates a new instance of <code>CheckedOutException</code> without detail
     * message.
     */
    public CheckedOutException() {
    }

    /**
     * Constructs an instance of <code>CheckedOutException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CheckedOutException(String msg) {
        super(msg);
    }
}
