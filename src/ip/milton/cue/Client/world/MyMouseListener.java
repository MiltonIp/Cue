/**
 * @class_name MyMouseListener
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class implements MouseListener to take in mouse clicks from user
 */

package ip.milton.cue.world;

import ip.milton.cue.objects.Tanks;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class MyMouseListener implements MouseListener {
	//Variable declarations
	private Timer timer;
	private TimerTask shootStun;

	private GameArena game;

	private JFrame frame;

	public MyMouseListener(GameArena game, JFrame frame) {
		//Initializing variables
		this.game = game;
		this.frame = frame;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//Do nothing
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		//Do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		//Do nothing
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		//Do nothing
	}

	/**
	 * mousePressed
	 * This method sends a mouse click to the server
	 * @param MouseEvent - The cursor
	 * @return void
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		//Sends to server that this tank has fired a cannonball
		try {
			Tanks tank = this.game.getTankList().get(this.game.getClient().getPlayer() - 1);
			if (!tank.isShootStun()) {
				tank.shoot();

				//Set delay of half a second before this player can shoot again 
				tank.setShootStun(true);
				timer = new Timer();
				shootStun = new TimerTask() {
					public void run() {
						tank.setShootStun(false);
					}
				};

				timer.schedule(shootStun, 500);
			}
		} catch (NullPointerException ne) {	//Error with connecting to server, trust me
			game.setVisible(false);
			GUI.setErrorMessage("Specified server not found, exit and try again");
			this.frame.add(GUI.getErrorPanel());
			GUI.getErrorPanel().validate();
			GUI.getErrorPanel().repaint();
			GUI.getErrorPanel().setVisible(true);
		}
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public TimerTask getShootStun() {
		return shootStun;
	}

	public void setShootStun(TimerTask shootStun) {
		this.shootStun = shootStun;
	}

	public GameArena getGame() {
		return game;
	}

	public void setGame(GameArena game) {
		this.game = game;
	}

}
