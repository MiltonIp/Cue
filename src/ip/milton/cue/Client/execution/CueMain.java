/**
 * @class_name CueMain
 * @version Final
 * @author Milton Ip
 * @date 01/23/17
 * Entry point for program
 */

package ip.milton.cue.execution;

import ip.milton.cue.world.GUI;

import java.awt.EventQueue;

public class CueMain {

	//Variable Declarations
	private static boolean runThreads;
	
	//Locks for synchronization statements to prevent multi-threading issues
	private static final Object lockA = new Object();
	private static final Object lockB = new Object();
	
	/**
	 * main
	 * This is the main method of the Cue program (client-side)
	 * @param String array - An array of command line arguments
	 * @return void
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				setRunThreads(true);
				new GUI();
			}
		});
	}

	public static boolean isRunThreads() {
		return runThreads;
	}

	public static void setRunThreads(boolean runThreads) {
		CueMain.runThreads = runThreads;
	}

	public static Object getLocka() {
		return lockA;
	}

	public static Object getLockb() {
		return lockB;
	}


}
