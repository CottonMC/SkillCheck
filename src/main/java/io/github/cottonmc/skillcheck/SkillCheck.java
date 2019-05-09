package io.github.cottonmc.skillcheck;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.cottonmc.cotton.config.ConfigManager;
import io.github.cottonmc.skillcheck.api.traits.ClassManager;
import io.github.cottonmc.skillcheck.container.CharacterSheetContainer;
import io.github.cottonmc.skillcheck.events.PlayerAttackEvent;
import io.github.cottonmc.skillcheck.events.PlayerStealEvent;
import io.github.cottonmc.skillcheck.api.dice.Dice;
import io.github.cottonmc.skillcheck.api.dice.RollResult;
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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SkillCheck implements ModInitializer {
    public static SkillCheckConfig config;
    public static final String MOD_ID = "skillcheck";

    public static Item CHARACTER_SHEET;
    public static final ItemGroup SKILLCHECK_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "skillcheck_group"), () -> new ItemStack(CHARACTER_SHEET));

    public static final Tag<Block> SLIPPERY_BLOCKS = TagRegistry.block(new Identifier(MOD_ID, "slippery"));

    public static Identifier BRAWLER = ClassManager.registerClass(new Identifier(MOD_ID, "brawler"), 10);
    public static Identifier ARTISAN = ClassManager.registerClass(new Identifier(MOD_ID, "artisan"), 5);
    public static Identifier THIEF = ClassManager.registerClass(new Identifier(MOD_ID, "thief"), 5);

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
                                context.getSource().sendError(new TextComponent(e.getMessage()));
                                return -1;
                            }
                            if (result.isCritFail()) context.getSource().sendFeedback(new TranslatableComponent("msg.skillcheck.roll.fail", result.getFormattedNaturals()), false);
                            else context.getSource().sendFeedback(new TranslatableComponent("msg.skillcheck.roll.result", result.getTotal(), result.getFormattedNaturals()), false);
                            return 1;
                        })))));
    }

}
