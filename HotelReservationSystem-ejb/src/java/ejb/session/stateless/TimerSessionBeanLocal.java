/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import javax.ejb.Local;

@Local
public interface TimerSessionBeanLocal {

    public void currentDayRoomAllocationTimer();
    
    public void useTimer();
}
