package dev.jorel.commandapi.preprocessor;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;

import de.icongmbh.oss.maven.plugin.javassist.ClassTransformer;
import javassist.CannotCompileException;
import javassist.ClassMap;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.build.JavassistBuildException;

/**
 * Conceptually, it should be possible to use javassist to "dynamically generate"
 * the "similar" NMS for specific classes. For example, 1.13 and 1.13.1 share a
 * lot of very similar code, so it should be possible to generate a "template"
 * and then modify the imports post-compile time to what we want
 */
public class ProcessNMSMeta extends ClassTransformer {

	@Override
	public boolean shouldTransform(final CtClass candidateClass) throws JavassistBuildException {
		try {
			return candidateClass.getAnnotation(NMSMeta.class) != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Override
	public void applyTransformations(CtClass classToTransform) throws JavassistBuildException {
		try {
			// If we have a compatibleVersions method, trash it
			CtMethod toStringMethod = classToTransform.getDeclaredMethod("compatibleVersions");
			classToTransform.removeMethod(toStringMethod);
		} catch (NotFoundException e) {
		}

		String versions = "";
		try {
			NMSMeta source = (NMSMeta) classToTransform.getAnnotation(NMSMeta.class);
			versions = Arrays.stream(source.compatibleWith()).map(x -> "\"" + x + "\"").collect(Collectors.joining(", "));
		} catch (ClassNotFoundException e) {
			throw new JavassistBuildException(e);
		}
		
		final ClassMap map = new ClassMap();
		map.put("org/bukkit/craftbukkit/v1_13_R1/CraftLootTable", "org/bukkit/craftbukkit/v1_13_R2/CraftLootTable");
		map.put("org/bukkit/craftbukkit/v1_13_R1/CraftParticle", "org/bukkit/craftbukkit/v1_13_R2/CraftParticle");
		map.put("org/bukkit/craftbukkit/v1_13_R1/CraftServer", "org/bukkit/craftbukkit/v1_13_R2/CraftServer");
		map.put("org/bukkit/craftbukkit/v1_13_R1/CraftSound", "org/bukkit/craftbukkit/v1_13_R2/CraftSound");
		map.put("org/bukkit/craftbukkit/v1_13_R1/CraftWorld", "org/bukkit/craftbukkit/v1_13_R2/CraftWorld");
		map.put("org/bukkit/craftbukkit/v1_13_R1/block/data/CraftBlockData", "org/bukkit/craftbukkit/v1_13_R2/block/data/CraftBlockData");
		map.put("org/bukkit/craftbukkit/v1_13_R1/command/CraftBlockCommandSender", "org/bukkit/craftbukkit/v1_13_R2/command/CraftBlockCommandSender");
		map.put("org/bukkit/craftbukkit/v1_13_R1/command/ProxiedNativeCommandSender", "org/bukkit/craftbukkit/v1_13_R2/command/ProxiedNativeCommandSender");
		map.put("org/bukkit/craftbukkit/v1_13_R1/command/VanillaCommandWrapper", "org/bukkit/craftbukkit/v1_13_R2/command/VanillaCommandWrapper");
		map.put("org/bukkit/craftbukkit/v1_13_R1/enchantments/CraftEnchantment", "org/bukkit/craftbukkit/v1_13_R2/enchantments/CraftEnchantment");
		map.put("org/bukkit/craftbukkit/v1_13_R1/entity/CraftEntity", "org/bukkit/craftbukkit/v1_13_R2/entity/CraftEntity");
		map.put("org/bukkit/craftbukkit/v1_13_R1/entity/CraftMinecartCommand", "org/bukkit/craftbukkit/v1_13_R2/entity/CraftMinecartCommand");
		map.put("org/bukkit/craftbukkit/v1_13_R1/entity/CraftPlayer", "org/bukkit/craftbukkit/v1_13_R2/entity/CraftPlayer");
		map.put("org/bukkit/craftbukkit/v1_13_R1/help/CustomHelpTopic", "org/bukkit/craftbukkit/v1_13_R2/help/CustomHelpTopic");
		map.put("org/bukkit/craftbukkit/v1_13_R1/help/SimpleHelpMap", "org/bukkit/craftbukkit/v1_13_R2/help/SimpleHelpMap");
		map.put("org/bukkit/craftbukkit/v1_13_R1/inventory/CraftItemStack", "org/bukkit/craftbukkit/v1_13_R2/inventory/CraftItemStack");
		map.put("org/bukkit/craftbukkit/v1_13_R1/potion/CraftPotionEffectType", "org/bukkit/craftbukkit/v1_13_R2/potion/CraftPotionEffectType");
		map.put("org/bukkit/craftbukkit/v1_13_R1/util/CraftChatMessage", "org/bukkit/craftbukkit/v1_13_R2/util/CraftChatMessage");
		map.put("net/minecraft/server/v1_13_R1/Advancement", "net/minecraft/server/v1_13_R2/Advancement");
		map.put("net/minecraft/server/v1_13_R1/ArgumentBlockPredicate", "net/minecraft/server/v1_13_R2/ArgumentBlockPredicate");
		map.put("net/minecraft/server/v1_13_R1/ArgumentChat", "net/minecraft/server/v1_13_R2/ArgumentChat");
		map.put("net/minecraft/server/v1_13_R1/ArgumentChatComponent", "net/minecraft/server/v1_13_R2/ArgumentChatComponent");
		map.put("net/minecraft/server/v1_13_R1/ArgumentChatFormat", "net/minecraft/server/v1_13_R2/ArgumentChatFormat");
		map.put("net/minecraft/server/v1_13_R1/ArgumentCriterionValue", "net/minecraft/server/v1_13_R2/ArgumentCriterionValue");
		map.put("net/minecraft/server/v1_13_R1/ArgumentEnchantment", "net/minecraft/server/v1_13_R2/ArgumentEnchantment");
		map.put("net/minecraft/server/v1_13_R1/ArgumentEntity", "net/minecraft/server/v1_13_R2/ArgumentEntity");
		map.put("net/minecraft/server/v1_13_R1/ArgumentEntitySummon", "net/minecraft/server/v1_13_R2/ArgumentEntitySummon");
		map.put("net/minecraft/server/v1_13_R1/ArgumentItemPredicate", "net/minecraft/server/v1_13_R2/ArgumentItemPredicate");
		map.put("net/minecraft/server/v1_13_R1/ArgumentItemStack", "net/minecraft/server/v1_13_R2/ArgumentItemStack");
		map.put("net/minecraft/server/v1_13_R1/ArgumentMathOperation", "net/minecraft/server/v1_13_R2/ArgumentMathOperation");
		map.put("net/minecraft/server/v1_13_R1/ArgumentMinecraftKeyRegistered", "net/minecraft/server/v1_13_R2/ArgumentMinecraftKeyRegistered");
		map.put("net/minecraft/server/v1_13_R1/ArgumentMobEffect", "net/minecraft/server/v1_13_R2/ArgumentMobEffect");
		map.put("net/minecraft/server/v1_13_R1/ArgumentNBTTag", "net/minecraft/server/v1_13_R2/ArgumentNBTTag");
		map.put("net/minecraft/server/v1_13_R1/ArgumentParticle", "net/minecraft/server/v1_13_R2/ArgumentParticle");
		map.put("net/minecraft/server/v1_13_R1/ArgumentPosition", "net/minecraft/server/v1_13_R2/ArgumentPosition");
		map.put("net/minecraft/server/v1_13_R1/ArgumentProfile", "net/minecraft/server/v1_13_R2/ArgumentProfile");
		map.put("net/minecraft/server/v1_13_R1/ArgumentRotation", "net/minecraft/server/v1_13_R2/ArgumentRotation");
		map.put("net/minecraft/server/v1_13_R1/ArgumentRotationAxis", "net/minecraft/server/v1_13_R2/ArgumentRotationAxis");
		map.put("net/minecraft/server/v1_13_R1/ArgumentScoreboardCriteria", "net/minecraft/server/v1_13_R2/ArgumentScoreboardCriteria");
		map.put("net/minecraft/server/v1_13_R1/ArgumentScoreboardObjective", "net/minecraft/server/v1_13_R2/ArgumentScoreboardObjective");
		map.put("net/minecraft/server/v1_13_R1/ArgumentScoreboardSlot", "net/minecraft/server/v1_13_R2/ArgumentScoreboardSlot");
		map.put("net/minecraft/server/v1_13_R1/ArgumentScoreboardTeam", "net/minecraft/server/v1_13_R2/ArgumentScoreboardTeam");
		map.put("net/minecraft/server/v1_13_R1/ArgumentScoreholder", "net/minecraft/server/v1_13_R2/ArgumentScoreholder");
		map.put("net/minecraft/server/v1_13_R1/ArgumentTag", "net/minecraft/server/v1_13_R2/ArgumentTag");
		map.put("net/minecraft/server/v1_13_R1/ArgumentTile", "net/minecraft/server/v1_13_R2/ArgumentTile");
		map.put("net/minecraft/server/v1_13_R1/ArgumentVec2", "net/minecraft/server/v1_13_R2/ArgumentVec2");
		map.put("net/minecraft/server/v1_13_R1/ArgumentVec3", "net/minecraft/server/v1_13_R2/ArgumentVec3");
		map.put("net/minecraft/server/v1_13_R1/BlockPosition", "net/minecraft/server/v1_13_R2/BlockPosition");
		map.put("net/minecraft/server/v1_13_R1/CommandListenerWrapper", "net/minecraft/server/v1_13_R2/CommandListenerWrapper");
		map.put("net/minecraft/server/v1_13_R1/CompletionProviders", "net/minecraft/server/v1_13_R2/CompletionProviders");
		map.put("net/minecraft/server/v1_13_R1/CriterionConditionValue", "net/minecraft/server/v1_13_R2/CriterionConditionValue");
		map.put("net/minecraft/server/v1_13_R1/CriterionConditionValue/c", "net/minecraft/server/v1_13_R2/CriterionConditionValue/c");
		map.put("net/minecraft/server/v1_13_R1/CustomFunction", "net/minecraft/server/v1_13_R2/CustomFunction");
		map.put("net/minecraft/server/v1_13_R1/CustomFunctionData", "net/minecraft/server/v1_13_R2/CustomFunctionData");
		map.put("net/minecraft/server/v1_13_R1/DedicatedServer", "net/minecraft/server/v1_13_R2/DedicatedServer");
		map.put("net/minecraft/server/v1_13_R1/Entity", "net/minecraft/server/v1_13_R2/Entity");
		map.put("net/minecraft/server/v1_13_R1/EntitySelector", "net/minecraft/server/v1_13_R2/EntitySelector");
		map.put("net/minecraft/server/v1_13_R1/EntityTypes", "net/minecraft/server/v1_13_R2/EntityTypes");
		map.put("net/minecraft/server/v1_13_R1/EnumDirection/EnumAxis", "net/minecraft/server/v1_13_R2/EnumDirection/EnumAxis");
		map.put("net/minecraft/server/v1_13_R1/IBlockData", "net/minecraft/server/v1_13_R2/IBlockData");
		map.put("net/minecraft/server/v1_13_R1/IChatBaseComponent/ChatSerializer", "net/minecraft/server/v1_13_R2/IChatBaseComponent/ChatSerializer");
		map.put("net/minecraft/server/v1_13_R1/ICompletionProvider", "net/minecraft/server/v1_13_R2/ICompletionProvider");
		map.put("net/minecraft/server/v1_13_R1/IVectorPosition", "net/minecraft/server/v1_13_R2/IVectorPosition");
		map.put("net/minecraft/server/v1_13_R1/ItemStack", "net/minecraft/server/v1_13_R2/ItemStack");
		map.put("net/minecraft/server/v1_13_R1/LootTable", "net/minecraft/server/v1_13_R2/LootTable");
		map.put("net/minecraft/server/v1_13_R1/LootTableRegistry", "net/minecraft/server/v1_13_R2/LootTableRegistry");
		map.put("net/minecraft/server/v1_13_R1/MinecraftKey", "net/minecraft/server/v1_13_R2/MinecraftKey");
		map.put("net/minecraft/server/v1_13_R1/MinecraftServer", "net/minecraft/server/v1_13_R2/MinecraftServer");
		map.put("net/minecraft/server/v1_13_R1/MobEffectList", "net/minecraft/server/v1_13_R2/MobEffectList");
		map.put("net/minecraft/server/v1_13_R1/ParticleParam", "net/minecraft/server/v1_13_R2/ParticleParam");
		map.put("net/minecraft/server/v1_13_R1/ParticleParamBlock", "net/minecraft/server/v1_13_R2/ParticleParamBlock");
		map.put("net/minecraft/server/v1_13_R1/ParticleParamItem", "net/minecraft/server/v1_13_R2/ParticleParamItem");
		map.put("net/minecraft/server/v1_13_R1/ParticleParamRedstone", "net/minecraft/server/v1_13_R2/ParticleParamRedstone");
		map.put("net/minecraft/server/v1_13_R1/ShapeDetectorBlock", "net/minecraft/server/v1_13_R2/ShapeDetectorBlock");
		map.put("net/minecraft/server/v1_13_R1/Vec2F", "net/minecraft/server/v1_13_R2/Vec2F");
		map.put("net/minecraft/server/v1_13_R1/Vec3D", "net/minecraft/server/v1_13_R2/Vec3D");

		try {
			CtClass nms_1_13 = classToTransform.getClassPool().get("dev.jorel.commandapi.nms.NMS_1_13");
			for(CtMethod method : nms_1_13.getDeclaredMethods()) {
				try {
					classToTransform.getDeclaredMethod(method.getName());
				} catch(NotFoundException e) {
					if(method.getName().equals("compatibleVersions")) {
						continue;
					}
					getLogger().error("Adding method " + method.getName());
					try {
						classToTransform.addMethod(new CtMethod(method, classToTransform, map));
					} catch(CannotCompileException e1) {
//						getLogger().error("FAILED TO ADD method " + method.getName());
						e1.printStackTrace();
					}
				}
			}
			
			classToTransform.getClassFile().getConstPool().renameClass("org/bukkit/craftbukkit/v1_18_R2/CraftLootTable", "org/bukkit/craftbukkit/v1_18_R1/CraftLootTable");
			classToTransform.getClassFile().getConstPool().renameClass("net/minecraft/server/v1_13_R1/CommandListenerWrapper", "net/minecraft/server/v1_13_R2/CommandListenerWrapper");
			
			//classToTransform.getClassPool().get("org.bukkit.craftbukkit.v1_18_R2.CraftLootTable").getClass().getClassLoader()
//			classToTransform.getClassPool().removeClassPath();
			getLogger().error("AAAAAAAAA\n\n" + classToTransform.getClassPool().get("org.bukkit.craftbukkit.v1_18_R1.CraftLootTable") + "\n\n\nAAAAAAAAAA");
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			CtMethod compatibleVersionsMethod = CtNewMethod.make("""
					public String[] compatibleVersions() {
						return new String[] { %s };
					}
					""".formatted(versions), classToTransform);
			classToTransform.addMethod(compatibleVersionsMethod);
		} catch (CannotCompileException e) {
			throw new JavassistBuildException(e);
		}
	}

	@Override
	public void configure(final Properties properties) {
		if (properties == null) {
			return;
		}
	}
}
