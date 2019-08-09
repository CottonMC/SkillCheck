package io.github.cottonmc.skillcheck;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.cottonmc.cotton.config.ConfigManager;
import io.github.cottonmc.cottonrpg.CottonRPG;
import io.github.cottonmc.cottonrpg.data.CharacterClass;
import io.github.cottonmc.skillcheck.api.classes.PlayerClassType;
import io.github.cottonmc.skillcheck.classes.SimpleClassType;
import io.github.cottonmc.skillcheck.container.CharacterSheetContainer;
import io.github.cottonmc.skillcheck.events.PlayerAttackEvent;
import io.github.cottonmc.skillcheck.events.PlayerStealEvent;
import io.github.cottonmc.skillcheck.api.dice.Dice;
import io.github.cottonmc.skillcheck.api.dice.RollResult;
import io.github.cottonmc.skillcheck.util.CharSheetClass;
import io.github.cottonmc.skillcheck.util.SkillCheckConfig;
import io.github.cottonmc.skillcheck.util.SkillCheckNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.registry.CommandRegistry;
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
import net.minecraft.util.registry.SimpleRegistry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class SkillCheck implements ModInitializer {
    public static SkillCheckConfig config;
    public static final String MOD_ID = "skillcheck";

    public static Item CHARACTER_SHEET;
    public static final ItemGroup SKILLCHECK_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "skillcheck_group"), () -> new ItemStack(CHARACTER_SHEET));

    public static final Tag<Block> SLIPPERY_BLOCKS = TagRegistry.block(new Identifier(MOD_ID, "slippery"));

    @Deprecated
    public static final Registry<PlayerClassType> PLAYER_CLASS_TYPES = new SimpleRegistry<>();

    public static PlayerClassType OLD_BRAWLER = Registry.register(PLAYER_CLASS_TYPES, new Identifier(MOD_ID, "brawler"), new SimpleClassType(10));
    public static PlayerClassType OLD_ARTISAN = Registry.register(PLAYER_CLASS_TYPES, new Identifier(MOD_ID, "artisan"), new SimpleClassType(5));
    public static PlayerClassType OLD_THIEF = Registry.register(PLAYER_CLASS_TYPES, new Identifier(MOD_ID, "thief"), new SimpleClassType(5));

    public static final Identifier ARTISAN_ID = new Identifier(MOD_ID, "artisan");
    public static final Identifier BRAWLER_ID = new Identifier(MOD_ID, "brawler");
    public static final Identifier THIEF_ID = new Identifier(MOD_ID, "thief");

    public static CharacterClass ARTISAN = Registry.register(CottonRPG.CLASSES, ARTISAN_ID, new SkillCheckCharClass(5));
    public static CharacterClass BRAWLER = Registry.register(CottonRPG.CLASSES, BRAWLER_ID, new SkillCheckCharClass(10));
    public static CharacterClass THIEF = Registry.register(CottonRPG.CLASSES, THIEF_ID, new SkillCheckCharClass(5));

    public static Item BRAWLER_SCROLL = register("brawler_scroll", new ClassScrollItem(BRAWLER));
    public static Item WEAVER_SCROLL = register("artisan_scroll", new ClassScrollItem(ARTISAN));
    public static Item THIEF_SCROLL = register("thief_scroll", new ClassScrollItem(THIEF));
    public static Item PRESTIGE = register("class_prestige", new TraitPrestigeItem());

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
        AttackEntityCallback.EVENT.register(PlayerAttackEvent.onPlayerAttack);
        UseEntityCallback.EVENT.register(PlayerStealEvent.onPlayerInteract);

        ContainerProviderRegistry.INSTANCE.registerFactory(CHARACTER_SHEET_CONTAINER, (syncId, id, player, buf) -> new CharacterSheetContainer(syncId, player));

        //register a /roll command
        CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register((
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
                            if (result.isCritFail()) context.getSource().sendFeedback(new TranslatableText("msg.skillcheck.roll.fail", result.getFormattedNaturals()), false);
                            else context.getSource().sendFeedback(new TranslatableText("msg.skillcheck.roll.result", result.getTotal(), result.getFormattedNaturals()), false);
                            return 1;
                        })))));
    }

    public static List<Identifier> getCharSheetClasses() {
	  Set<Identifier> allIds = CottonRPG.CLASSES.getIds();
	  List<Identifier> ret = new ArrayList<>();
	  for (Identifier id : allIds) {
	    if (CottonRPG.CLASSES.get(id) instanceof CharSheetClass) ret.add(id);
	  }
	  ret.sort(Comparator.comparing(Identifier::getPath));
	  return ret;
	}

}
