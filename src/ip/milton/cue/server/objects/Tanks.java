/**
 * @class_name Tanks
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This is a class that models a tank
 */

package ip.milton.cue.server.objects;

import ip.milton.cue.server.execution.ServerMain;
import ip.milton.cue.server.world.GUI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Tanks extends MoveableObject implements Moveable, Collision {
	//Variable declaration
	private int player;
	private Thread thread;
	private BufferedImage tankBody;
	private BufferedImage tankHead;

	private boolean slain;

	private double mouseRadians;

	private boolean leftPress, rightPress, upPress, downPress;

	private GUI gui;

	public Tanks(int player, GUI gui) {
		//Initializing values
		this.gui = gui;
		this.player = player;
		final int defaultSpeed = 10;
		this.setSpeed(defaultSpeed);

		//Loading values from tank sprites
		try {
			if (player == 1) {
				this.tankBody = ImageIO.read(new File("Images/tankBody1New.png"));
				this.tankHead = ImageIO.read(new File("Images/tankHead1New.png"));
				this.setX(0);
				this.setY((int) ((this.gui.getHeight() / 2) - (this.tankBody.getHeight() / 2)));

			} else if (player == 2) {
				this.tankBody = ImageIO.read(new File("Images/tankBody2New.png"));
				this.tankHead = ImageIO.read(new File("Images/tankHead2New.png"));
				this.setX((int) ((this.gui.getWidth()) - (this.tankBody.getWidth())));
				this.setY((int) ((this.gui.getHeight() / 2) - (this.tankBody.getHeight() / 2)));
			}

		} catch (Exception e) {
			GUI.setDisplayMessage("Tank sprites not found, please exit, fix path and try again");
		}

	}

	/**
	 * wallCollide
	 * This method checks and resolves for collisions between the tank and the walls 
	 * @param int - Minimum x position the ball can be
	 * @param int - Minimum y position the ball can be
	 * @param int - Maximum x position the ball can be
	 * @param int - Maximum y position the ball can be
	 * @return void
	 */
	@Override
	public void wallCollide(int minX, int minY, int maxX, int maxY) {
		//If the tank collides with a wall, set the tank's position to stay within the boundaries
		if (this.getX() > (maxX - this.getTankBody().getWidth())) {
			this.setX(maxX - (this.getTankBody().getWidth()));
		}

		if (this.getX() < minX) {
			this.setX(minX);
		}

		if (this.getY() > (maxY - this.getTankBody().getHeight())) {
			this.setY(maxY - (this.getTankBody().getHeight()));
		}

		if (this.getY() < minY) {
			this.setY(minY);
		}
	}

	/**
	 * move
	 * This method updates the tank's x y position according to it's x y velocity
	 * @param null
	 * @return void
	 */
	@Override
	public void move() {
		this.setX((int) (this.getX() + this.getxVelocity()));
		this.setY((int) (this.getY() + this.getyVelocity()));
	}

	/**
	 * orbCollide
	 * This method checks and resolves for collisions between a tank and orbs
	 * @param Tanks - The tank being checked for collisions
	 * @param ArrayList<Ball> - List of orbs to check against the tank
	 * @return boolean - True if collision, false if not
	 */
	public boolean orbCollide(Tanks tank, ArrayList<Ball> ballList) {
		synchronized (ServerMain.getLocka()) {
			for (Ball ball : ballList) {
				if (ball instanceof Orbs) {
					float orbRadius = ((Orbs) ball).getRadius();
					//Preliminary check to see if tank is close to an orb, if not, can just not check to save resources
					if (tank.getX() < (ball.getX() + ball.getBodyImg().getWidth())
							&& tank.getX() + tank.getTankBody().getWidth() > ball.getX()
							&& tank.getY() < ball.getY() + ball.getBodyImg().getHeight()
							&& tank.getY() + tank.getTankBody().getHeight() > ball.getY()) {
						//Checks the distance between the orb's center and the tank, if distance is less than or equal to the orb's radius, a collision has occurred
						if (Math.pow(tank.getX() - ball.getCenter().x, 2) + Math.pow(tank.getY() - ball.getCenter().y, 2) <= 
								Math.pow(orbRadius, 2)
								|| Math.pow(tank.getX() + tank.getTankBody().getWidth() - ball.getCenter().x, 2) + Math.pow(tank.getY() - ball.getCenter().y,2) <= 
								Math.pow(orbRadius, 2)
								|| Math.pow(tank.getX() - ball.getCenter().x, 2) + Math.pow(tank.getY() + tank.getTankBody().getHeight() - ball.getCenter().y, 2) <=
								Math.pow(orbRadius, 2)
								|| Math.pow(tank.getX() + tank.getTankBody().getWidth() - ball.getCenter().x, 2) + Math.pow(tank.getY() + tank.getTankBody().getHeight() - ball.getCenter().y, 2) <= 
								Math.pow(orbRadius, 2)
								|| Math.pow(tank.getX() + tank.getTankBody().getWidth() / 2 - ball.getCenter().x, 2) + Math.pow(tank.getY() - ball.getCenter().y,2) <= 
								Math.pow(orbRadius, 2)
								|| Math.pow(tank.getX() + tank.getTankBody().getWidth() - ball.getCenter().x, 2) + Math.pow(tank.getY() + tank.getTankBody().getHeight() / 2 - ball.getCenter().y, 2) <= 
								Math.pow(orbRadius, 2)
								|| Math.pow(tank.getX() + tank.getTankBody().getWidth() / 2 - ball.getCenter().x, 2) + Math.pow(tank.getY() + tank.getTankBody().getHeight() - ball.getCenter().y, 2) <= 
								Math.pow(orbRadius, 2)
								|| Math.pow(tank.getX() - ball.getCenter().x, 2) + Math.pow(tank.getY() + tank.getTankBody().getHeight() / 2 - ball.getCenter().y, 2) <= 
								Math.pow(orbRadius, 2)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int player) {
		this.player = player;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
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

	public double getMouseRadians() {
		return mouseRadians;
	}

	public void setMouseRadians(double radians) {
		this.mouseRadians = radians;
	}

	public boolean isLeftPress() {
		return leftPress;
	}

	public void setLeftPress(boolean leftPress) {
		this.leftPress = leftPress;
	}

	public boolean isRightPress() {
		return rightPress;
	}

	public void setRightPress(boolean rightPress) {
		this.rightPress = rightPress;
	}

	public boolean isUpPress() {
		return upPress;
	}

	public void setUpPress(boolean upPress) {
		this.upPress = upPress;
	}

	public boolean isDownPress() {
		return downPress;
	}

	public void setDownPress(boolean downPress) {
		this.downPress = downPress;
	}

	public GUI getGui() {
		return gui;
	}

	public void setGui(GUI gui) {
		this.gui = gui;
	}

	public boolean isSlain() {
		return slain;
	}

	public void setSlain(boolean slain) {
		this.slain = slain;
	}

}
