package io.github.cottonmc.skillworks;

import io.github.cottonmc.skillworks.traits.ClassTrait;
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
    public static TraitEntry<ClassTrait> WEAVER = (TraitEntry<ClassTrait>) TraitRegistry.register(new Identifier("skillworks", "weaver"), ClassTrait.class);
    public static TraitEntry<ClassTrait> GYMNIST = (TraitEntry<ClassTrait>) TraitRegistry.register(new Identifier("skillworks", "gymnist"), ClassTrait.class);

    public static Item FISTICUFFS_FIVE = register("fisticuffs_five", new TraitModItem(new Identifier("skillworks", "fisticuffs"), 5f));
    public static Item CLASS_WEAVER = register("class_weaver", new TraitModItem(new Identifier("skillworks", "weaver")));
    public static Item CLASS_GYMNIST = register("class_gymnist", new TraitModItem(new Identifier("skillworks", "gymnist")));
    public static Item PRESTIGE = register("class_prestige", new TraitPrestigeItem());

    public static Item register(String name, Item item) {
        Registry.register(Registry.ITEM, "skillworks:" + name, item);
        return item;
    }

    @Override
    public void onInitialize() {
        config = ConfigManager.load(SkillworksConfig.class);
        TraitRegistry.addInherent(PlayerEntity.class, (PlayerEntity e) -> new FloatTrait());
        if (config.disableClasses) TraitRegistry.addInherent(PlayerEntity.class, (PlayerEntity e) -> new ClassTrait());
    }
}
