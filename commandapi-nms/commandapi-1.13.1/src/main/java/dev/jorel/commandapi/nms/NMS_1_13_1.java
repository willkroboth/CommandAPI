package dev.jorel.commandapi.nms;

import java.util.Collection;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.v1_13_R2.CraftLootTable;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftSound;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.help.SimpleHelpMap;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;

import dev.jorel.commandapi.arguments.SuggestionProviders;
import dev.jorel.commandapi.preprocessor.Differs;
import dev.jorel.commandapi.preprocessor.NMSMeta;
import dev.jorel.commandapi.preprocessor.RequireField;
import dev.jorel.commandapi.wrappers.Location2D;
import net.minecraft.server.v1_13_R2.Advancement;
import net.minecraft.server.v1_13_R2.ArgumentDimension;
import net.minecraft.server.v1_13_R2.ArgumentEntitySummon;
import net.minecraft.server.v1_13_R2.ArgumentMinecraftKeyRegistered;
import net.minecraft.server.v1_13_R2.ArgumentVec2I;
import net.minecraft.server.v1_13_R2.CommandListenerWrapper;
import net.minecraft.server.v1_13_R2.CompletionProviders;
import net.minecraft.server.v1_13_R2.CustomFunctionData;
import net.minecraft.server.v1_13_R2.DimensionManager;
import net.minecraft.server.v1_13_R2.EntitySelector;
import net.minecraft.server.v1_13_R2.IBlockData;
import net.minecraft.server.v1_13_R2.ICompletionProvider;
import net.minecraft.server.v1_13_R2.IRegistry;
import net.minecraft.server.v1_13_R2.ItemStack;
import net.minecraft.server.v1_13_R2.LootTableRegistry;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.ParticleParamBlock;
import net.minecraft.server.v1_13_R2.ParticleParamItem;

@NMSMeta(compatibleWith = "1.13.1")
@RequireField(in = CraftSound.class, name = "minecraftKey", ofType = String.class)
@RequireField(in = EntitySelector.class, name = "m", ofType = boolean.class)
@RequireField(in = LootTableRegistry.class, name = "e", ofType = Map.class)
@RequireField(in = SimpleHelpMap.class, name = "helpTopics", ofType = Map.class)
@RequireField(in = ParticleParamBlock.class, name = "c", ofType = IBlockData.class)
@RequireField(in = ParticleParamItem.class, name = "c", ofType = ItemStack.class)
//@RequireField(in = ParticleParamRedstone.class, name = "g", ofType = float.class)
public class NMS_1_13_1 implements NMS<CommandListenerWrapper> {

	private static final net.minecraft.server.v1_13_R2.MinecraftServer MINECRAFT_SERVER = ((CraftServer) Bukkit
			.getServer()).getServer();

	@Differs(from = "1.13", by = "Not throwing EnvironmentArgumentException")
	@Override
	public ArgumentType<?> _ArgumentDimension() {
		return ArgumentDimension.a();
	}

	@Differs(from = "1.13", by = "using ArgumentVec2I instead of ArgumentVec2")
	@Override
	public ArgumentType<?> _ArgumentPosition2D() {
		return ArgumentVec2I.a();
	}

	@Override
	public String[] compatibleVersions() {
		return new String[] { "1.13.1" };
	}

	@Differs(from = "1.13", by = "Uses IRegistry.MOB_EFFECT instead of MobEffectList.REGISTRY")
	@SuppressWarnings("deprecation")
	@Override
	public String convert(PotionEffectType potion) {
		return IRegistry.MOB_EFFECT.getKey(IRegistry.MOB_EFFECT.fromId(potion.getId())).toString();
	}

	@Differs(from = "1.13", by = "Implements getDimension for EnvironmentArgument")
	@Override
	public Environment getDimension(CommandContext<CommandListenerWrapper> cmdCtx, String key) {
		DimensionManager manager = ArgumentDimension.a(cmdCtx, key);
		return switch (manager.getDimensionID()) {
			case 0 -> Environment.NORMAL;
			case -1 -> Environment.NETHER;
			case 1 -> Environment.THE_END;
			default -> null;
		};
	}

	@Differs(from = "1.13", by = "uses IRegistry.ENTITY_TYPE instead of EntityTypes")
	@Override
	public EntityType getEntityType(CommandContext<CommandListenerWrapper> cmdCtx, String str)
			throws CommandSyntaxException {
		return IRegistry.ENTITY_TYPE.get(ArgumentEntitySummon.a(cmdCtx, str))
				.a(((CraftWorld) getWorldForCSS(cmdCtx.getSource())).getHandle()).getBukkitEntity().getType();
	}

	@Differs(from = "1.13", by = "uses ArgumentVec2I instead of ArgumentVec2")
	@Override
	public Location2D getLocation2DBlock(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException {
		ArgumentVec2I.a blockPos = ArgumentVec2I.a(cmdCtx, key);
		return new Location2D(getWorldForCSS(cmdCtx.getSource()), blockPos.a, blockPos.b);
	}

	@Differs(from = "1.13", by = "method name change: aP().a() -> getLootTableRegistry().getLootTable()")
	@Override
	public org.bukkit.loot.LootTable getLootTable(CommandContext<CommandListenerWrapper> cmdCtx, String str) {
		MinecraftKey minecraftKey = ArgumentMinecraftKeyRegistered.c(cmdCtx, str);
		return new CraftLootTable(fromMinecraftKey(minecraftKey),
				MINECRAFT_SERVER.getLootTableRegistry().getLootTable(minecraftKey));
	}

	@Differs(from = "1.13", by = "use of getLootTableRegistry() instead of .aP(). No use of ::iterator for advancements")
	@Override
	public SuggestionProvider<CommandListenerWrapper> getSuggestionProvider(SuggestionProviders provider) {
		return switch (provider) {
			case FUNCTION -> (context, builder) -> {
				CustomFunctionData functionData = MINECRAFT_SERVER.getFunctionData();
				ICompletionProvider.a(functionData.g().a(), builder, "#");
				return ICompletionProvider.a(functionData.c().keySet(), builder);
			};
			case RECIPES -> CompletionProviders.b;
			case SOUNDS -> CompletionProviders.c;
			case ADVANCEMENTS -> (cmdCtx, builder) -> {
				Collection<Advancement> advancements = MINECRAFT_SERVER.getAdvancementData().b();
				return ICompletionProvider.a(advancements.stream().map(Advancement::getName), builder);
			};
//			case LOOT_TABLES -> (context, builder) -> {
//				Map<MinecraftKey, LootTable> map = (Map<MinecraftKey, LootTable>) LootTableRegistry_e
//						.get(MINECRAFT_SERVER.getLootTableRegistry());
//				return ICompletionProvider.a(map.keySet(), builder);
//			};
			case ENTITIES -> CompletionProviders.d;
			default -> (context, builder) -> Suggestions.empty();
		};
	}

	public World getWorldForCSS(CommandListenerWrapper clw) {
		return (clw.getWorld() == null) ? null : clw.getWorld().getWorld();
	}

	private static NamespacedKey fromMinecraftKey(MinecraftKey key) {
		return new NamespacedKey(key.b(), key.getKey());
	}
}
