/**
 * @class_name Ball
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This abstract class models a ball
 */

package ip.milton.cue.server.objects;

import java.awt.Point;
import java.awt.image.BufferedImage;

public abstract class Ball extends MoveableObject implements Moveable, Collision {

	//Variable declaration
	private BufferedImage bodyImg;
	private double radians;
	private Point center;
	private int mass;

	public Ball(int x, int y, float speed, float xVelocity, float yVelocity, double radians) {
		//Initializing values
		this.setX(x);
		this.setY(y);
		this.setSpeed(speed);
		this.setxVelocity(xVelocity);
		this.setyVelocity(yVelocity);
		this.setRadians(radians);
	}

	/**
	 * move
	 * This method updates the ball's x y position according to it's x y velocity
	 * @param null
	 * @return void
	 */
	@Override
	public void move() {
		this.setX((int) (this.getX() + this.getxVelocity()));
		this.setY((int) (this.getY() + this.getyVelocity()));
		//Update the center of the ball as well
		this.getCenter().setLocation(this.getX() + this.getBodyImg().getWidth() / 2,
				this.getY() + this.getBodyImg().getHeight() / 2);
	}

	/**
	 * wallCollide
	 * This method checks and resolves for collisions between the ball and the walls
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

		//Making ball bounce off the wall if it collides with it
		if (this.getX() > (maxX - imageWidth)) {
			this.setxVelocity(-this.getxVelocity());
			this.setX(maxX - imageWidth);
		} else if (this.getX() < minX) {
			this.setxVelocity(-this.getxVelocity());
			this.setX(minX);
		}

		if (this.getY() > (maxY - imageHeight)) {
			this.setyVelocity(-this.getyVelocity());
			this.setY(maxY - imageHeight);
		} else if (this.getY() < minY) {
			this.setyVelocity(-this.getyVelocity());
			this.setY(minY);
		}
	}

	public double getRadians() {
		return radians;
	}

	public void setRadians(double radians) {
		this.radians = radians;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}
	
	public BufferedImage getBodyImg() {
		return bodyImg;
	}

	public void setBodyImg(BufferedImage body) {
		this.bodyImg = body;
	}

}
