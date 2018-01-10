/**
 * @class_name MyKeyListener
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class extends KeyAdapter to take in key inputs from user
 */

package ip.milton.cue.world;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class MyKeyListener extends KeyAdapter {

	//Variable declarations
	private GameArena game;

	private JFrame frame;

	public MyKeyListener(GameArena game, JFrame frame) {
		//Initailizing variables
		this.game = game;
		this.frame = frame;
	}

	/**
	 * keyReleased
	 * This method sends the key released to the server
	 * @param KeyEvent - The key released
	 * @return void
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		try {
			this.game.getTankList().get(this.game.getClient().getPlayer() - 1).keyReleased(e);
		} catch (NullPointerException ne) {	//Error with connecting to server, trust me
			game.setVisible(false);
			GUI.setErrorMessage("Specified server not found, exit and try again");
			this.frame.add(GUI.getErrorPanel());
			GUI.getErrorPanel().validate();
			GUI.getErrorPanel().repaint();
			GUI.getErrorPanel().setVisible(true);
		}
	}

	/**
	 * keyPressed
	 * This method sends the key pressed to the server
	 * @param KeyEvent - The key pressed
	 * @return void
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		try {
			this.game.getTankList().get(this.game.getClient().getPlayer() - 1).keyPressed(e, false);
		} catch (NullPointerException ne) {	//Error with connecting to server, trust me
			game.setVisible(false);
			GUI.setErrorMessage("Specified server not found, exit and try again");
			this.frame.add(GUI.getErrorPanel());
			GUI.getErrorPanel().validate();
			GUI.getErrorPanel().repaint();
			GUI.getErrorPanel().setVisible(true);
		}
	}
}
