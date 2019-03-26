package io.github.cottonmc.skillworks;

import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.cottonmc.cotton.config.ConfigManager;
import io.github.cottonmc.skillworks.events.PlayerAttackEvent;
import io.github.cottonmc.skillworks.events.PlayerStealEvent;
import io.github.cottonmc.skillworks.util.Dice;
import io.github.cottonmc.skillworks.util.DiceResult;
import io.github.cottonmc.skillworks.util.SkillworksConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.tag.Tag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Skillworks implements ModInitializer {
    public static SkillworksConfig config;

    public static Item BASE_SCROLL;

    public static final ItemGroup SKILLWORKS_GROUP = FabricItemGroupBuilder.build(new Identifier("skillworks:skillworks_group"), () -> new ItemStack(BASE_SCROLL));

    public static final Tag<Block> SLIPPERY_BLOCKS = TagRegistry.block(new Identifier("skillworks", "slippery"));

    public static Identifier BRAWLER = new Identifier("skillworks", "brawler");
    public static Identifier WEAVER = new Identifier("skillworks", "weaver");
    public static Identifier THIEF = new Identifier("skillworks", "thief");

    public static Item BRAWLER_SCROLL = register("brawler_scroll", new ClassScrollItem(BRAWLER));
    public static Item WEAVER_SCROLL = register("weaver_scroll", new ClassScrollItem(WEAVER));
    public static Item THIEF_SCROLL = register("thief_scroll", new ClassScrollItem(THIEF));
    public static Item PRESTIGE = register("class_prestige", new TraitPrestigeItem());

    public static Item register(String name, Item item) {
        Registry.register(Registry.ITEM, "skillworks:" + name, item);
        return item;
    }

    @Override
    public void onInitialize() {
        //to prevent forward reference issue
        BASE_SCROLL = register("base_scroll", new Item(new Item.Settings().itemGroup(SKILLWORKS_GROUP)));
        config = ConfigManager.loadConfig(SkillworksConfig.class);
        AttackEntityCallback.EVENT.register(PlayerAttackEvent.onPlayerAttack);
        UseEntityCallback.EVENT.register(PlayerStealEvent.onPlayerInteract);

        //register a /roll command
        CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register((
                ServerCommandManager.literal("roll")
                        .then(ServerCommandManager.argument("formula", StringArgumentType.word())
                        .executes(context -> {
                            String formula = context.getArgument("formula", String.class);
                            DiceResult result;
                            try {
                                result = Dice.roll(formula);
                            } catch (IllegalArgumentException e) {
                                context.getSource().sendError(new StringTextComponent(e.getMessage()));
                                return -1;
                            }
                            if (result.isCritFail()) context.getSource().sendFeedback(new TranslatableTextComponent("msg.skillworks.roll.fail", result.getFormattedNaturals()), false);
                            else context.getSource().sendFeedback(new TranslatableTextComponent("msg.skillworks.roll.result", result.getTotal(), result.getFormattedNaturals()), false);
                            return 1;
                        })))));
    }

}
