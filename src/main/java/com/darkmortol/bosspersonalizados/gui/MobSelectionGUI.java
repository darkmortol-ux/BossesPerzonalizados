package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.BossKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MobSelectionGUI {

    // Ender Dragon y Wither quedan afuera: se crean con /boss crear <nombre> ENDER_DRAGON|WITHER
    private static final EntityType[] MOBS_HOSTILES = {
            EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIE_VILLAGER,
            EntityType.SKELETON, EntityType.STRAY, EntityType.BOGGED,
            EntityType.CREEPER, EntityType.SPIDER, EntityType.CAVE_SPIDER,
            EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.SILVERFISH,
            EntityType.WITCH, EntityType.SLIME, EntityType.MAGMA_CUBE,
            EntityType.BLAZE, EntityType.GHAST, EntityType.PHANTOM,
            EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER, EntityType.VEX, EntityType.RAVAGER,
            EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.SHULKER,
            EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.HOGLIN, EntityType.ZOGLIN,
            EntityType.WARDEN, EntityType.BREEZE
    };

    public static Inventory build() {
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.MOB_SELECTION);
        Inventory inv = Bukkit.createInventory(holder, 54, Component.text("Elige el mob del boss", NamedTextColor.DARK_RED));
        holder.setInventory(inv);

        int slot = 0;
        for (EntityType tipo : MOBS_HOSTILES) {
            Material huevo = huevoDe(tipo);
            if (huevo == null) continue;
            ItemStack item = new ItemBuilder(huevo)
                    .nombre(nombreLegible(tipo), NamedTextColor.YELLOW)
                    .lore(List.of("Click para elegir este mob"), NamedTextColor.GRAY)
                    .pdcString(BossKeys.ENTITY_TYPE, tipo.name())
                    .build();
            inv.setItem(slot++, item);
            if (slot >= 45) break;
        }

        inv.setItem(49, new ItemBuilder(Material.BARRIER).nombre("Cancelar", NamedTextColor.RED).build());
        return inv;
    }

    private static Material huevoDe(EntityType tipo) {
        try {
            return Material.valueOf(tipo.name() + "_SPAWN_EGG");
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String nombreLegible(EntityType tipo) {
        String[] partes = tipo.name().toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String parte : partes) sb.append(Character.toUpperCase(parte.charAt(0))).append(parte.substring(1)).append(" ");
        return sb.toString().trim();
    }
}
