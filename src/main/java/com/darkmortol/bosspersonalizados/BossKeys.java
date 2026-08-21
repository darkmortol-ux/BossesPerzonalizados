package com.darkmortol.bosspersonalizados;

import org.bukkit.NamespacedKey;

public final class BossKeys {

    private BossKeys() {}

    public static NamespacedKey ENTITY_TYPE;
    public static NamespacedKey ABILITY_TYPE;
    public static NamespacedKey SLOT;
    public static NamespacedKey MATERIAL_INDEX;
    public static NamespacedKey ENCHANT_KEY;
    public static NamespacedKey SPAWN_EGG_BOSS_ID;
    public static NamespacedKey BOSS_INSTANCE_ID;

    public static void inicializar(BossPersonalizadosPlugin plugin) {
        ENTITY_TYPE = new NamespacedKey(plugin, "entity-type");
        ABILITY_TYPE = new NamespacedKey(plugin, "ability-type");
        SLOT = new NamespacedKey(plugin, "slot");
        MATERIAL_INDEX = new NamespacedKey(plugin, "material-index");
        ENCHANT_KEY = new NamespacedKey(plugin, "enchant-key");
        SPAWN_EGG_BOSS_ID = new NamespacedKey(plugin, "spawn-egg-boss-id");
        BOSS_INSTANCE_ID = new NamespacedKey(plugin, "boss-instance-id");
    }
}
