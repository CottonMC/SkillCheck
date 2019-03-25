package io.github.cottonmc.skillworks;

import io.github.cottonmc.skillworks.events.PlayerAttackEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Skillworks implements ModInitializer {
    public static SkillworksConfig config;

    public static Item BASE_SCROLL;

    public static final ItemGroup SKILLWORKS_GROUP = FabricItemGroupBuilder.build(new Identifier("skillworks:skillworks_group"), () -> new ItemStack(BASE_SCROLL));

    public static final Tag<Block> SLIPPERY_BLOCKS = TagRegistry.block(new Identifier("skillworks", "slippery"));

    public static Identifier BRAWLER = new Identifier("skillworks", "brawler");
    public static Identifier WEAVER = new Identifier("skillworks", "weaver");
    public static Identifier GYMNIST = new Identifier("skillworks", "gymnist");

    public static Item BRAWLER_SCROLL = register("brawler_scroll", new ClassScrollItem(BRAWLER));
    public static Item WEAVER_SCROLL = register("weaver_scroll", new ClassScrollItem(WEAVER));
    public static Item GYMNIST_SCROLL = register("gymnist_scroll", new ClassScrollItem(GYMNIST));
    public static Item PRESTIGE = register("class_prestige", new TraitPrestigeItem());

    public static Item register(String name, Item item) {
        Registry.register(Registry.ITEM, "skillworks:" + name, item);
        return item;
    }

    @Override
    public void onInitialize() {
        //to prevent forward reference issue
        BASE_SCROLL = register("base_scroll", new Item(new Item.Settings().itemGroup(SKILLWORKS_GROUP)));
        config = ConfigManager.load(SkillworksConfig.class);
        AttackEntityCallback.EVENT.register(PlayerAttackEvent.onPlayerAttack);
    }
}
