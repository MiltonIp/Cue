/**
 * @class_name Cannonballs
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This class models a cannonball
 */

package ip.milton.cue.server.objects;

import java.awt.Point;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import ip.milton.cue.server.execution.ServerMain;
import ip.milton.cue.server.world.GUI;

public class Cannonballs extends Ball {

	//Variable declaration
	private float radius;

	private boolean collided;

	public Cannonballs(int x, int y, float speed, float xVelocity, float yVelocity, double radians) {
		super(x, y, speed, xVelocity, yVelocity, radians);

		//Initializing variables
		final int C_BALL_MASS = 5;

		this.setMass(C_BALL_MASS);

		this.collided = false;

		//Loading values from picture
		try {
			this.setBodyImg(ImageIO.read(new File("Images/CannonballNew.png")));
			this.radius = (float) (this.getBodyImg().getWidth() / 2.0);
		} catch (IOException e) {
			GUI.setDisplayMessage("Cannonball sprites not found, exit, check paths and try again");
			GUI.getMainPanel().repaint();
			ServerMain.setRunThreads(false);
		}
		
		this.setCenter(new Point(this.getX() + this.getBodyImg().getWidth() / 2,
				this.getY() + this.getBodyImg().getHeight() / 2));
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public boolean isCollided() {
		return collided;
	}

	public void setCollided(boolean collided) {
		this.collided = collided;
	}

}
