/**
 * @class_name Instructions
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class implements an actionlistener for the instructions button
 */

package ip.milton.cue.execution;

import ip.milton.cue.world.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class Instructions implements ActionListener {
	
	//Variable Declaration
	private GUI gui;
	
	public Instructions(GUI gui){
		this.gui = gui;
	}
	
	/**
	 * actionPerformed
	 * When fired, the main menu gets replaced with the instructions screen
	 * @param ActionEvent
	 * @return void
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.gui.getMainPanel().setVisible(false);
		this.gui.add(this.gui.getInstructionsPanel());
		this.gui.getInstructionsPanel().repaint();
		this.gui.getInstructionsPanel().setVisible(true);
	}

}
