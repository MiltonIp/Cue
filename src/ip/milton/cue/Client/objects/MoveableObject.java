/**
 * @class_name MoveableObject
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This is an abstract class to be extended by objects that can move
 */

package ip.milton.cue.objects;

public abstract class MoveableObject {
	
	//Variable Declaration
	private int x, y;
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

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

}
