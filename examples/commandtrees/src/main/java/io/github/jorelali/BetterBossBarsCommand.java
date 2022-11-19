package io.github.jorelali;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.ChatComponentArgument;
import dev.jorel.commandapi.arguments.EntitySelector;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.NamespacedKeyArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

public class BetterBossBarsCommand {
	
	// /betterbossbar set <id> players <targets>
	// /betterbossbar set <id> style (notched_6|notched_10|notched_12|notched_20|progress)
	// /betterbossbar set <id> value <value>
	// /betterbossbar set <id> visible <visible>
	
	// /betterbossbar remove <id>
	
	// /betterbossbar get <id> players
	// /betterbossbar get <id> visible
	// /betterbossbar get <id> max
	// /betterbossbar get <id> value
	
	// /betterbossbar add <id> <name>
	
	// /betterbossbar list
	
	private Map<NamespacedKey, Integer> maxValues;
	
	public BetterBossBarsCommand() {
		this.maxValues = new HashMap<>();
	}

	public void registerBetterBossBarCommand() {
		new CommandTree("betterbossbar")
			.withPermission(CommandPermission.OP)
			.then(new LiteralArgument("set")
				.then(new NamespacedKeyArgument("id")
					.then(new LiteralArgument("players")
						.then(new EntitySelectorArgument<Collection<Player>>("targets", EntitySelector.MANY_PLAYERS)
							.executes(this::setPlayers)
						)
					)
					.then(new LiteralArgument("style")
						.then(new MultiLiteralArgument("notched_6", "notched_10", "notched_12", "notched_20", "progress")
							.executes(this::setStyle)
						)
					)
					.then(new LiteralArgument("value")
						.then(new IntegerArgument("value")
							.executes(this::setValue)
						)
					)
					.then(new LiteralArgument("visible")
						.then(new BooleanArgument("visible")
							.executes(this::setVisible)
						)
					)
				)
			)
			.then(new LiteralArgument("remove")
				.then(new NamespacedKeyArgument("id")
					.executes(this::removeBossbar)
				)
			)
			.then(new LiteralArgument("get")
				.then(new NamespacedKeyArgument("id")
					.then(new LiteralArgument("players")
						.executes(this::getPlayers)
					)
					.then(new LiteralArgument("visible")
						.executes(this::getVisible)
					)
					.then(new LiteralArgument("max")
						.executes(this::getMax)
					)
					.then(new LiteralArgument("value")
						.executes(this::getValue)
					)
				)
			)
			.then(new LiteralArgument("add")
				.then(new NamespacedKeyArgument("id")
					.then(new ChatComponentArgument("name")
						.executes(this::addBossbar)
					)
				)
			)
			.then(new LiteralArgument("list")
				.executes(this::list)
			)
			.register();
	}

	private void setPlayers(CommandSender sender, Object[] args) {
		NamespacedKey id = (NamespacedKey) args[0];

		@SuppressWarnings("unchecked")
		Collection<Player> targets = (Collection<Player>) args[1];

		Bukkit.getBossBar(id).removeAll();
		for (Player player : targets) {
			Bukkit.getBossBar(id).addPlayer(player);
		}
	}

	private void setStyle(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException {
		NamespacedKey id = (NamespacedKey) args[0];
		String style = (String) args[1];

		Bukkit.getBossBar(id).setStyle(
			switch (style) {
				case "notched_6" -> BarStyle.SEGMENTED_6;
				case "notched_10" -> BarStyle.SEGMENTED_10;
				case "notched_12" -> BarStyle.SEGMENTED_12;
				case "notched_20" -> BarStyle.SEGMENTED_20;
				case "progress" -> BarStyle.SOLID;
				default -> throw CommandAPI.fail(style + " is an invalid bossbar style");
			});
	}

	private void setValue(CommandSender sender, Object[] args) {
		NamespacedKey id = (NamespacedKey) args[0];
		int value = (int) args[1];

		if (this.maxValues.containsKey(id)) {
			Bukkit.getBossBar(id).setProgress((double) value / (double) this.maxValues.get(id));
		} else {
			this.maxValues.put(id, value);
			Bukkit.getBossBar(id).setProgress(1.0D);
		}
	}

	private void setVisible(CommandSender sender, Object[] args) {
		NamespacedKey id = (NamespacedKey) args[0];
		boolean visible = (boolean) args[1];

		Bukkit.getBossBar(id).setVisible(visible);
	}

	private void removeBossbar(CommandSender sender, Object[] args) {
		NamespacedKey id = (NamespacedKey) args[0];

		sender.sendMessage("Removed custom bossbar [" + Bukkit.getBossBar(id).getTitle() + "]");
		Bukkit.getBossBar(id).removeAll();
		Bukkit.removeBossBar(id);
	}

	private void getPlayers(CommandSender sender, Object[] args) {
		NamespacedKey id = (NamespacedKey) args[0];
		List<Player> bossBarPlayers = Bukkit.getBossBar(id).getPlayers();
		String players = bossBarPlayers.stream().map(Player::getName).collect(Collectors.joining(", "));
		sender.sendMessage("Custom bossbar [" + Bukkit.getBossBar(id).getTitle() + "] has " + bossBarPlayers.size() + " players currently online: " + players);
	}

	private void getVisible(CommandSender sender, Object[] args) {
		NamespacedKey id = (NamespacedKey) args[0];
		BossBar bossBar = Bukkit.getBossBar(id);
		sender.sendMessage("Custom bossbar [" + bossBar.getTitle() + "] is currently " + (bossBar.isVisible() ? "shown" : "hidden"));
	}

	private void getMax(CommandSender sender, Object[] args) {
		NamespacedKey id = (NamespacedKey) args[0];
		sender.sendMessage("Custom bossbar [" + Bukkit.getBossBar(id).getTitle() + "] has a maximum of " + this.maxValues.getOrDefault(id, 100));
	}

	private void getValue(CommandSender sender, Object[] args) {
		NamespacedKey id = (NamespacedKey) args[0];
		BossBar bossBar = Bukkit.getBossBar(id);
		
		int value = (int) (bossBar.getProgress() * this.maxValues.getOrDefault(id, 100));
		sender.sendMessage("Custom bossbar [" + Bukkit.getBossBar(id).getTitle() + "] has a value of " + value);
	}

	private void addBossbar(CommandSender sender, Object[] args) {
		NamespacedKey id = (NamespacedKey) args[0];
		BaseComponent[] name = (BaseComponent[]) args[1];

		Bukkit.createBossBar(id, BaseComponent.toLegacyText(name), BarColor.WHITE, BarStyle.SOLID);
		this.maxValues.put(id, 100);
		sender.sendMessage("Created custom bossbar [" + BaseComponent.toLegacyText(name) + ChatColor.WHITE + "]");
	}

	private void list(CommandSender sender, Object[] args) {
		Iterable<KeyedBossBar> bossBars = () -> Bukkit.getBossBars();
		sender.sendMessage("List of custom bossbars: " +
			StreamSupport
				.stream(bossBars.spliterator(), false)
				.map(KeyedBossBar::getKey)
				.map(NamespacedKey::toString)
				.collect(Collectors.joining(", "))
		);
	}

}
