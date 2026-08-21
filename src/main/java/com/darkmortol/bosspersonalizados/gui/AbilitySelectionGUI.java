package com.darkmortol.bosspersonalizados.gui;

import com.darkmortol.bosspersonalizados.BossKeys;
import com.darkmortol.bosspersonalizados.ability.AbilityType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AbilitySelectionGUI {

    public static Inventory build(WizardSession sesion) {
        BossGuiHolder holder = new BossGuiHolder(BossGuiHolder.GuiType.ABILITY_SELECTION);
        Inventory inv = Bukkit.createInventory(holder, 54, Component.text("Elige las habilidades (opcional)", NamedTextColor.DARK_PURPLE));
        holder.setInventory(inv);

        int slot = 0;
        for (AbilityType tipo : AbilityType.values()) {
            boolean elegida = sesion.getDefinicion().getHabilidades().contains(tipo);
            ItemBuilder builder = new ItemBuilder(tipo.getIcono())
                    .nombre((elegida ? "✔ " : "") + tipo.getNombre(), elegida ? NamedTextColor.GREEN : NamedTextColor.YELLOW)
                    .lore(List.of(tipo.getDescripcion(), "Categoría: " + tipo.getCategoria().name(), "", elegida ? "Click para quitar" : "Click para seleccionar"), NamedTextColor.GRAY)
                    .pdcString(BossKeys.ABILITY_TYPE, tipo.name());
            if (elegida) builder.brillo();
            inv.setItem(slot++, builder.build());
        }

        inv.setItem(48, new ItemBuilder(Material.ARROW).nombre("Atrás", NamedTextColor.GRAY).build());
        inv.setItem(49, new ItemBuilder(Material.BARRIER).nombre("Cancelar", NamedTextColor.RED).build());
        inv.setItem(50, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).nombre("Siguiente ▶", NamedTextColor.GREEN).build());
        return inv;
    }
}
