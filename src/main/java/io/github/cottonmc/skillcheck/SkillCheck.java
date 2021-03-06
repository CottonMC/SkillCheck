package io.github.cottonmc.skillcheck;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.cottonmc.cotton.config.ConfigManager;
import io.github.cottonmc.cottonrpg.CottonRPG;
import io.github.cottonmc.cottonrpg.data.rpgclass.CharacterClass;
import io.github.cottonmc.cottonrpg.data.rpgresource.CharacterResource;
import io.github.cottonmc.cottonrpg.data.rpgresource.SimpleCharacterResource;
import io.github.cottonmc.skillcheck.api.dice.Dice;
import io.github.cottonmc.skillcheck.api.dice.RollResult;
import io.github.cottonmc.skillcheck.container.CharacterSheetContainer;
import io.github.cottonmc.skillcheck.events.PlayerAttackEvent;
import io.github.cottonmc.skillcheck.events.PlayerStealEvent;
import io.github.cottonmc.skillcheck.impl.SkillCheckCharacterClass;
import io.github.cottonmc.skillcheck.impl.ThiefCharacterClass;
import io.github.cottonmc.skillcheck.item.CharacterSheetItem;
import io.github.cottonmc.skillcheck.item.ClassPrestigeItem;
import io.github.cottonmc.skillcheck.item.ClassScrollItem;
import io.github.cottonmc.skillcheck.util.SkillCheckConfig;
import io.github.cottonmc.skillcheck.util.SkillCheckNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SkillCheck implements ModInitializer {
	public static SkillCheckConfig config;
	public static final String MOD_ID = "skillcheck";

	public static Item CHARACTER_SHEET;
	public static final ItemGroup SKILLCHECK_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "skillcheck_group"), () -> new ItemStack(CHARACTER_SHEET));

	public static final Tag<Block> SLIPPERY_BLOCKS = TagRegistry.block(new Identifier(MOD_ID, "slippery"));

	public static final CharacterClass ARTISAN = Registry.register(CottonRPG.CLASSES, new Identifier(MOD_ID, "artisan"), new SkillCheckCharacterClass(5));
	public static final CharacterClass BRAWLER = Registry.register(CottonRPG.CLASSES, new Identifier(MOD_ID, "brawler"), new SkillCheckCharacterClass(10));
	public static final CharacterClass THIEF = Registry.register(CottonRPG.CLASSES, new Identifier(MOD_ID, "thief"), new ThiefCharacterClass(5));

	public static final CharacterResource STAMINA = Registry.register(CottonRPG.RESOURCES, new Identifier(MOD_ID, "stamina"),
		new SimpleCharacterResource(10, 10, 25, 2, 0x00C6FF, CharacterResource.ResourceVisibility.HUD));

	public static final Item BRAWLER_SCROLL = register("brawler_scroll", new ClassScrollItem(BRAWLER));
	public static final Item WEAVER_SCROLL = register("artisan_scroll", new ClassScrollItem(ARTISAN));
	public static final Item THIEF_SCROLL = register("thief_scroll", new ClassScrollItem(THIEF));
	public static final Item PRESTIGE = register("class_prestige", new ClassPrestigeItem());

	public static final Identifier CHARACTER_SHEET_CONTAINER = new Identifier(MOD_ID, "character_sheet");

	public static Item register(String name, Item item) {
		Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), item);
		return item;
	}

	@Override
	public void onInitialize() {
		SkillCheckNetworking.init();
		//to prevent forward reference issue
		CHARACTER_SHEET = register("character_sheet", new CharacterSheetItem());
		config = ConfigManager.loadConfig(SkillCheckConfig.class);
		AttackEntityCallback.EVENT.register(new PlayerAttackEvent());
		UseEntityCallback.EVENT.register(new PlayerStealEvent());

		ContainerProviderRegistry.INSTANCE.registerFactory(CHARACTER_SHEET_CONTAINER, (syncId, id, player, buf) -> new CharacterSheetContainer(syncId, player));

		//register a /roll command
		CommandRegistrationCallback.EVENT.register((dispatcher, b) -> dispatcher.register((
			CommandManager.literal("roll")
				.then(CommandManager.argument("formula", StringArgumentType.word())
					.executes(context -> {
						String formula = context.getArgument("formula", String.class);
						RollResult result;
						try {
							result = Dice.roll(formula);
						} catch (IllegalArgumentException e) {
							context.getSource().sendError(new LiteralText(e.getMessage()));
							return -1;
						}
						if (result.isCritFail())
							context.getSource().sendFeedback(new TranslatableText("msg.skillcheck.roll.fail", result.getFormattedNaturals()), false);
						else
							context.getSource().sendFeedback(new TranslatableText("msg.skillcheck.roll.result", result.getTotal(), result.getFormattedNaturals()), false);
						return 1;
					})))));
	}

	public static List<CharacterClass> getCharSheetClasses() {
		List<CharacterClass> ret = new ArrayList<>();
		for (CharacterClass c : CottonRPG.CLASSES) {
			if (c instanceof SkillCheckCharacterClass) ret.add(c);
		}
		ret.sort(Comparator.comparing(c -> c.getId().toString()));
		return ret;
	}

}
