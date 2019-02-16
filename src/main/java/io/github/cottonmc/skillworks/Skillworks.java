package io.github.cottonmc.skillworks;

import io.github.cottonmc.skillworks.traits.BooleanTrait;
import io.github.cottonmc.skillworks.traits.FloatTrait;
import me.elucent.earlgray.api.TraitEntry;
import me.elucent.earlgray.api.TraitRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Skillworks implements ModInitializer {
    public static SkillworksConfig config;

    public static TraitEntry<FloatTrait> FISTICUFFS = (TraitEntry<FloatTrait>) TraitRegistry.register(new Identifier("skillworks", "fisticuffs"), FloatTrait.class);
    public static TraitEntry<BooleanTrait> WEAVER = (TraitEntry<BooleanTrait>) TraitRegistry.register(new Identifier("skillworks", "weaver"), BooleanTrait.class);

    public static Item UNARMED__ZERO = register("unarmed_zero_debug", new TraitModItem( 0));
    public static Item UNARMED__FIVE = register("unarmed_five_debug", new TraitModItem(5));
    public static Item WEAVER_TRUE = register("weaver_true", new TraitModItem(true));
    public static Item WEAVER_FALSE = register("weaver_false", new TraitModItem(false));
    
    public static Item register(String name, Item item) {
        Registry.register(Registry.ITEM, "skillworks:" + name, item);
        return item;
    }

    @Override
    public void onInitialize() {
        config = ConfigManager.load(SkillworksConfig.class);
        TraitRegistry.addInherent(PlayerEntity.class, (PlayerEntity e) -> new FloatTrait());
    }
}
