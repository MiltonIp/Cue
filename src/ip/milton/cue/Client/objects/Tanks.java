/**
 * @class_name Tanks
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class models a tank
 */

package ip.milton.cue.objects;

import ip.milton.cue.world.GUI;
import ip.milton.cue.world.GameArena;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import javax.imageio.ImageIO;

import javax.swing.JFrame;

public class Tanks extends MoveableObject {
	
	//Variable Declaration
	private int player;
	
	private BufferedImage tankBody;
	private BufferedImage tankHead;

	private ArrayList<Integer> keysPressed = new ArrayList<Integer>();

	private boolean shootStun;
	private boolean slain;

	private double mouseRadians;

	private GameArena game;
	
	private JFrame frame;

	public Tanks(int player, GameArena game, JFrame frame) {
		//Initializing variables
		this.game = game;
		this.frame = frame;
		this.player = player;

		final int defaultSpeed = 10;
		this.setSpeed(defaultSpeed);

		this.shootStun = false;

		//Loading tank sprites
		try {
			if (player == 1) {
				this.tankBody = ImageIO.read(new File("Images/tankBody1New.png"));
				this.tankHead = ImageIO.read(new File("Images/tankHead1New.png"));
				this.setX(0);
				this.setY((int) ((this.game.getFrame().getHeight() / 2) - (this.tankBody.getHeight() / 2)));

			} else if (player == 2) {
				this.tankBody = ImageIO.read(new File("Images/tankBody2New.png"));
				this.tankHead = ImageIO.read(new File("Images/tankHead2New.png"));
				this.setX((int) ((this.game.getFrame().getWidth()) - (this.tankBody.getWidth())));
				this.setY((int) ((this.game.getFrame().getHeight() / 2) - (this.tankBody.getHeight() / 2)));
			}
			//Displays error message is error occurs
		} catch (IOException e) {
			game.setVisible(false);
			GUI.setErrorMessage("Tank sprites not found, exit, check paths (images should be inside 'Images' folder) and try again");
			this.frame.add(GUI.getErrorPanel());
			GUI.getErrorPanel().revalidate();
			GUI.getErrorPanel().repaint();
			GUI.getErrorPanel().setVisible(true);
		}

	}
	
	/**
	 * shoot
	 * This method triggers another method to run
	 * @param null
	 * @return void
	 */
	public void shoot() {
		this.game.getClient().sendCannonball();
	}

	/**
	 * keyReleased
	 * This method removes the key let go from the arraylist and sends information to the server
	 * @param KeyEvent - The key that was let go by the player
	 * @return void
	 */
	public void keyReleased(KeyEvent e) {

		int key = e.getKeyCode();

		//Removing key from arraylist
		for (int i = 0, p = keysPressed.size(); i < p; i++) {
			if (keysPressed.get(i).equals(key)) {
				keysPressed.remove(i);
				break;
			}
		}

		//Sends key release to the server
		if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
			this.game.getClient().sendKeyRelease("x");
		}

