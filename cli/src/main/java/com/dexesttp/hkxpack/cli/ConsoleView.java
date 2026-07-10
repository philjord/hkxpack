package com.dexesttp.hkxpack.cli;

import com.dexesttp.hkxpack.cli.commands.Command;
import com.dexesttp.hkxpack.cli.commands.CommandFactory;
import com.dexesttp.hkxpack.cli.commands.Command_help;

/**
 * Entry point for the Command Line Interface.
 */
public final class ConsoleView {
	private static final int MINIMUM_ARG_COUNT = 1;

	private ConsoleView() {
		// NO OP
	}

	/**
	 * Entry point for the console
	 * 
	 * @param args the console arguments.
	 */
	public static void main(final String... args2) {
		
		System.out.println("This is a nonsense cli wrapper, you must edit the command in  com.dexesttp.hkxpack.cli.ConsoleView then run the code");
		
		//String[] args = new String[] {"unpack","D:\\temp\\hkx\\aggrowarning1.hkx"};
		
		//String[] args = new String[] {"unpack","C:\\temp\\BOSBarricadeBase01-bhkPhysicsSystem_3.hkx"};
		String[] args = new String[] {"unpack","C:\\temp\\ese\\skeleton-bhkPhysicsSystem_165.hkx"};
		//Meshes\SetDressing\Signage\Billboard01Tall.nif
		//Meshes\Vehicles\Automotive\VaultTecVan01HulkStatic.nif
		
		System.out.println("args[0] " + args[0]);
		System.out.println("args[1] " + args[1]);
		
		// Set the logging properties
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$s] %5$s%n");
		Command command;
		if (args.length < MINIMUM_ARG_COUNT) {
			command = new Command_help();
		} else {
			command = new CommandFactory().newInstance(args[0]);
		}
		command.execute(args);
		
		System.out.println("Finished, no message probably means success");
	}
}
