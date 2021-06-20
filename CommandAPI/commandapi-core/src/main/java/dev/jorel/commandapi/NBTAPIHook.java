package dev.jorel.commandapi;

import de.tr7zw.nbtapi.NBTContainer;

public class NBTAPIHook {
	
	// Not instantiable
	private NBTAPIHook() {};
	
	public static boolean checkDependency() {
		try {
			Class.forName("de.tr7zw.nbtapi.NBTContainer");
			return true;
		} catch(ClassNotFoundException e) {
			try {
				new NBTContainer();
				return true;
			} catch(Exception f) {
				return false;
			}
		}
	}
	
	public static Object construct(Object object) {
		return new NBTContainer(object);
	}

}
