/**
 * @class_name ServerMain
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * Entry point for program
 */

package ip.milton.cue.server.execution;

import ip.milton.cue.server.world.GUI;
import ip.milton.cue.server.world.Server;

public class ServerMain {

	//Variable declaration
	private static boolean runThreads;
	
	//Lock for synchronization statements to prevent multi-threading issues
	private static final Object lockA = new Object();

	/**
	 * main
	 * This is the main method of the Cue program (server-side)
	 * @param String array - An array of command line arguments
	 * @return void
	 */
	public static void main(String[] args) {
		setRunThreads(true);
		new Server(new GUI());
	}

	public static boolean isRunThreads() {
		return runThreads;
	}

	public static void setRunThreads(boolean runThreads) {
		ServerMain.runThreads = runThreads;
	}

	public static Object getLocka() {
		return lockA;
	}

}
