package dev.jorel.commandapi;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.jorel.commandapi.executors.IExecutorNormal;

public abstract class BukkitExecutable extends Executable<BukkitExecutable, CommandSender> {

	public BukkitExecutable executesPlayer(IExecutorNormal<Player> executor) {
		// TODO Auto-generated method stub
		return null;
	}

}
