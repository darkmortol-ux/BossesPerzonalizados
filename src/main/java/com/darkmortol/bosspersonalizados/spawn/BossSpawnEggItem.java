package com.darkmortol.bosspersonalizados.spawn;

import com.darkmortol.bosspersonalizados.BossKeys;
import com.darkmortol.bosspersonalizados.gui.ItemBuilder;
import com.darkmortol.bosspersonalizados.model.BossDefinition;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BossSpawnEggItem {

    public static ItemStack crear(BossDefinition def) {
        Material material = huevoDe(def.getTipoMob());
        return new ItemBuilder(material)
                .nombre("Huevo de Boss: " + def.getNombre() + " #" + def.getId(), NamedTextColor.LIGHT_PURPLE)
                .lore(List.of(
                        "Click derecho sobre un bloque",
                        "para crear el punto de aparición",
                        "de este boss.",
                        "",
                        "Mob: " + def.getTipoMob().name(),
                        "Vida: " + def.getVida(),
                        "Daño: " + def.getDano()
                ), NamedTextColor.GRAY)
                .pdcInt(BossKeys.SPAWN_EGG_BOSS_ID, def.getId())
                .build();
    }

    private static Material huevoDe(EntityType tipo) {
        try {
            return Material.valueOf(tipo.name() + "_SPAWN_EGG");
        } catch (IllegalArgumentException e) {
            // Ender Dragon y Wither no tienen huevo: usamos un item representativo.
            return tipo == EntityType.ENDER_DRAGON ? Material.DRAGON_EGG : Material.WITHER_SKELETON_SKULL;
        }
    }
}
