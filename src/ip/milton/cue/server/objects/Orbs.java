/**
 * @class_name Orbs
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This is a class that models an orb
 */

package ip.milton.cue.server.objects;

import java.awt.Point;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import ip.milton.cue.server.execution.ServerMain;
import ip.milton.cue.server.world.GUI;

public class Orbs extends Ball {

	//Variable declaration
	private float radius;

	private int speedExponent;

	public Orbs(int x, int y, float speed, float xVelocity, float yVelocity, double radians) {
		super(x, y, speed, xVelocity, yVelocity, radians);

		//Initializing values
		final int ORB_MASS = 1;

		this.setMass(ORB_MASS);
		try {
			this.setBodyImg(ImageIO.read(new File("Images/OrbNew.png")));
			this.setRadius(this.getBodyImg().getWidth() / 2);
		} catch (IOException e) {
			GUI.setDisplayMessage("Orb sprites not found, exit, check paths and try again");
			GUI.getMainPanel().repaint();
			ServerMain.setRunThreads(false);
		}
		
		this.setCenter(new Point(this.getX() + this.getBodyImg().getWidth() / 2,
				this.getY() + this.getBodyImg().getHeight() / 2));

	}

	/**
	 * wallCollide
	 * This method checks and resolves for collisions between the ball and the walls and slows the orb's speed on impact with wall to slow game down
	 * @param int - Minimum x position the ball can be
	 * @param int - Minimum y position the ball can be
	 * @param int - Maximum x position the ball can be
	 * @param int - Maximum y position the ball can be
	 * @return void
	 */
	@Override
	public void wallCollide(int minX, int minY, int maxX, int maxY) {
		int imageHeight = this.getBodyImg().getHeight();
		int imageWidth = this.getBodyImg().getWidth();

		//Making orb bounce of walls if orb collsides, also reduces it's speed to 
		if (this.getX() > (maxX - imageWidth)) {
			this.setxVelocity((float) (-this.getxVelocity()));

			if (this.getxVelocity() > 1) {
				this.setxVelocity((float) (this.getxVelocity() * 0.5));
			}

			if (this.getyVelocity() > 1) {
				this.setyVelocity((float) (this.getyVelocity() * 0.5));
			}

			this.setX(maxX - imageWidth);
		} else if (this.getX() < minX) {
			this.setxVelocity((float) (-this.getxVelocity()));

			if (this.getxVelocity() > 1) {
				this.setxVelocity((float) (this.getxVelocity() * 0.5));
			}

			if (this.getyVelocity() > 1) {
				this.setyVelocity((float) (this.getyVelocity() * 0.5));
			}
			this.setX(minX);
		}

		if (this.getY() > (maxY - imageHeight)) {
			this.setyVelocity((float) (-this.getyVelocity()));

			if (this.getxVelocity() > 1) {
				this.setxVelocity((float) (this.getxVelocity() * 0.5));
			}

			if (this.getyVelocity() > 1) {
				this.setyVelocity((float) (this.getyVelocity() * 0.5));
			}
			this.setY(maxY - imageHeight);
		} else if (this.getY() < minY) {
			this.setyVelocity((float) (-this.getyVelocity()));

			if (this.getxVelocity() > 1) {
				this.setxVelocity((float) (this.getxVelocity() * 0.5));
			}

			if (this.getyVelocity() > 1) {
				this.setyVelocity((float) (this.getyVelocity() * 0.5));
			}
			this.setY(minY);
		}
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public int getSpeedExponent() {
		return speedExponent;
	}

	public void setSpeedExponent(int speedExponent) {
		this.speedExponent = speedExponent;
	}

}
