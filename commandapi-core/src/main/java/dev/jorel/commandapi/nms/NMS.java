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
package dev.jorel.commandapi.nms;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.Axis;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffectType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import de.tr7zw.nbtapi.NBTContainer;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.EntitySelector;
import dev.jorel.commandapi.arguments.SuggestionProviders;
import dev.jorel.commandapi.wrappers.FloatRange;
import dev.jorel.commandapi.wrappers.FunctionWrapper;
import dev.jorel.commandapi.wrappers.IntegerRange;
import dev.jorel.commandapi.wrappers.Location2D;
import dev.jorel.commandapi.wrappers.MathOperation;
import dev.jorel.commandapi.wrappers.ParticleData;
import dev.jorel.commandapi.wrappers.Rotation;
import dev.jorel.commandapi.wrappers.ScoreboardSlot;
import dev.jorel.commandapi.wrappers.SimpleFunctionWrapper;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;

public interface NMS<CommandListenerWrapper> {

	/* Argument types */
	public default ArgumentType<?> _ArgumentAngle() {
		return null;
	};

	public default ArgumentType<?> _ArgumentAxis() {
		return null;
	};

	public default ArgumentType<?> _ArgumentBlockPredicate() {
		return null;
	};

	public default ArgumentType<?> _ArgumentBlockState() {
		return null;
	};

	public default ArgumentType<?> _ArgumentChat() {
		return null;
	};

	public default ArgumentType<?> _ArgumentChatComponent() {
		return null;
	};

	public default ArgumentType<?> _ArgumentChatFormat() {
		return null;
	};

	public default ArgumentType<?> _ArgumentDimension() {
		return null;
	};

	public default ArgumentType<?> _ArgumentEnchantment() {
		return null;
	};

	public default ArgumentType<?> _ArgumentEntity(EntitySelector selector) {
		return null;
	};

	public default ArgumentType<?> _ArgumentEntitySummon() {
		return null;
	};

	public default ArgumentType<?> _ArgumentFloatRange() {
		return null;
	};

	public default ArgumentType<?> _ArgumentIntRange() {
		return null;
	};

	public default ArgumentType<?> _ArgumentItemPredicate() {
		return null;
	};

	public default ArgumentType<?> _ArgumentItemStack() {
		return null;
	};

	public default ArgumentType<?> _ArgumentMathOperation() {
		return null;
	};

	public default ArgumentType<?> _ArgumentMinecraftKeyRegistered() {
		return null;
	};

	public default ArgumentType<?> _ArgumentMobEffect() {
		return null;
	};

	public default ArgumentType<?> _ArgumentNBTCompound() {
		return null;
	};

	public default ArgumentType<?> _ArgumentParticle() {
		return null;
	};

	public default ArgumentType<?> _ArgumentPosition() {
		return null;
	};

	public default ArgumentType<?> _ArgumentPosition2D() {
		return null;
	};

	public default ArgumentType<?> _ArgumentProfile() {
		return null;
	};

	public default ArgumentType<?> _ArgumentRotation() {
		return null;
	};

	public default ArgumentType<?> _ArgumentScoreboardCriteria() {
		return null;
	};

	public default ArgumentType<?> _ArgumentScoreboardObjective() {
		return null;
	};

	public default ArgumentType<?> _ArgumentScoreboardSlot() {
		return null;
	};

	public default ArgumentType<?> _ArgumentScoreboardTeam() {
		return null;
	};

	public default ArgumentType<?> _ArgumentScoreholder(boolean single) {
		return null;
	};

	public default ArgumentType<?> _ArgumentTag() {
		return null;
	};

	public default ArgumentType<?> _ArgumentTime() {
		return null;
	};

	public default ArgumentType<?> _ArgumentUUID() {
		return null;
	};

	public default ArgumentType<?> _ArgumentVec2() {
		return null;
	};

	public default ArgumentType<?> _ArgumentVec3() {
		return null;
	};

	/*
	 * Synthetic arguments - arguments that don't actually exist, but have
	 * version-specific implementations, so we can switch their implementation as
	 * needed. For example, the BiomeArgument is both a
	 * _ArgumentMinecraftKeyRegistered and a _ArgumentResourceOrTag, but we'll refer
	 * to it as an _ArgumentSyntheticBiome
	 */

	public default ArgumentType<?> _ArgumentSyntheticBiome() {
		return null;
	};

	/**
	 * A String array of Minecraft versions that this NMS implementation is
	 * compatible with. For example, ["1.14", "1.14.1", "1.14.2", "1.14.3"]. This
	 * can be found by opening a Minecraft jar file, viewing the version.json file
	 * and reading the object "name".
	 * 
	 * @return A String array of compatible Minecraft versions
	 */
	public default String[] compatibleVersions() {
		return null;
	};

	public default String convert(ItemStack is) {
		return null;
	};

	public default String convert(ParticleData<?> particle) {
		return null;
	};

	public default String convert(PotionEffectType potion) {
		return null;
	};

	public default String convert(Sound sound) {
		return null;
	};

	/**
	 * Creates a JSON file that describes the hierarchical structure of the commands
	 * that have been registered by the server.
	 * 
	 * @param file       The JSON file to write to
	 * @param dispatcher The Brigadier CommandDispatcher
	 * @throws IOException When the file fails to be written to
	 */
	public default void createDispatcherFile(File file, CommandDispatcher<CommandListenerWrapper> dispatcher)
			throws IOException {
	};

