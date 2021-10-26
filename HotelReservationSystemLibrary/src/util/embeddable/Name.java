/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.embeddable;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Name implements Serializable
{
    @Column(nullable = false, length = 64)
    private String firstName;
    @Column(nullable = false, length = 64)
    private String lastName;

    public Name() 
    {
    }

    public Name(String firstName, String lastName) 
    {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    /**
     * @return the firstName
     */
    public String getFirstName() 
    {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) 
    {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() 
    {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) 
    {
        this.lastName = lastName;
    }
}
