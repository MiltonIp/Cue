/**
 * @class_name Exit
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class implements an actionlistener for the exit button
 */

package ip.milton.cue.execution;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Exit implements ActionListener {
	
	/**
	 * actionPerformed
	 * When fired, the program closes
	 * @param ActionEvent
	 * @return void
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}
