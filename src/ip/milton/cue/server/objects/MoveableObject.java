/**
 * @class_name MoveableObject
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This is an abstract class to be extended by objects that can move
 */

package ip.milton.cue.server.objects;

public abstract class MoveableObject {
	private int x, y; //Coordinates are ints so shorter messages can be sent from server to client (improves performance as well), 
						//I cast x y to int at very end of calculations, so no difference from having 
						//float/double x y and casting to int when drawing objects
	private float xVelocity, yVelocity;	
	private float speed;
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public float getxVelocity() {
		return xVelocity;
	}

	public void setxVelocity(float xVelocity) {
		this.xVelocity = xVelocity;
	}

	public float getyVelocity() {
		return yVelocity;
	}

	public void setyVelocity(float yVelocity) {
		this.yVelocity = yVelocity;
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
