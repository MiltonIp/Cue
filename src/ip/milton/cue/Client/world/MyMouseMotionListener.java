/**
 * @class_name MyMouseListener
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class implements MouseMotionListener to take in mouse movement from user
 */

package ip.milton.cue.world;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public class MyMouseMotionListener implements MouseMotionListener {

	//Variable declaration
	private GameArena game;

	private JFrame frame;

	public MyMouseMotionListener(GameArena game, JFrame frame) {
		//Initializing variables
		this.game = game;
		this.frame = frame;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		//Do nothing
	}

	/**
	 * mouseMoved
	 * This method sends the x y of the cursor to be calculated into an angle which will be sent to the server
	 * @param MouseEvent - The cursor 
	 * @return void
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		try {
			this.game.getTankList().get(this.game.getClient().getPlayer() - 1).mouseMoved(e.getX(), e.getY());
		} catch (NullPointerException ne) {	//Error connecting to server, trust me
			game.setVisible(false);
			GUI.setErrorMessage("Specified server not found, exit and try again");
			this.frame.add(GUI.getErrorPanel());
			GUI.getErrorPanel().validate();
			GUI.getErrorPanel().repaint();
			GUI.getErrorPanel().setVisible(true);
		}
	}

	public GameArena getGame() {
		return game;
	}

	public void setGame(GameArena game) {
		this.game = game;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}
	
	

}
