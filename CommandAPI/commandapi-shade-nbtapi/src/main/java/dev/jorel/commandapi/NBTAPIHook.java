package dev.jorel.commandapi;

import de.tr7zw.changeme.nbtapi.NBTContainer;

public class NBTAPIHook {
	
	// Not instantiable
	private NBTAPIHook() {};
	
	public static boolean checkDependency() {
		try {
			new NBTContainer();
			return true;
		} catch(Exception f) {
			return false;
		}
	}
	
	public static Object construct(Object object) {
		return new NBTContainer(object);
	}

}
