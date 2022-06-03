/*******************************************************************************
 * Copyright 2018, 2021 Jorel Ali (Skepter) - MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package dev.jorel.commandapi;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;

/**
 * Class to register commands with the 1.13 command UI
 *
 */
public abstract class CommandAPIBase {
	
	private static boolean canRegister = true;
	static File dispatcherFile;
	static Logger logger;
	private static boolean loaded = false;
	
	static File getDispatcherFile() {
		return dispatcherFile;
	}
	
	// TODO: Implement these
	public static void logWarning(String s) {};
	static void logInfo(String s) {};
	static void logNormal(String s) {};
	static void logError(String s) {};
	static Config getConfiguration() { return null; }
	
	/**
	 * Returns the CommandAPI's logger
	 * @return the CommandAPI's logger
	 */
	public static Logger getLog() {
		if(logger == null) {
			logger = new Logger("CommandAPI", null) {
				{
					// TODO: Logging. Just everything about logging needs redoing anyway
//					this.setParent(Bukkit.getServer().getLogger());
					this.setLevel(Level.ALL);
				}
				
				public void log(LogRecord logRecord) {
					logRecord.setMessage("[CommandAPI] " + logRecord.getMessage());
					super.log(logRecord);
				}
			};
		}
		return logger;
	}
	
	/**
	 * Forces a command to return a success value of 0
	 * @param message Description of the error message
	 * @throws WrapperCommandSyntaxException which indicates that there was a command failure
	 */
	public static WrapperCommandSyntaxException fail(String message) throws WrapperCommandSyntaxException {
		return new WrapperCommandSyntaxException(new SimpleCommandExceptionType(new LiteralMessage(message)).create());
	}

	/**
	 * Determines whether command registration is permitted via the CommandAPI
	 * @return true if commands can still be registered
	 */
	public static boolean canRegister() {
		return canRegister;
	}
	
	/**
	 * Unregisters a command
	 * @param command the name of the command to unregister
	 */
	public static void unregister(String command) {
		CommandAPIHandler.getInstance().unregister(command, false);
	}
	
	/**
	 * Unregisters a command, by force (removes all instances of that command)
	 * 
	 * @param command the name of the command to unregister
	 * @param force   if true, attempt to unregister all instances of the command
	 *                across all plugins as well as minecraft, bukkit and spigot
	 */
	public static void unregister(String command, boolean force) {
		if(!canRegister()) {
			getLog().warning("Unexpected unregistering of /" + command + ", as server is loaded! Unregistering anyway, but this can lead to unstable results!");
		}
		CommandAPIHandler.getInstance().unregister(command, force);
	}

	/**
	 * Registers a command. Used with the CommandAPI's Annotation API.
	 * @param commandClass the class to register
	 */
	public static void registerCommand(Class<?> commandClass) {
		try {
			Class.forName(commandClass.getName() + "$Command").getDeclaredMethod("register").invoke(null);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}
}
