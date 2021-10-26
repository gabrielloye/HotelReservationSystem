/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import util.embeddable.Name;

@Entity
public class Guest extends Customer implements Serializable {

    @Column(nullable = false, length = 64, unique = true)
    private String username;
    @Column(nullable = false, length = 64)
    private String password;

    public Guest() {
    }

    public Guest(String username, String password, Name name, String email, Long mobileNum) {
        super(name, email, mobileNum);
        this.username = username;
        this.password = password;
    }


    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
}