	public default Advancement getAdvancement(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default Component getAdventureChat(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default Component getAdventureChatComponent(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default float getAngle(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return 0f;
	};

	public default EnumSet<Axis> getAxis(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default Biome getBiome(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default Predicate<Block> getBlockPredicate(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default BlockData getBlockState(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	/**
	 * Returns the Brigadier CommandDispatcher from the NMS CommandDispatcher
	 * 
	 * @return A Brigadier CommandDispatcher
	 */
	public default CommandDispatcher<CommandListenerWrapper> getBrigadierDispatcher() {
		return null;
	};

	public default BaseComponent[] getChat(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default ChatColor getChatColor(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default BaseComponent[] getChatComponent(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	/**
	 * Converts a CommandSender into a CLW
	 * 
	 * @param sender the command sender to convert
	 * @return a CLW.
	 */
	public default CommandListenerWrapper getCLWFromCommandSender(CommandSender sender) {
		return null;
	};

	/**
	 * Returns a CommandSender of a given CommandListenerWrapper object
	 * 
	 * @param clw The CommandListenerWrapper object
	 * @return A CommandSender (not proxied) from the command listener wrapper
	 */
	public default CommandSender getCommandSenderFromCSS(CommandListenerWrapper clw) {
		return null;
	};

	public default Environment getDimension(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default Enchantment getEnchantment(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default Object getEntitySelector(CommandContext<CommandListenerWrapper> cmdCtx, String key,
			EntitySelector selector) throws CommandSyntaxException {
		return null;
	};

	public default EntityType getEntityType(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default FloatRange getFloatRange(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default FunctionWrapper[] getFunction(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default SimpleFunctionWrapper getFunction(NamespacedKey key) {
		return null;
	};

	public default Set<NamespacedKey> getFunctions() {
		return null;
	};

	public default IntegerRange getIntRange(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default ItemStack getItemStack(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default Predicate<ItemStack> getItemStackPredicate(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default String getKeyedAsString(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default Location2D getLocation2DBlock(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default Location2D getLocation2DPrecise(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default Location getLocationBlock(CommandContext<CommandListenerWrapper> cmdCtx, String str)
			throws CommandSyntaxException {
		return null;
	};

	public default Location getLocationPrecise(CommandContext<CommandListenerWrapper> cmdCtx, String str)
			throws CommandSyntaxException {
		return null;
	};

	public default LootTable getLootTable(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default MathOperation getMathOperation(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default NBTContainer getNBTCompound(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default String getObjective(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws IllegalArgumentException, CommandSyntaxException {
		return null;
	};

	public default String getObjectiveCriteria(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default ParticleData<?> getParticle(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default Player getPlayer(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default OfflinePlayer getOfflinePlayer(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default PotionEffectType getPotionEffect(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default Recipe getRecipe(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default Rotation getRotation(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default ScoreboardSlot getScoreboardSlot(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default Collection<String> getScoreHolderMultiple(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default String getScoreHolderSingle(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	/**
	 * Retrieves a CommandSender, given some CommandContext. This method should
	 * handle Proxied CommandSenders for entities if a Proxy is being used.
	 * 
	 * @param cmdCtx      The
	 *                    <code>CommandContext&lt { return null; }; public defaultCommandListenerWrapper&gt { return null; }; public default</code>
	 *                    for a given command
	 * @param forceNative whether or not the CommandSender should be a
	 *                    NativeProxyCommandSender or not
	 * @return A CommandSender instance (such as a ProxiedNativeCommandSender or
	 *         Player)
	 */
	public default CommandSender getSenderForCommand(CommandContext<CommandListenerWrapper> cmdCtx,
			boolean forceNative) {
		return null;
	};

	/**
	 * Returns the Server's internal (OBC) CommandMap
	 * 
	 * @return A SimpleCommandMap from the OBC server
	 */
	public default SimpleCommandMap getSimpleCommandMap() {
		return null;
	};

	public default Sound getSound(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	/**
	 * Retrieve a specific NMS implemented SuggestionProvider
	 * 
	 * @param provider The SuggestionProvider type to retrieve
	 * @return A SuggestionProvider that matches the SuggestionProviders input
	 */
	public default SuggestionProvider<CommandListenerWrapper> getSuggestionProvider(SuggestionProviders provider) {
		return null;
	};

	public default SimpleFunctionWrapper[] getTag(NamespacedKey key) {
		return null;
	};

	public default Set<NamespacedKey> getTags() {
		return null;
	};

	public default String getTeam(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		return null;
	};

	public default int getTime(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return 0;
	};

	public default UUID getUUID(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		return null;
	};

	public default World getWorldForCSS(CommandListenerWrapper clw) {
		return null;
	};

	/**
	 * Checks if a Command is an instance of the OBC VanillaCommandWrapper
	 * 
	 * @param command The Command to check
	 * @return true if Command is an instance of VanillaCommandWrapper
	 */
	public default boolean isVanillaCommandWrapper(Command command) {
		return false;
	};

	/**
	 * Reloads the datapacks by using the updated the commandDispatcher tree
	 */
	public default void reloadDataPacks() {
	};

	/**
	 * Resends the command dispatcher's set of commands to a player.
	 * 
	 * @param player the player to send the command graph packet to
	 */
	public default void resendPackets(Player player) {
	};

	public default HelpTopic generateHelpTopic(String commandName, String shortDescription, String fullDescription,
			String permission) {
		return null;
	};

	public default void addToHelpMap(Map<String, HelpTopic> helpTopicsToAdd) {
	};

}