		if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
			this.game.getClient().sendKeyRelease("y");
		}

		//Triggers key pressed for smoother movement
		keyPressed(e, true);

	}

	/**
	 * keyPressed
	 * This method adds the key pressed to the arraylist and sends this information to the server
	 * @param KeyEvent - The key pressed
	 * @param boolean - True if this method was triggered by the keyrelease method, false if not
	 * @return void
	 */
	public void keyPressed(KeyEvent e, boolean release) {

		int key = e.getKeyCode();

		//This flag keeps track of whether the key pressed is a new key or not, to prevent sending the same message to the server, thus improving performance
		boolean alreadyPressed = true;

		//Algorithm to allow for 'overriding' of movement controls for a smoother feel
		if (!release) {
			key = e.getKeyCode();
			if (!keysPressed.contains(key)) {
				keysPressed.add(key);
				alreadyPressed = false;
			}
		}

		//Sends the last key pressed down by player to the server to allow for "overriding" of movement commands for smoother and more responsive control
		//Pythagorean theorem used when player moves diagonally so the distance covered is equal no metter which way they move
		if ((!alreadyPressed || release)
				&& (keysPressed.lastIndexOf(KeyEvent.VK_LEFT)) > (keysPressed.lastIndexOf(KeyEvent.VK_RIGHT))
				&& (keysPressed.lastIndexOf(KeyEvent.VK_UP)) > (keysPressed.lastIndexOf(KeyEvent.VK_DOWN))) {
			this.game.getClient()
					.sendKeyPress("x|" + String.valueOf((float) (-(Math.sqrt(Math.pow(this.getSpeed(), 2) / 2)))));
			this.game.getClient()
					.sendKeyPress("y|" + String.valueOf((float) (-(Math.sqrt(Math.pow(this.getSpeed(), 2) / 2)))));
		} else if ((!alreadyPressed || release)
				&& (keysPressed.lastIndexOf(KeyEvent.VK_LEFT)) > (keysPressed.lastIndexOf(KeyEvent.VK_RIGHT))
				&& (keysPressed.lastIndexOf(KeyEvent.VK_DOWN)) > (keysPressed.lastIndexOf(KeyEvent.VK_UP))) {
			this.game.getClient()
					.sendKeyPress("x|" + String.valueOf((float) (-(Math.sqrt(Math.pow(this.getSpeed(), 2) / 2)))));
			this.game.getClient()
					.sendKeyPress("y|" + String.valueOf((float) ((Math.sqrt(Math.pow(this.getSpeed(), 2) / 2)))));
		} else if ((!alreadyPressed || release)
				&& (keysPressed.lastIndexOf(KeyEvent.VK_RIGHT)) > (keysPressed.lastIndexOf(KeyEvent.VK_LEFT))
				&& (keysPressed.lastIndexOf(KeyEvent.VK_UP)) > (keysPressed.lastIndexOf(KeyEvent.VK_DOWN))) {
			this.game.getClient()
					.sendKeyPress("x|" + String.valueOf((float) ((Math.sqrt(Math.pow(this.getSpeed(), 2) / 2)))));
			this.game.getClient()
					.sendKeyPress("y|" + String.valueOf((float) (-(Math.sqrt(Math.pow(this.getSpeed(), 2) / 2)))));
		} else if ((!alreadyPressed || release)
				&& (keysPressed.lastIndexOf(KeyEvent.VK_RIGHT)) > (keysPressed.lastIndexOf(KeyEvent.VK_LEFT))
				&& (keysPressed.lastIndexOf(KeyEvent.VK_DOWN)) > (keysPressed.lastIndexOf(KeyEvent.VK_UP))) {
			this.game.getClient()
					.sendKeyPress("x|" + String.valueOf((float) ((Math.sqrt(Math.pow(this.getSpeed(), 2) / 2)))));
			this.game.getClient()
					.sendKeyPress("y|" + String.valueOf((float) ((Math.sqrt(Math.pow(this.getSpeed(), 2) / 2)))));
		} else if ((!alreadyPressed || release)
				&& keysPressed.lastIndexOf(KeyEvent.VK_LEFT) > keysPressed.lastIndexOf(KeyEvent.VK_RIGHT)) {
			this.game.getClient().sendKeyPress("x|" + String.valueOf(-this.getSpeed()));
		} else if ((!alreadyPressed || release)
				&& keysPressed.lastIndexOf(KeyEvent.VK_LEFT) < keysPressed.lastIndexOf(KeyEvent.VK_RIGHT)) {
			this.game.getClient().sendKeyPress("x|" + String.valueOf(this.getSpeed()));
		} else if ((!alreadyPressed || release)
				&& keysPressed.lastIndexOf(KeyEvent.VK_UP) > keysPressed.lastIndexOf(KeyEvent.VK_DOWN)) {
			this.game.getClient().sendKeyPress("y|" + String.valueOf(-this.getSpeed()));
		} else if ((!alreadyPressed || release)
				&& keysPressed.lastIndexOf(KeyEvent.VK_UP) < keysPressed.lastIndexOf(KeyEvent.VK_DOWN)) {
			this.game.getClient().sendKeyPress("y|" + String.valueOf(this.getSpeed()));
		}
	}

	/**
	 * mouseMoved
	 * This method takes in the x y of the cursor and calculates the angle in radians between tank and cursor relative to x-axis which is then send to the server
	 * @param int - The x coordinate of the cursor
	 * @param int - The y coordinate of the cursor
	 * @return void
	 */
	public void mouseMoved(int x, int y) {
		double radians = Math.atan2(((y - (this.getY() + (this.getTankBody().getHeight() / 2)))),
				((x - (this.getX() + (this.getTankBody().getHeight() / 2)))));
		this.game.getClient().sendRadians(radians);
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public BufferedImage getTankBody() {
		return tankBody;
	}

	public void setTankBody(BufferedImage img) {
		this.tankBody = img;
	}

	public BufferedImage getTankHead() {
		return tankHead;
	}

	public void setTankHead(BufferedImage img) {
		this.tankHead = img;
	}

	public ArrayList<Integer> getKeysPressed() {
		return keysPressed;
	}

	public void setKeysPressed(ArrayList<Integer> keysPressed) {
		this.keysPressed = keysPressed;
	}

	public boolean isShootStun() {
		return shootStun;
	}

	public void setShootStun(boolean shootStun) {
		this.shootStun = shootStun;
	}

	public double getMouseRadians() {
		return mouseRadians;
	}

	public void setMouseRadians(double radians) {
		this.mouseRadians = radians;
	}

	public GameArena getGame() {
		return game;
	}

	public void setGame(GameArena game) {
		this.game = game;
	}

	public boolean isSlain() {
		return slain;
	}

	public void setSlain(boolean slain) {
		this.slain = slain;
	}

}
