/**
 * @interface_name Collision
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * This interface contains a method for checking and resolving wall collisions
 */

package ip.milton.cue.server.objects;

public interface Collision {
	public void wallCollide(int minX, int minY, int maxX, int maxY);
}